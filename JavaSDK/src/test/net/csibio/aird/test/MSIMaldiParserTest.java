/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.test;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.parser.MSIMaldiParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MSIMaldiParserTest {

    String filePath1 = "D:\\test\\imzml\\aird\\77-20240424_mMALDI_neg_9AA_brain_30um_low_CLMC_ProcessedHugo.json";
    String filePath2 = "D:\\test\\imzml-raw\\aird-raw\\11-test_POS.json";

    @Test
    public void testXICSpeed() throws Exception {
        MSIMaldiParser parser = new MSIMaldiParser(filePath1);
        AirdInfo airdInfo = parser.getAirdInfo();
        try {
            List<DDAMs> allMsList = parser.readAllToMemory();
            AtomicInteger total = new AtomicInteger(0);
            total.getAndAdd(allMsList.size());
            System.out.println("理论光谱图数目:" + airdInfo.getTotalCount() + "");
            System.out.println("实际光谱图数目:" + total.get() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadSpeed() throws Exception {
        MSIMaldiParser parser1 = new MSIMaldiParser(filePath1);
        long start1 = System.currentTimeMillis();
        parser1.readAllToMemory();
        System.out.println("Cost1:" + (System.currentTimeMillis() - start1));

        MSIMaldiParser parser2 = new MSIMaldiParser(filePath2);
        long start2 = System.currentTimeMillis();
        parser2.readAllToMemory();
        System.out.println("Cost2:" + (System.currentTimeMillis() - start2));
    }

    @Test
    public void testReadSingleSpectrum() throws Exception {
        MSIMaldiParser parser = new MSIMaldiParser(filePath1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < parser.getAirdInfo().getTotalCount(); i++) {
            Spectrum spectrum = parser.getSpectrumByNum(i);
        }
        System.out.println("Cost:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testReadMultipleSpectra() throws Exception {
        MSIMaldiParser parser = new MSIMaldiParser(filePath1);
        long start = System.currentTimeMillis();
        Spectrum[] list = parser.getSpectraByNums(1, 2, 3);
        System.out.println("Cost:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testReadSpectra() throws Exception {
        MSIMaldiParser parser = new MSIMaldiParser(filePath1);
        long start = System.currentTimeMillis();
        List<DDAMs> ms1List = parser.getSpectraByRtRange(0d, 10d, false);
        System.out.println("Cost:" + (System.currentTimeMillis() - start));
    }
}
