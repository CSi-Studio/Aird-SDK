/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird;

import com.westlake.aird.api.DIAParser;
import com.westlake.aird.bean.BlockIndex;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.util.CompressUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Guess {

    public static void main(String[] args) {

        File indexFile = new File("C:\\Users\\zhang\\Documents\\Propro\\projet\\data\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("E:\\data\\HYE110_6600_32_Fix\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("E:\\data\\HCC_QE3\\C20181205yix_HCC_DIA_N_38A.json");
//        File indexFile = new File("E:\\data\\HYE5\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
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
        DIAParser airdParser = new DIAParser(indexFile.getAbsolutePath());
        List<BlockIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
        testMergePerformance(airdParser);

//        swathIndexList.parallelStream().forEach(index -> {
//            System.out.println("正在扫描:" + iter + "/" + swathIndexList.size());
//            index.getRts().forEach(rt -> {
//                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
//
//                if (index.getLevel() == 1) {
//                    ms1IntensitySet.addAll(new HashSet<Float>(Arrays.asList(pairs.getIntensityArray())));
//                } else {
//                    mzCount.addAndGet(pairs.getMzArray().length);
//                    ms2IntensitySet.addAll(new HashSet<Float>(Arrays.asList(pairs.getIntensityArray())));
//                    ms2MzSet.addAll(new HashSet<Float>(Arrays.asList(pairs.getMzArray())));
//                }
//            });
//            rtCount.addAndGet(index.getRts().size());
//            iter.addAndGet(1);
//        });
//        System.out.println("总计RT:" + rtCount);
//        System.out.println("总计mzCount:" + mzCount);
//        System.out.println("总计ms1 mz:" + ms1MzSet.size());
//        System.out.println("总计ms2 mz:" + ms2MzSet.size());
//        System.out.println("总计ms1 int:" + ms1IntensitySet.size());
//        System.out.println("总计ms2 int:" + ms2IntensitySet.size());

    }


    public static void testMergePerformance(DIAParser airdParser) {
        List<BlockIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
        MzIntensityPairs pairs1 = airdParser.getSpectrum(swathIndexList.get(0), swathIndexList.get(0).getRts().get(1));
        int[] origin = new int[pairs1.getMzArray().length];
        for (int i = 0; i < pairs1.getMzArray().length; i++) {
            origin[i] = (int) (pairs1.getMzArray()[i] * 1000);
        }
        int[] temp = CompressUtil.fastPforEncoder(origin);
        System.out.println("Pairs1--Before FastPFor:" + origin.length + ";After FastPFor: " + temp.length);

        MzIntensityPairs pairs2 = airdParser.getSpectrum(swathIndexList.get(0), swathIndexList.get(0).getRts().get(2));
        int[] origin2 = new int[pairs2.getMzArray().length];
        for (int i = 0; i < pairs2.getMzArray().length; i++) {
            origin2[i] = (int) (pairs2.getMzArray()[i] * 1000);
        }
        int[] temp2 = CompressUtil.fastPforEncoder(origin2);
        System.out.println("Pairs2--Before FastPFor:" + origin2.length + ";After FastPFor: " + temp2.length);

        List<Integer> finalList = new ArrayList<>();
        for (int i = 0; i < origin.length; i++) {
            finalList.add(origin[i]);
        }
        for (int i = 0; i < origin2.length; i++) {
            finalList.add(origin2[i]);
        }
        finalList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        int[] origin3 = new int[finalList.size()];
        for (int i = 0; i < finalList.size(); i++) {
            origin3[i] = finalList.get(i);
        }
        int[] temp3 = CompressUtil.fastPforEncoder(origin3);
        System.out.println("Pairs3--Before FastPFor:" + origin3.length + ";After FastPFor: " + temp3.length);
    }
}
