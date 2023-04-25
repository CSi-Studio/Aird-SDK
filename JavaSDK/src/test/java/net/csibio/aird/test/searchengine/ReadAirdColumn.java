package net.csibio.aird.test.searchengine;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ReadAirdColumn {

    //    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp\\DDA-Thermo-MTBLS733-SA1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\4dp\\DDA-Sciex-MTBLS733-SampleA_1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp-SearchEngine\\LIPPOS-1-A-B-SAM-21.json";
//    static String indexPath = "D:\\AirdColumn\\Neiyansuo\\3dp\\LIPPOS-1-A-B-BLK-1.cjson";
//    static String indexPath = "D:\\Aird-SliceTestDataSets\\3dp\\File1.SA1.cjson";
    static String indexPath = "C:\\Users\\LMS\\Desktop\\LIPPOS-1-A-B-SAM-100.cjson";

    double[] targets = new double[]{967.97259, 487.3524, 753.61337, 711.56642, 864.63014, 755.55625, 647.51225, 829.79845};
//    double[] targets = new double[]{967.97259};
//    double[] targets = new double[]{864.63014};

    @Test
    public void speedTest() throws Exception {
        double[] randomTargets = generateRandomTargets(100, 400, 1500);
        long start = System.currentTimeMillis();
        ColumnParser parser = new ColumnParser(indexPath);
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < targets.length; i++) {
            double target = targets[i];
            parser.calcXic(target - 0.015, target + 0.015, null, null, null);
        }

        System.out.println("Cost:" + (System.currentTimeMillis() - start));
        System.out.println("Cost1:" + (System.currentTimeMillis() - start1));
    }

    public static double[] generateRandomTargets(int count, int start, int end) {
        double[] targets = new double[count];
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            targets[i] = start + random.nextInt(end - start) + Math.random();
        }
        System.out.println(JSON.toJSONString(targets));
        return targets;
    }
}
