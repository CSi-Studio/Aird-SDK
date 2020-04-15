package com.westlake.aird;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class XzCompress {

    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        // 第二个参数为xz压缩等级
        byte[] compressed = xzCompress(bbTarget.array(), 1);
        return compressed;
    }

    public static float[] transToFloat(byte[] target){
        byte[] decompressed = xzDecompress(target);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decompressed);
        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }

        byteBuffer.clear();
        return floatValues;
    }

    public static byte[] xzCompress(byte[] target, int level)  {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            XZOutputStream xzOutputStream = new XZOutputStream(bos, new LZMA2Options(level));
            xzOutputStream.write(target);
            xzOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public static byte[] xzDecompress(byte[] target) {
        List<Byte> byteList = new ArrayList<>();
        try {
            InputStream is = new ByteArrayInputStream(target);
            XZInputStream xzInputStream = new XZInputStream(is);

            int readLen;
            byte[] buffer = new byte[2048];
            while ((readLen = xzInputStream.read(buffer, 0, 2048)) != -1) {
                for (int i = 0; i < readLen; i++) {
                    byteList.add(buffer[i]);
                }
            }

            xzInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] ret = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            ret[i] = byteList.get(i);
        }

        return ret;
    }
    /* 测试代码
    public static void main(String[] args) {
//        File indexFile = new File("E:\\data\\HYE124_5600_64_Var\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
        File indexFile = new File("D:\\Propro\\projet\\data\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("D:\\Propro\\projet\\data\\C20181205yix_HCC_DIA_N_38A.json");
//        File indexFile = new File("D:\\Propro\\projet\\data\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
//        File indexFile = new File("E:\\data\\SGSNew\\napedro_L120224_010_SW.json");
//        File indexFile = new File("E:\\metabolomics\\宣武医院 10-19 raw data\\NEG-Convert\\QXA01DNNEG20190627_DIAN1019VWHUMAN_HUMAN_PLASMA1_01.json");

        System.out.println(indexFile.getAbsolutePath());
        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

        SwathIndex index = swathIndexList.get(10);
        Float rt = index.getRts().get(1000);
                //intensity -> zlib     mz -> fastpfor
        MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
        float[] intensity = pairs.getIntensityArray();
        byte[] compressed = xzCompress(transToByte(intensity), 1);
        System.out.println(compressed.length);
        float[] decompressed = transToFloat(xzDecompress(compressed));
        float mse = 0;
        for (int i = 0; i < intensity.length; i++) {
//            System.out.println(String.format("%f -> %f", intensity[i], decompressed[i]));
            mse += Math.pow((intensity[i] - decompressed[i]), 2);
        }
        System.out.println(String.format("mse = %f", mse));

    }

    public static void xzCompress2File(byte[] target, int level)  {
        try {
            OutputStream os = new FileOutputStream("D:\\Propro\\projet\\xzCompress\\test.xz");
            XZOutputStream xzOutputStream = new XZOutputStream(os, new LZMA2Options(level));
            xzOutputStream.write(target,0, target.length);

            xzOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static byte[] xzDeCompressFile(){
        List<Byte> byteList = new ArrayList<>();
        try{
            InputStream is = new FileInputStream("D:\\Propro\\projet\\xzCompress\\test.xz");
            XZInputStream xzInputStream = new XZInputStream(is);

            int len = 0;
            int readLen;
            byte[] buffer = new byte[2048];
            while ((readLen = xzInputStream.read(buffer, 0, 2048))!= -1){
                len += readLen;
                for (int i = 0; i < readLen; i++) {
                    byteList.add(buffer[i]);
                }
            }

            System.out.println("read " + len);
            xzInputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("list len " + byteList.size());
        byte[] ret = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            ret[i] = byteList.get(i);
        }

        return ret;
    }
    */
}
