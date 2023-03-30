package net.csibio.aird.test;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.ChromatogramIndex;
import net.csibio.aird.bean.common.MrmPair;
import net.csibio.aird.parser.MRMParser;
import org.junit.Test;

import java.util.List;

public class MRMTest {

    @Test
    public void testMRM() throws Exception {
        MRMParser parser = new MRMParser("D:\\鹿明合作项目\\Aird-MRM\\10677-A1.json");
        ChromatogramIndex index = parser.getChromatogramIndex();
        List<MrmPair> pairs = parser.getAllMrmPairs();
        System.out.println(JSON.toJSONString(pairs.get(50).getRts()));
        System.out.println(JSON.toJSONString(pairs.get(50).getInts()));
    }
}
