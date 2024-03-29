/*
 * JOCL - Java bindings for OpenCL
 *
 * Copyright 2009-2019 Marco Hutter - http://www.jocl.org/
 */
package net.csibio.aird.opencl;

import lombok.Data;
import org.jocl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.jocl.CL.*;

/**
 * Using GPU for XIC
 */
public class XIC {

    /**
     * The OpenCL context
     */
    private static cl_context context;

    /**
     * The OpenCL command queue to which the all work will be dispatched
     */
    private static cl_command_queue commandQueue;

    /**
     * The OpenCL program containing the reduction kernel
     */
    private static cl_program program;

    /**
     * The OpenCL kernel that performs the reduction
     */
    private static cl_kernel kernel;

    /**
     * 在单张光谱图中查找多个目标mz
     *
     * @param pairsList 多张光谱图
     * @param targets   多个目标值
     * @param mzWindow  目标窗口
     * @return 目标值结果
     */
    public static double[] lowerBoundWithGPU(List<net.csibio.aird.bean.common.Spectrum> pairsList, double[] targets, double mzWindow) {

        int countInBatch = pairsList.size();
        double[] results = new double[targets.length * countInBatch];

        // Allocate the memory objects for the input- and output data
        cl_mem targetsMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_double * targets.length, Pointer.to(targets), null);

        List<GpuSpectrum> paramsList = new ArrayList<>();
        for (int i = 0; i < countInBatch; i++) {
            double[] mzArray = pairsList.get(i).getMzs();
            if (mzArray.length == 0) {
                paramsList.add(null);
            } else {
                paramsList.add(new GpuSpectrum(clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_double * pairsList.get(i).getMzs().length, Pointer.to(pairsList.get(i).getMzs()), null), clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_double * pairsList.get(i).getInts().length, Pointer.to(pairsList.get(i).getInts()), null), pairsList.get(i).getInts().length));
            }
        }

        cl_mem resultsMem = clCreateBuffer(context, CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_double * targets.length * countInBatch, Pointer.to(results), null);
        lowerBound(paramsList, targetsMem, targets.length, resultsMem, mzWindow);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, resultsMem, CL_TRUE, 0, targets.length * Sizeof.cl_double, Pointer.to(results), 0, null, null);

        // Release memory objects
        paramsList.forEach(params -> {
            if (params != null) {
                clReleaseMemObject(params.getMzArrayMem());
                clReleaseMemObject(params.getIntArrayMem());
            }
        });
        clReleaseMemObject(targetsMem);
        clReleaseMemObject(resultsMem);
        return results;
    }

    /**
     * 在单张光谱图中查找多个目标mz
     *
     * @param xicParamsList 需要压缩的光谱图数组
     * @param targetsMem    目标内存
     * @param targetsLength 目标长度
     * @param resultsMem    结果内存
     * @param mzWindow      结果内存长度
     */
    private static void lowerBound(List<GpuSpectrum> xicParamsList, cl_mem targetsMem, int targetsLength, cl_mem resultsMem, double mzWindow) {
        // Set the arguments for the kernel
        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(targetsMem));
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{targetsLength}));
        for (int i = 0; i < xicParamsList.size(); i++) {
            if (xicParamsList.get(i) != null) {
                clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(xicParamsList.get(i).getMzArrayMem()));
                clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(xicParamsList.get(i).getIntArrayMem()));
                clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{xicParamsList.get(i).getLength()}));
            } else {
                clSetKernelArg(kernel, a++, Sizeof.cl_mem, null);
                clSetKernelArg(kernel, a++, Sizeof.cl_mem, null);
                clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{0}));
            }
        }

        clSetKernelArg(kernel, a++, Sizeof.cl_double, Pointer.to(new double[]{mzWindow}));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(resultsMem));

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, new long[]{targetsLength}, null, 0, null, null);
    }

    /**
     * 使用CPU计算
     *
     * @param array   需要搜索的数组
     * @param targets 需要搜索的目标
     * @return 返回搜索到的坐标索引
     */
    private static int[] lowerBoundWithCPU(double[] array, double[] targets) {
        int[] results = new int[targets.length];
        for (int i = 0; i < targets.length; i++) {
            results[i] = lowerBound(array, targets[i]);
        }
        return results;
    }

    /**
     * Initialize a default OpenCL context, command queue, program and kernel
     *
     * @param countInBatch 分多少个批次进行
     */
    public static void initialize(int countInBatch) {
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 2;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();

        //   commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null);
        commandQueue = clCreateCommandQueue(context, device, 0, null);

        // Create the program from the source code
        String programSource = readFile("src/main/resources/clkernel/XICKernel" + countInBatch + ".cpp");
        program = clCreateProgramWithSource(context, 1, new String[]{programSource}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        kernel = clCreateKernel(program, "lowerBound", null);
    }

    /**
     * Shut down and release all resources that have been allocated
     */
    public static void shutdown() {
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }

    /**
     * Read the contents of the file with the given name, and return it as a string
     *
     * @param fileName The name of the file to read
     * @return The contents of the file
     */
    private static String readFile(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * lower bound search
     *
     * @param array  search array
     * @param target target value
     * @return target value's index
     */
    public static int lowerBound(double[] array, Double target) {
        int rightIndex = array.length - 1;

        if (target <= array[0]) {
            return 0;
        }
        if (target >= array[rightIndex]) {
            return -1;
        }

        int leftIndex = 0;
        while (leftIndex + 1 < rightIndex) {
            int tmp = (leftIndex + rightIndex) >>> 1;
            if (target < array[tmp]) {
                rightIndex = tmp;
            } else if (target > array[tmp]) {
                leftIndex = tmp;
            } else {
                return tmp;
            }
        }

        return rightIndex;
    }

    /**
     * Spectrum Object for GPU computation
     */
    @Data
    public static class GpuSpectrum {

        cl_mem mzArrayMem;
        cl_mem intArrayMem;
        int length;

        /**
         * 构造函数
         */
        public GpuSpectrum() {
        }

        /**
         * 构造函数
         *
         * @param mzArrayMem  mz array mem object
         * @param intArrayMem intensity array mem object
         * @param length      the total length
         */
        public GpuSpectrum(cl_mem mzArrayMem, cl_mem intArrayMem, int length) {
            this.mzArrayMem = mzArrayMem;
            this.intArrayMem = intArrayMem;
            this.length = length;
        }
    }
}
