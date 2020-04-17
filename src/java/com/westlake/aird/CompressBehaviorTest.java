package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import com.westlake.aird.util.CompressUtil;
import com.westlake.aird.util.GZIPCompressUtil;
import com.westlake.aird.util.XZCompressUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class CompressBehaviorTest {

    public static void main(String[] args) {
        
        String[] fileNames = {
                "D:\\Propro\\projet\\data\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json",
                "D:\\Propro\\projet\\data\\C20181205yix_HCC_DIA_N_38A.json",
                "D:\\Propro\\projet\\data\\HYE124_TTOF5600_64var_lgillet_L150206_007.json"
        };
        for (String fileName:
            fileNames) {
            File indexFile = new File(fileName);

            AtomicLong intensitySize = new AtomicLong(0);
            AtomicLong zlibCompressedIntSize = new AtomicLong(0);
            AtomicLong xzCompressedIntSize = new AtomicLong(0);
            AtomicLong gzipCompressedIntSize = new AtomicLong(0);

            AtomicLong mzSize = new AtomicLong(0);
            AtomicLong zlibCompressedMzSize = new AtomicLong(0);
            AtomicLong xzCompressedMzSize = new AtomicLong(0);
            AtomicLong gzipCompressedMzSize = new AtomicLong(0);

            System.out.println();
            System.out.println(indexFile.getAbsolutePath());
            AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
            List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
            swathIndexList.forEach(index -> {
                index.getRts().parallelStream().forEach(rt -> {
                    //intensity -> zlib     mz -> fastpfor
                    MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);

                    // 原数据
                    byte[] intensity = transToByte(pairs.getIntensityArray());
                    int[] mz = transToInt(pairs.getMzArray());

                    // zlib压缩数据
                    byte[] intensityCompressed = CompressUtil.zlibCompress(intensity);
                    byte[] mzCompressed = CompressUtil.transToByte(CompressUtil.fastPForEncoder(mz));

                    // Xz压缩数据
                    byte[] intensityXZ = XZCompressUtil.xzCompress(intensity, 1);
                    byte[] mzXZ = XZCompressUtil.xzCompress(transToByte(CompressUtil.fastPForEncoder(mz)),1);

                    // Gzip压缩数据
                    byte[] intensityGzip = GZIPCompressUtil.gzipCompress(intensity);
                    byte[] mzGzip = GZIPCompressUtil.gzipCompress(transToByte(CompressUtil.fastPForEncoder(mz)));

                    intensitySize.addAndGet(intensity.length);
                    mzSize.addAndGet(4 * mz.length);
                    zlibCompressedIntSize.addAndGet(intensityCompressed.length);
                    zlibCompressedMzSize.addAndGet(mzCompressed.length);
                    xzCompressedIntSize.addAndGet(intensityXZ.length);
                    xzCompressedMzSize.addAndGet(mzXZ.length);
                    gzipCompressedIntSize.addAndGet(intensityGzip.length);
                    gzipCompressedMzSize.addAndGet(mzGzip.length);
                });
            });

            System.out.println(String.format("mz: %f MBs ", mzSize.get() / 1024f / 1024f));
            System.out.println(String.format("int: %f MBs ", intensitySize.get() / 1024f / 1024f));

            printResult("zlib", zlibCompressedMzSize.get(), zlibCompressedIntSize.get(), mzSize.get(), intensitySize.get());
            printResult("xz", xzCompressedMzSize.get(), xzCompressedIntSize.get(), mzSize.get(), intensitySize.get());
            printResult("gzip", gzipCompressedMzSize.get(), gzipCompressedIntSize.get(), mzSize.get(), intensitySize.get());

        }
    }

    public static void printResult(String method, long mzSize, long intSize, long mzSizeO, long intSizeO){

        System.out.println("================" + method + "=================");
        System.out.println(String.format("mz : %f MBs, %f %% reduced",
                mzSize / 1024f / 1024f,
                100 * (1.0 - (double) mzSize / mzSizeO)));
        System.out.println(String.format("int : %f MBs, %f %% reduced",
                intSize / 1024f / 1024f,
                100 * (1.0 - (double) intSize / intSizeO)));

    }

    public static byte[] transToByte(float[] target) {

        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

    public static byte[] transToByte(int[] target) {

        IntBuffer ibTarget = IntBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
        bbTarget.asIntBuffer().put(ibTarget);
        return bbTarget.array();
    }

    public static int[] transToInt(float[] target){

        int[] out = new int[target.length];
        for (int i = 0; i < target.length; i++) {
            out[i] = (int)(target[i] * 1000);
        }
        return out;
    }

    public static float[] transToFloat(byte[] target) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(target);
        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }

        byteBuffer.clear();
        return floatValues;
    }

}
