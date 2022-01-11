package net.csibio.aird.test;/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.parser.BaseParser;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

public class DDAParserTest {

  String filePath = "D:\\meta-vendor\\SA1_with_zero.json";

  @Test
  public void testXICSpeed() {
    BaseParser parser = BaseParser.buildParser(filePath);
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

      System.out.println("理论光谱图数目:" + airdInfo.getTotalScanCount() + "");
      System.out.println("实际光谱图数目:" + total.get() + "");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testSize() {

  }
}
