package net.csibio.aird.test.compressor;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.common.MrmPair;
import net.csibio.aird.parser.MRMParser;
import org.junit.Test;

import java.util.List;

public class 测试MRMParser {

    @Test
    public void test() throws Exception {
        MRMParser MRMParser = new MRMParser("D:/iota_sp_gr_3-1.json");
        AirdInfo airdInfo = MRMParser.getAirdInfo();
        List<MrmPair> mrmPairs = MRMParser.getAllMrmPairs();
        System.out.println(mrmPairs.size());
    }
}
