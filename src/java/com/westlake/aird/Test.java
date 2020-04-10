package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import com.westlake.aird.huffman.HuffmanCode;
import com.westlake.aird.huffman.Wrapper;
import com.westlake.aird.util.AirdScanUtil;
import com.westlake.aird.util.CompressUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Test {


    public static void main(String[] args) {
        String[] fileNames = {
                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json",
                "C20181205yix_HCC_DIA_N_38A.json",
                "HYE124_TTOF5600_64var_lgillet_L150206_007.json",
                "napedro_L120224_010_SW.json",
                "C20181208yix_HCC_DIA_T_46A_with_zero_lossless.json",
                "D20181207yix_HCC_SW_T_46A_with_zero_lossless.json",
                "HYE110_TTOF6600_32fix_lgillet_I160308_001_with_zero_lossless.json"
        };
        for (String fileName :
                fileNames) {

            String path = "D:\\Propro\\projet\\data\\";
            System.out.println(fileName);

            File indexFile = new File(path+fileName);

            AtomicInteger rtCount = new AtomicInteger(0);
//            AtomicLong mzCount = new AtomicLong(0);

            // {intensitySize, compressedIntensitySize, compressTime,huffmanIntensitySize}
            final long[] sizes = {0, 0, 0, 0};
            final long[] durant = {0, 0};


            Wrapper intensityWrapper = new Wrapper();

            AtomicInteger iter = new AtomicInteger(0);
            AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
            List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

            long startTime = System.currentTimeMillis();
//            System.out.println("Zip compress start");
            swathIndexList.parallelStream().forEach(index -> {
//                System.out.println("正在扫描:" + iter + "/" + swathIndexList.size());
                index.getRts().forEach(rt -> {
                    //intensity -> zlib     mz -> fastpfor
                    MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                    if (index.getLevel() != 1) {
                        intensityWrapper.offer(pairs.getIntensityArray());

                        byte[] ms2Intensity = transToByte(pairs.getIntensityArray());
                        byte[] ms2IntensityCompressed = CompressUtil.zlibCompress(ms2Intensity);

                        sizes[0] += ms2Intensity.length;
                        sizes[1] += ms2IntensityCompressed.length;
                    }
                });
                rtCount.addAndGet(index.getRts().size());
                iter.addAndGet(1);

            });
            long endTime = System.currentTimeMillis();
            durant[0] = (endTime - startTime) / 1000;
//            System.out.println("Zip compress completed");

//            System.out.println("huffman compress start");
            startTime = System.currentTimeMillis();
            HuffmanCode huffmanCode = new HuffmanCode(intensityWrapper.getNodeArray());
            huffmanCode.createHuffmanTree();
            huffmanCode.createHuffmanCodeMap();
//            System.out.println("huffman tree built");
            swathIndexList.parallelStream().forEach(index -> {
//                System.out.println("正在扫描:" + iter + "/" + swathIndexList.size());
                index.getRts().forEach(rt -> {
                    //intensity -> zlib     mz -> fastpfor
                    MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                    if (index.getLevel() != 1) {
                        intensityWrapper.offer(pairs.getIntensityArray());

                        float[] ms2Intensity = pairs.getIntensityArray();
                        byte[] ms2IntensityCompressed = huffmanCode.huffmanCompress(ms2Intensity);
                        sizes[2] += ms2IntensityCompressed.length ;

                        byte[] extremeCompress = CompressUtil.zlibCompress(ms2IntensityCompressed);
                        sizes[3] += extremeCompress.length ;
                    }
                });
            });
//            System.out.println("huffman compress completed");

            endTime = System.currentTimeMillis();
            durant[1] = (endTime - startTime) / 1000;

            System.out.println(String.format("ms2 int size: %d Bytes \n" +
                            "Zip : %d Bytes compressed in %d s\n" +
                            "Huffman : %d Bytes compressed in %d s\n" +
                            "Huffman + Zip : %d Bytes \n" ,
                    sizes[0],
                    sizes[1],
                    durant[0],
                    sizes[2],
                    durant[1],
                    sizes[3]));
        }
    }

    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

}
