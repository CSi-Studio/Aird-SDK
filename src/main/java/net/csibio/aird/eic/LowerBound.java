/*
 * JOCL - Java bindings for OpenCL
 *
 * Copyright 2009-2019 Marco Hutter - http://www.jocl.org/
 */
package net.csibio.aird.eic;

import org.jocl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import static org.jocl.CL.*;

/**
 * A sample showing a simple reduction with JOCL
 */
public class LowerBound {
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

    public static int[] doSearch(float[] array, float[] targets) {
        initialize();
        int[] result = lowerBoundWithGPU(array, targets);
        shutdown();
        return result;

    }

    public static int[] lowerBoundWithGPU(float[] array, float[] targets) {

        int[] results = new int[targets.length];

        // Allocate the memory objects for the input- and output data
        cl_mem targetsMem = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * targets.length, Pointer.to(targets), null);
        cl_mem arrayMem = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * array.length, Pointer.to(array), null);
        cl_mem resultsMem = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_int * targets.length, Pointer.to(results), null);
        // Perform the reduction on the GPU: Each work group will 
        // perform the reduction of 'localWorkSize' elements, and
        // the results will be written into the output memory
        lowerBound(arrayMem, array.length,
                targetsMem, targets.length,
                resultsMem, targets.length);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, resultsMem, CL_TRUE, 0,
                targets.length * Sizeof.cl_float, Pointer.to(results),
                0, null, null);

        // Release memory objects
        clReleaseMemObject(arrayMem);
        clReleaseMemObject(targetsMem);
        clReleaseMemObject(resultsMem);
        return results;
    }

    private static void lowerBound(
            cl_mem arrayMem, int arrayLength,
            cl_mem targetsMem, int targetsLength,
            cl_mem resultsMem, int resultsLength) {
        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(targetsMem));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(arrayMem));
        clSetKernelArg(kernel, 2, Sizeof.cl_int, Pointer.to(new int[]{arrayLength}));
        clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(resultsMem));

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                new long[]{targetsLength}, null,
                0, null, null);
    }

    /**
     * Implementation of a Kahan summation reduction in plain Java
     *
     * @param array The input
     * @return The reduction result
     */
    private static int[] lowerBoundWithCPU(float[] array, float[] targets) {
        int[] results = new int[targets.length];
        for (int i = 0; i < targets.length; i++) {
            results[i] = lowerBound(array, targets[i]);
        }
        return results;
    }

    /**
     * Initialize a default OpenCL context, command queue, program and kernel
     */
    public static void initialize() {
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        commandQueue = clCreateCommandQueueWithProperties(
                context, device, properties, null);

        // Create the program from the source code
        String programSource = readFile("src/main/resources/clkernel/LowerBound.cl");
        program = clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        kernel = clCreateKernel(program, "lowerBound", null);
    }

    /**
     * Shut down and release all resources that have been allocated
     * in {@link #initialize()}
     */
    public static void shutdown() {
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }

    /**
     * Read the contents of the file with the given name, and return
     * it as a string
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

    public static int lowerBound(float[] array, Float target) {
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
}
