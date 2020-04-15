package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class XzLearn {


    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

    public static byte[] transToByte(int[] target){
        IntBuffer intTarget = IntBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(intTarget.capacity() * 4);
        bbTarget.asIntBuffer().put(intTarget);
        return bbTarget.array();
    }

    public static int[] mzToInt(float[] mzArray){
        int[] mzIntArray = new int[mzArray.length];
        for (int i = 0; i < mzArray.length; i++) {
            mzIntArray[i] = (int)(mzArray[i] * 1000);
        }
        return mzIntArray;
    }

    public static byte[] xzCompress(byte[] target, int level)  {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            XZOutputStream xzOutputStream = new XZOutputStream(bos, new LZMA2Options(level));
            xzOutputStream.write(target);
        }catch (IOException e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }


    public static void main(String[] args) {
        String fileName =
//                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
                "C20181205yix_HCC_DIA_N_38A.json";
//                "HYE124_TTOF5600_64var_lgillet_L150206_007.json";
//                "napedro_L120224_010_SW.json";
//                "C20181208yix_HCC_DIA_T_46A_with_zero_lossless.json";
//                "D20181207yix_HCC_SW_T_46A_with_zero_lossless.json";
//                "HYE110_TTOF6600_32fix_lgillet_I160308_001_with_zero_lossless.json";

        String xzDir = "D:\\Propro\\projet\\xzCompress\\" + fileName.split("\\.")[0];
        File outDir = new File(xzDir);
        if (!outDir.exists() && !outDir.isDirectory()) {
            outDir.mkdir();
        }
        System.out.println(fileName.split("\\\\")[0]);
        String path = "D:\\Propro\\projet\\data\\";
        File indexFile = new File(path + fileName);

        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

        try {
            xzUnpack(airdParser, swathIndexList, xzDir, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void xzUnpack(AirdParser airdParser, List<SwathIndex> swathIndexList, String xzDir, int level) throws IOException {


//        FileOutputStream mzOutFile = new FileOutputStream(xzDir + "\\\\mz_zlib.xz");
//        XZOutputStream mzOutputStream = new XZOutputStream(mzOutFile, new LZMA2Options(level));

        long time = 0;
        for (int swath = 0; swath < swathIndexList.size(); swath++) {

            FileOutputStream intOutFile = new FileOutputStream(String.format("%s\\\\intensity_swath%d.xz", xzDir, swath));
            XZOutputStream intOutputStream = new XZOutputStream(intOutFile, new LZMA2Options(level));

            ArrayList<Float> intArray = new ArrayList<>();
            System.out.println("正在扫描:" + (swath + 1) + "/" + swathIndexList.size());
            SwathIndex index = swathIndexList.get(swath);
//            System.out.println(index.getStartPtr());
            List<Float> rts = index.getRts();
            for (Float rt :
                    rts) {
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
//                int[] mzArray = mzToInt(pairs.getMzArray());
//                byte[] mzArrayFastpfor = CompressUtil.transToByte(CompressUtil.fastPForEncoder(mzArray));
                float[] intensities = pairs.getIntensityArray();
                for (float intensity :
                        intensities) {
                    intArray.add(intensity);
                }
//                mzOutputStream.write(mzArrayFastpfor);
            }
            float[] floats = new float[intArray.size()];
            for (int i = 0; i < intArray.size(); i++) {
                floats[i] = intArray.get(i);
            }

            long start = System.currentTimeMillis();
            byte[] outBytes = xzCompress(transToByte(floats), 1);
            intOutputStream.write(outBytes);
            long end = System.currentTimeMillis();
            time += (end - start);

            intOutputStream.close();
            intOutFile.close();
        }

        System.out.println(String.format("total time:  %d second", time/1000));

//        mzOutputStream.close();
//        mzOutFile.close();
    }
}
