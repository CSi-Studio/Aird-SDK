package net.csibio.aird.test.searchengine;

import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

public class ReadAirdColumn {

//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp\\DDA-Thermo-MTBLS733-SA1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\4dp\\DDA-Sciex-MTBLS733-SampleA_1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp-SearchEngine\\LIPPOS-1-A-B-SAM-21.json";
    static String indexPath = "D:\\AirdColumn\\Neiyansuo\\3dp\\LIPPOS-1-A-B-BLK-1.cjson";

    double[] targets = new double[]{967.97259,487.3524,753.61337,711.56642,864.63014,755.55625,647.51225,829.79845};

    @Test
    public void speedTest() throws Exception {
        long start = System.currentTimeMillis();
        ColumnParser parser = new ColumnParser(indexPath);
        for (double target : targets) {
            parser.calcXic(target-0.01, target+0.01, 80d,90d,null);
        }
        System.out.println("Cost:"+(System.currentTimeMillis() - start));
    }
}
