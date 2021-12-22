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
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.FileUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class DDAParserTest {

    @Test
    public void testXICSpeed() {
        DDAParser parser = new DDAParser("D:\\meta-vendor\\SA1.json");
        AirdInfo airdInfo = parser.getAirdInfo();
        try {
            List<DDAMs> allMsList = parser.readAllToMemory();
            System.out.println(allMsList.size()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
