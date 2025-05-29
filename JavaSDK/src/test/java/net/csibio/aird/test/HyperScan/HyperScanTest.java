package net.csibio.aird.test.HyperScan;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

import java.io.IOException;

public class HyperScanTest {

    static String indexPath = "/Users/cicci/Documents/TestFile/SampleA_1.cjson";

    @Test
    public void testEIC() throws IOException {
        long start = System.currentTimeMillis();
        double testMz = 192.1383;
        double[] testMzList = {126.0544, 136.0616, 180.10170, 192.1383, 220.0984, 220.9531, 233.0922, 237.1024, 252.1022};
        ColumnParser parserNew = new ColumnParser(indexPath);
        System.out.println("索引初始化时间为:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        Xic newXic = parserNew.calcXicByMz(testMz, 0.015);
        System.out.println("构建EIC单次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        for (int i = 0; i < testMzList.length; i++) {
            Xic xic = parserNew.calcXicByMz(testMzList[i], 0.015);
        }
        System.out.println("构建EIC 10次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        Xic newXicS = parserNew.calcXicSIMDByMz(testMz, 0.015);
        System.out.println("SIMD构建EIC单次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        for (int i = 0; i < testMzList.length; i++) {
            Xic xic = parserNew.calcXicByMz(testMzList[i], 0.015);
        }
        System.out.println("SIMD构建EIC 10次的时间:"+(System.currentTimeMillis() - start)+"毫秒");
    }

//    private void validateResults(Xic xic1, Xic xic2) {
//        // 验证RT数量一致
//        if (xic1.getRts().length != xic2.getRts().length) {
//            System.out.println("Warning: RT array lengths differ!");
//        }
//
//        // 验证每个RT值的差异
//        for (int i = 0; i < Math.min(xic1.getRts().length, xic2.getRts().length); i++) {
//            if (Math.abs(xic1.getRts()[i] - xic2.getRts()[i]) > 0.001) {
//                System.out.println("Warning: RT values differ at index " + i);
//            }
//        }
//
//        // 验证每个强度值的差异
//        for (int i = 0; i < Math.min(xic1.getIntensities().length, xic2.getIntensities().length); i++) {
//            if (Math.abs(xic1.getIntensities()[i] - xic2.getIntensities()[i]) > 0.001) {
//                System.out.println("Warning: Intensity values differ at index " + i);
//            }
//        }
//    }
}
