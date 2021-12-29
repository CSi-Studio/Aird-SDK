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
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

import java.util.List;

public class DDAParserTest {

    @Test
    public void testXICSpeed() {
        DDAParser parser = new DDAParser("D:\\meta-vendor\\SampleA_1_with_zero.json");
//        DDAParser parser = new DDAParser("D:\\meta-vendor\\SA1_with_zero.json");
        AirdInfo airdInfo = parser.getAirdInfo();
        try {
            List<DDAMs> allMsList = parser.readAllToMemory();
            System.out.println(allMsList.size() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
