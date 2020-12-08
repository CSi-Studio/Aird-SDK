package net.csibio.aird.test;/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */


import com.alibaba.fastjson.JSONObject;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.FileUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class DIAParserTest {

    @Test
    public void testXICSpeed() {
        DIAParser parser = new DIAParser("E:\\pData\\HYE4_64_fix\\HYE110_TTOF6600_64fix_lgillet_I160310_001.json");
        AirdInfo airdInfo = parser.getAirdInfo();

        //加载标准库
        String peptidesStr = FileUtil.readFile(getClass().getClassLoader().getResource("library.test.json").getPath());
        TreeMap<String, List<BigDecimal>> peptideJsonList = JSONObject.parseObject(peptidesStr, TreeMap.class);
        TreeMap<Double, float[]> peptideListMap = new TreeMap<>();
        AtomicLong count = new AtomicLong();
        peptideJsonList.forEach((key, mzs) -> {
            float[] f = new float[mzs.size()];
            for (int i = 0; i < mzs.size(); i++) {
                f[i] = mzs.get(i).floatValue();
            }
            count.getAndAdd(mzs.size());
            peptideListMap.put(Double.parseDouble(key), f);
        });

        long start = System.currentTimeMillis();
        airdInfo.getIndexList().stream().filter(index -> index.getLevel() != 1).sorted(Comparator.comparing(index -> index.getWindowRange().getStart())).collect(Collectors.toList()).forEach(blockIndex -> {
            if (blockIndex.getLevel() == 2) {
                System.out.println("Start Analysis For Index:" + blockIndex.getWindowRange().getStart() + ":" + blockIndex.getWindowRange().getEnd());

                TreeMap<Float, MzIntensityPairs> map = parser.getSpectrums(blockIndex);
                System.out.println("Total Loop:" + count.get() + "*" + map.size() + "=" + (count.get() * map.size() / 10000 / 10000) + "亿次");

                List<MzIntensityPairs> pairsList = new ArrayList<>();
                List<Float> mzList = new ArrayList();
                map.entrySet().forEach(entry -> {
                    pairsList.add(entry.getValue());
                });
                peptideListMap.forEach((precursorMz, mzArray) -> {
                    for (int i = 0; i < mzArray.length; i++) {
                        mzList.add(mzArray[i]);
                    }
                });
                float[] startMzArray = new float[mzList.size()];
                float[] endMzArray = new float[mzList.size()];
                for (int i = 0; i < mzList.size(); i++) {
                    startMzArray[i] = mzList.get(i) - 0.025f;
                    endMzArray[i] = mzList.get(i) + 0.025f;
                }
                System.out.println("内存对象配置完毕,开始运算");

                long indexStart = System.currentTimeMillis();
                pairsList.forEach(pairs -> {
                    mzList.forEach(mz -> {
                        Extractor.accumulation(pairs, mz - 0.025f, mz + 0.025f);
                    });
                });
//                peptideListMap.forEach((precursorMz, mzArray) -> {
//                    map.entrySet().parallelStream().forEach((rtPairs) -> {
//                        for (int i = 0; i < mzArray.length; i++) {
//                            float intensity = Extractor.accumulation(rtPairs.getValue(), mzArray[i] - 0.025f, mzArray[i] + 0.025f);
//                        }
//                    });
//                });
                System.out.println("CPU Index Analysis 耗时:" + (System.currentTimeMillis() - indexStart) / 1000 + "秒");

                indexStart = System.currentTimeMillis();
                Extractor.accumulationWithGPU(pairsList, startMzArray, endMzArray);
                System.out.println("GPU Index Analysis 耗时:" + (System.currentTimeMillis() - indexStart) / 1000 + "秒");
            }
        });
        System.out.println("总计耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }
}
