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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DIAParserTest {

    @Test
    public void testXICSpeed() {
        DIAParser parser = new DIAParser("D:\\pData\\HYE4_64_fix\\HYE110_TTOF6600_64fix_lgillet_I160310_001.json");
        AirdInfo airdInfo = parser.getAirdInfo();

        //加载标准库
        String peptidesStr = FileUtil.readFile(getClass().getClassLoader().getResource("library.test.json").getPath());
        TreeMap<Double, List<BigDecimal>> peptideJsonList = JSONObject.parseObject(peptidesStr, TreeMap.class);
        TreeMap<Double, List<Float>> peptideList = new TreeMap<>();
        peptideJsonList.entrySet().forEach(entry -> {
            List<Float> mzList = new ArrayList<>();
            entry.getValue().forEach(mz -> {
                mzList.add(mz.floatValue());
            });
            peptideList.put(entry.getKey(), mzList);
        });

        long start = System.currentTimeMillis();
        airdInfo.getIndexList().stream().filter(index -> index.getLevel() != 1).sorted(Comparator.comparing(index -> index.getWindowRange().getStart())).collect(Collectors.toList()).forEach(blockIndex -> {
            if (blockIndex.getLevel() == 2) {
                System.out.println("Start Analysis For Index:" + blockIndex.getWindowRange().getStart() + ":" + blockIndex.getWindowRange().getEnd());
                long indexStart = System.currentTimeMillis();
                TreeMap<Float, MzIntensityPairs> map = parser.getSpectrums(blockIndex);
                peptideList.entrySet().parallelStream().forEach(peptideEntry -> {
                    peptideEntry.getValue().forEach(mz -> {
                        map.entrySet().forEach(entry -> {
                            float intensity = Extractor.accumulation(entry.getValue(), mz - 0.025f, mz + 0.025f);
                        });
                    });
                });
                System.out.println("Index Analysis 耗时:" + (System.currentTimeMillis() - indexStart) / 1000 + "秒");
            }
        });
        System.out.println("总计耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }
}
