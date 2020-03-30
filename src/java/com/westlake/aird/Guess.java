package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import com.westlake.aird.util.AirdScanUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Guess {

    public static void main(String[] args) {
//        File indexFile = new File("E:\\data\\HYE124_5600_64_Var\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
//        File indexFile = new File("E:\\data\\HYE110_6600_32_Fix\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("E:\\data\\HCC_QE3\\C20181205yix_HCC_DIA_N_38A.json");
        File indexFile = new File("E:\\data\\HYE5\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("E:\\data\\SGSNew\\napedro_L120224_010_SW.json");
//        File indexFile = new File("E:\\metabolomics\\宣武医院 10-19 raw data\\NEG-Convert\\QXA01DNNEG20190627_DIAN1019VWHUMAN_HUMAN_PLASMA1_01.json");
        AtomicInteger rtCount = new AtomicInteger(0);
        AtomicLong mzCount = new AtomicLong(0);
        Set ms1MzSet = Collections.synchronizedSet(new HashSet<Float>());
        Set ms2MzSet = Collections.synchronizedSet(new HashSet<Float>());
        Set ms1IntensitySet = Collections.synchronizedSet(new HashSet<Float>());
        Set ms2IntensitySet = Collections.synchronizedSet(new HashSet<Float>());

        AtomicInteger iter = new AtomicInteger(0);
        System.out.println(indexFile.getAbsolutePath());
        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
        swathIndexList.parallelStream().forEach(index -> {
            System.out.println("正在扫描:" + iter + "/" + swathIndexList.size());
            index.getRts().forEach(rt -> {
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                if (index.getLevel() == 1) {
                    ms1IntensitySet.addAll(new HashSet<Float>(Arrays.asList(pairs.getIntensityArray())));
                } else {
                    mzCount.addAndGet(pairs.getMzArray().length);
                    ms2IntensitySet.addAll(new HashSet<Float>(Arrays.asList(pairs.getIntensityArray())));
                    ms2MzSet.addAll(new HashSet<Float>(Arrays.asList(pairs.getMzArray())));
                }
            });
            rtCount.addAndGet(index.getRts().size());
            iter.addAndGet(1);
        });
        System.out.println("总计RT:" + rtCount);
        System.out.println("总计mzCount:" + mzCount);
        System.out.println("总计ms1 mz:" + ms1MzSet.size());
        System.out.println("总计ms2 mz:" + ms2MzSet.size());
        System.out.println("总计ms1 int:" + ms1IntensitySet.size());
        System.out.println("总计ms2 int:" + ms2IntensitySet.size());

    }

}
