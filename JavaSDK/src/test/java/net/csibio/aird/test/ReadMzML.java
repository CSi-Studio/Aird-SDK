package net.csibio.aird.test;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.util.FileUtil;
import org.junit.Test;

import java.nio.*;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class ReadMzML {

    @Test
    public void readBase64String() {
        String mzOriString = "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";
        String mz = "              <binary>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</binary>";
        String intensityOrigin = "eJytVL9Lw0AU/qCLm4PUOaO6WHAR7BA0dwERyejo4FDQqYogFA3oIJ1cLOogBREc619gRkGEDg4uQszkJE4ODuK7JBev1ySmrRce9+7y7v343seD17JgBJHI1Z5kqJ0xHJc4/EMWnttk579GIpd4K9dTjSMwbdQdGxd3HE6V422co1tm8Hwr2f1TK9x1Hx2KIXTVp0exag8M9+/0r8LxsUp5kMhlVhm6Exw33ShHcRZ5tlvpOUpdvfP3Gb6uOEq7Nh6vbZc+bE3bGJvlwAyHd8JglCNJ8KH3lSWG9SpLzn3xCE9RU14eufkFVqad+OdQbG/7tzeI+6fmIuKHOQZWKi49/pVck3cpsVWe6PmtNf+uV31vKDUOjI+ChawdgXYv41AtjUWOnT3i5C3D1BFDhXjkCs5l8LlH3yjWRx3TNE7LO/293ps87hg53FB10cMifcjjWRq2qo0p9XMGZ45j/pPjsslhvhAvD+gurkPYCd3UYwX9uGXpLulCGpscKwscz98MnU7URze2c1OwSvOV4K3WGN87kvtB9iwpgl8Rm//S1XkTzurl4WaP1IX49Wz+yLnyly9hV2QOSqyGnQPq3O3ZB/SZlZeuj+JnxJ7/AHnYUcQ=";
        String intensity = "              <binary>eJytVL9Lw0AU/qCLm4PUOaO6WHAR7BA0dwERyejo4FDQqYogFA3oIJ1cLOogBREc619gRkGEDg4uQszkJE4ODuK7JBev1ySmrRce9+7y7v343seD17JgBJHI1Z5kqJ0xHJc4/EMWnttk579GIpd4K9dTjSMwbdQdGxd3HE6V422co1tm8Hwr2f1TK9x1Hx2KIXTVp0exag8M9+/0r8LxsUp5kMhlVhm6Exw33ShHcRZ5tlvpOUpdvfP3Gb6uOEq7Nh6vbZc+bE3bGJvlwAyHd8JglCNJ8KH3lSWG9SpLzn3xCE9RU14eufkFVqad+OdQbG/7tzeI+6fmIuKHOQZWKi49/pVck3cpsVWe6PmtNf+uV31vKDUOjI+ChawdgXYv41AtjUWOnT3i5C3D1BFDhXjkCs5l8LlH3yjWRx3TNE7LO/293ps87hg53FB10cMifcjjWRq2qo0p9XMGZ45j/pPjsslhvhAvD+gurkPYCd3UYwX9uGXpLulCGpscKwscz98MnU7URze2c1OwSvOV4K3WGN87kvtB9iwpgl8Rm//S1XkTzurl4WaP1IX49Wz+yLnyly9hV2QOSqyGnQPq3O3ZB/SZlZeuj+JnxJ7/AHnYUcQ=</binary>";
        float[] mzsOrigin = parse(mzOriString);
        double[] mzs = parse64(mz.trim().replace("<binary>", "").replace("</binary>", ""));
        float[] intsOrigin = parse(intensityOrigin);
        float[] ints = parse(intensity.trim().replace("<binary>", "").replace("</binary>", ""));


        String nonStandard = "eJztxaEBAAAMAqD5/9MLZj+AQq5i27Zt27Zt27Zt27Zt27Zt27bnD1ODBfE=";
        int[] nonStandardInts = parse64Integer(nonStandard);
        System.out.println("HelloWorld");
    }

    @Test
    public void statForFileSize() {
        statMzAirdFileSizes();
        statMzFileSizes();
        statIntensityFileSizes();
    }

    public String parentFolder = "D:\\PrecisionControl\\";
    public void statMzAirdFileSizes() {
        String[] directories = new String[]{
                "Aird\\mz3dp",
                "Aird\\mz4dp",
                "Aird\\mz5dp"
        };
        boolean printed = false;
        for (String directory : directories) {
            System.out.println("scanning " + directory + ":");
            TreeMap<String, Long> fileSizeMap = FileUtil.sumFileSizes(parentFolder+directory);
            long[] sizes = new long[fileSizeMap.size()];
            String[] fileName = new String[fileSizeMap.size()];
            int i = 0;
            for (Map.Entry<String, Long> entry : fileSizeMap.entrySet()) {
                sizes[i] = entry.getValue()/1024/1024;
                fileName[i] = entry.getKey();
                i++;
            }
            if (!printed){
                System.out.println(JSON.toJSONString(fileName));
                printed = true;
            }
            System.out.println(JSON.toJSONString(sizes));
        }
    }

    public void statMzFileSizes() {
        String[] directories = new String[]{
                "mzML64\\mz3dp",
                "mzML64\\mz4dp",
                "mzML64\\mz5dp",
                "mzML64\\mz2x5dp",
                "mzML64\\mz5x5dp",
                "mzML64\\lossless",
                "rawdata",
                "onlyIndex",
        };
        boolean printed = false;
        for (String directory : directories) {
            System.out.println("scanning " + directory + ":");
            TreeMap<String, Long> fileSizeMap = FileUtil.sumFileSizes(parentFolder+directory);
            long[] sizes = new long[fileSizeMap.size()];
            String[] fileName = new String[fileSizeMap.size()];
            int i = 0;
            for (Map.Entry<String, Long> entry : fileSizeMap.entrySet()) {
                sizes[i] = entry.getValue()/1024/1024;
                fileName[i] = entry.getKey();
                i++;
            }
            if (!printed){
                System.out.println(JSON.toJSONString(fileName));
                printed = true;
            }
            System.out.println(JSON.toJSONString(sizes));
        }
    }

    public void statIntensityFileSizes() {
        String[] directories = new String[]{
                "mzML64\\int2",
                "mzML64\\int02",
                "mzML64\\int002",
                "mzML64\\int008",
                "mzML64\\int0002",
        };
        for (String directory : directories) {
            System.out.println("scanning " + directory + ":");
            TreeMap<String, Long> fileSizeMap = FileUtil.sumFileSizes(parentFolder+directory);
            long[] sizes = new long[fileSizeMap.size()];
            int i = 0;
            for (Map.Entry<String, Long> entry : fileSizeMap.entrySet()) {
                sizes[i] = entry.getValue()/1024/1024;
                i++;
            }
            System.out.println(JSON.toJSONString(sizes));
        }
    }

    public float[] parse(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new ZlibWrapper().decode(bytes));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fb = byteBuffer.asFloatBuffer();
        float[] fs = new float[fb.capacity()];
        for (int i = 0; i < fb.capacity(); i++) {
            fs[i] = fb.get(i);
        }
        return fs;
    }

    public double[] parse64(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new ZlibWrapper().decode(bytes));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        DoubleBuffer db = byteBuffer.asDoubleBuffer();
        double[] ds = new double[db.capacity()];
        for (int i = 0; i < db.capacity(); i++) {
            ds[i] = db.get(i);
        }
        return ds;
    }

    public int[] parse64Integer(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new ZlibWrapper().decode(bytes));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer db = byteBuffer.asIntBuffer();
        int[] ds = new int[db.capacity()];
        for (int i = 0; i < db.capacity(); i++) {
            ds[i] = db.get(i);
        }
        return ds;
    }
}
