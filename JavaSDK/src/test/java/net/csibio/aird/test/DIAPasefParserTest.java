package net.csibio.aird.test;/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */


import com.alibaba.fastjson2.JSON;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.parser.DIAPasefParser;
import net.csibio.aird.util.FileUtil;
import org.junit.Test;

public class DIAPasefParserTest {

    @Test
    public void testReadFun() throws Exception {
        DIAPasefParser parser = new DIAPasefParser("D:\\MzmineTest\\PXD033904_PASEF\\Aird\\20220302_tims1_nElute_8cm_DOl_Phospho_7min_rep1_Slot1-94_1_1811.json");
        AirdInfo airdInfo = parser.getAirdInfo();
        for (BlockIndex blockIndex : airdInfo.getIndexList()) {
            TreeMap<Double, Spectrum> map = parser.getSpectra(blockIndex);
            System.out.println(map.size());
        }
    }
}
