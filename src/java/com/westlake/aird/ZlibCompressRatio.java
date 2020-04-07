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

public class ZlibCompressRatio {


    public static void main(String[] args) {
//        File indexFile = new File("E:\\data\\HYE124_5600_64_Var\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
//        File indexFile = new File("E:\\data\\HYE110_6600_32_Fix\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("E:\\data\\HCC_QE3\\C20181205yix_HCC_DIA_N_38A.json");
//        File indexFile = new File("C:\\Users\\zhang\\Documents\\Propro\\projet\\data\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("C:\\Users\\zhang\\Documents\\Propro\\projet\\data\\C20181205yix_HCC_DIA_N_38A.json");
//        File indexFile = new File("C:\\Users\\zhang\\Documents\\Propro\\projet\\data\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
        File indexFile = new File("C:\\Users\\zhang\\Documents\\Propro\\projet\\data\\napedro_L120224_010_SW.json");

        //        File indexFile = new File("E:\\data\\SGSNew\\napedro_L120224_010_SW.json");
//        File indexFile = new File("E:\\metabolomics\\宣武医院 10-19 raw data\\NEG-Convert\\QXA01DNNEG20190627_DIAN1019VWHUMAN_HUMAN_PLASMA1_01.json");
        AtomicInteger rtCount = new AtomicInteger(0);
        AtomicLong mzCount = new AtomicLong(0);

        // {intensitySize, compressedIntensitySize, huffmanIntensitySize}
        final double[] sizes = {0, 0};


        Wrapper intensityWrapper = new Wrapper();

        AtomicInteger iter = new AtomicInteger(0);
        System.out.println(indexFile.getAbsolutePath());
        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
        swathIndexList.parallelStream().forEach(index -> {
            System.out.println("正在扫描:" + iter + "/" + swathIndexList.size());
            index.getRts().forEach(rt -> {
                //intensity -> zlib     mz -> fastpfor
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                if (index.getLevel() != 1) {
                    intensityWrapper.offer(pairs.getIntensityArray());

                    byte[] ms2Intensity = transToByte(pairs.getIntensityArray());
                    byte[] ms2IntensityCompressed = CompressUtil.zlibCompress(ms2Intensity);

                    sizes[0] += ms2Intensity.length / 1024.000 ;
                    sizes[1] += ms2IntensityCompressed.length / 1024.000 ;
                }
            });
            rtCount.addAndGet(index.getRts().size());
            iter.addAndGet(1);

        });

        System.out.println(intensityWrapper);
        System.out.println(String.format("ms2 int: %f kBs was compressed to %f kBs by Zip,  %f percent reduced",
                sizes[0],
                sizes[1],
                100*(1.0-(float)sizes[1]/sizes[0])));
    }

    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

}
