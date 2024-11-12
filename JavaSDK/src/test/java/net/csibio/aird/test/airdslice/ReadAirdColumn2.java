package net.csibio.aird.test.airdslice;

import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

public class ReadAirdColumn2 {

    static String filePath = "C:\\Users\\LMS\\Desktop\\8b73647b-ba05-473c-91e5-0546b88dc139.cjson";


    //    static double[] targets = new double[]{967.97259, 487.3524, 753.61337, 711.56642, 864.63014, 755.55625, 647.51225, 829.79845, 637.51225, 429.79845};
    static double[] targets = new double[]{967.97259};
    static double[] targets2 = new double[]{967.97259};
//    double[] targets = new double[]{864.63014};

    @Test
    public void speedTest() throws Exception {
        ColumnParser parser = new ColumnParser(filePath);
        long start = System.currentTimeMillis();
        parser.calcXic(374.24475-0.015, 374.24475+0.015, null, null, null);
        System.out.println("Cost:" + (System.currentTimeMillis() - start));
    }
}
