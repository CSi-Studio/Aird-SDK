package net.csibio.aird.test.compressor;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.common.SrmPair;
import net.csibio.aird.parser.SRMParser;
import org.junit.Test;

import java.util.List;

public class 测试SRMParser {

    @Test
    public void test() throws Exception {
        SRMParser srmParser = new SRMParser("D:/iota_sp_gr_3-1.json");
        AirdInfo airdInfo = srmParser.getAirdInfo();
        List<SrmPair> srmPairs = srmParser.getAllSrmPairs();
        System.out.println(srmPairs.size());
    }
}
