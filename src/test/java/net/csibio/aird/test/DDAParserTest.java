package net.csibio.aird.test;/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */


import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.parser.BaseParser;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DDAParserTest {

    String filePath1 = "D:\\metadata\\neiyansuo\\neg\\Set1-Human-Liver-REF-2.json";
    String filePath2 = "D:\\metadata\\neiyansuo\\neg\\Set1-Human-Liver-MIX-1.json";

    @Test
    public void testXICSpeed() throws Exception {
        BaseParser parser = BaseParser.buildParser(filePath1);
        AirdInfo airdInfo = parser.getAirdInfo();
        try {
            List<DDAMs> allMsList = ((DDAParser) parser).readAllToMemory();
            AtomicInteger total = new AtomicInteger(0);
            total.getAndAdd(allMsList.size());
            allMsList.forEach(ms -> {
                if (ms.getMs2List() != null) {
                    total.getAndAdd(ms.getMs2List().size());
                }
            });

            System.out.println("理论光谱图数目:" + airdInfo.getTotalCount() + "");
            System.out.println("实际光谱图数目:" + total.get() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadSpeed() throws Exception {
        DDAParser parser1 = new DDAParser(filePath1);
        long start1 = System.currentTimeMillis();
        parser1.readAllToMemory();
        System.out.println("Cost1:" + (System.currentTimeMillis() - start1));

        DDAParser parser2 = new DDAParser(filePath2);
        long start2 = System.currentTimeMillis();
        parser2.readAllToMemory();
        System.out.println("Cost2:" + (System.currentTimeMillis() - start2));
    }

    @Test
    public void testReadSingleSpectrum() throws Exception {
        DDAParser parser1 = new DDAParser(filePath1);
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < parser1.getAirdInfo().getTotalCount(); i++) {
            Spectrum spectrum = parser1.getSpectrumByNum(i);
        }
        System.out.println("Cost1:" + (System.currentTimeMillis() - start1));
    }

    @Test
    public void testReadSpectra() throws Exception {
        DDAParser parser1 = new DDAParser(filePath1);
        long start1 = System.currentTimeMillis();
        List<DDAMs> ms1List = parser1.getSpectraByRtRange(0d, 100d, false);
        System.out.println("Cost1:" + (System.currentTimeMillis() - start1));
    }
}
