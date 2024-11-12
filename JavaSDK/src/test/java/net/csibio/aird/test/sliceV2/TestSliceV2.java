package net.csibio.aird.test.sliceV2;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

import java.io.IOException;

public class TestSliceV2 {

    static String indexPath = "F:\\测试\\SA1.index";
    static String indexPath2 = "F:\\测试\\SampleA_1.index";
    static String oldIndexPath = "F:\\测试\\V1\\SA1.index";

    @Test
    public void testEIC() throws IOException {
        long start = System.currentTimeMillis();

        ColumnParser parserNew = new ColumnParser(indexPath);
        System.out.println("索引初始化时间为:"+(System.currentTimeMillis() - start)+"毫秒");
        start = System.currentTimeMillis();
//        ColumnParser parserOld = new ColumnParser(oldIndexPath);
        Xic newXic = parserNew.calcXicByMz(313.1733, 0.015);
        System.out.println("构建EIC单次的时间:"+(System.currentTimeMillis() - start)+"毫秒");
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Xic xic = parserNew.calcXicByMz(313.1733+i*5, 0.015);
        }
        System.out.println("构建EIC 10次的时间:"+(System.currentTimeMillis() - start)+"毫秒");
//        Xic oldXic = parserOld.calcXicByMz(313.1733, 0.015);
//        System.out.println(JSON.toJSONString(newXic.getRts()));
        System.out.println(JSON.toJSONString(newXic.getInts()));
//        System.out.println(JSON.toJSONString(oldXic.getRts()));
//        System.out.println(JSON.toJSONString(oldXic.getInts()));
    }
}
