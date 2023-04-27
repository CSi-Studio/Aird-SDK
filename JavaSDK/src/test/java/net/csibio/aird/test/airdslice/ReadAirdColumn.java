package net.csibio.aird.test.searchengine;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

public class ReadAirdColumn {

    static String parentFolder = "D:\\AirdMatrixTest\\Aird\\";
    static String[] precisionFolders = new String[]{
            "3dp\\",
            "4dp\\",
            "5dp\\",
            "3dp-centroid\\",
            "4dp-centroid\\",
            "5dp-centroid\\",
    };
    static String[] filenames = new String[]{
            "File1.SA1.cjson",
            "File2.SampleA_1.cjson",
            "File3.PestMix1_8PlasmaDDA20-50.cjson",
            "File4.jaeger3_0001.cjson",
            "File5.1.cjson",
            "File6.02122020_QCPool_Set1_P_2.cjson",
            "File7.SID599_H.cjson",
            "File8.RP POS-QC-1.cjson",
            "File9.MS01.cjson",
            "File10.EL01_pos.cjson",
            "File11.2_feces_fibrosis1_positive.cjson",
            "File12.20220818_HFX_cy_MCF_phospho_TNF_DDA_1_120min.cjson",
            "File13.20220818_HFX_cy_MCF_phospho_TNF_DDA_2_120min.cjson",
            "File14.20220905_HFX_cy_MCF_phospho_TNF_DIA_1_120min.cjson",
            "File15.20220905_HFX_cy_MCF_phospho_TNF_DIA_3_120min.cjson",
            "File16.20220905_HFX_cy_MCF_phospho_TNF_DIA_4_120min.cjson",
    };

//    static double[] targets = new double[]{967.97259, 487.3524, 753.61337, 711.56642, 864.63014, 755.55625, 647.51225, 829.79845, 637.51225, 429.79845};
        static double[] targets = new double[]{967.97259};
//    double[] targets = new double[]{864.63014};

    @Test
    public void speedTest() throws Exception {
        double[] randomTargets = generateRandomTargets(1, 400, 1500);
        //预热代码用
        xic("C:\\Users\\lms19\\Desktop\\jaeger3_0001.cjson", 0, new long[1], new long[1]);
        for (int i = 0; i < precisionFolders.length; i++) {
            String precisionFolder = precisionFolders[i];
            System.out.println("Precision Mode:" + precisionFolder);
            long[] timeWithIndex = new long[filenames.length];
            long[] timeWithoutIndex = new long[filenames.length];
            for (int j = 0; j < filenames.length; j++) {
                String filename = filenames[j];
                String indexPath = parentFolder + precisionFolder + filename;
                xic(indexPath, j, timeWithIndex, timeWithoutIndex);
            }
            System.out.println("Time With Index:");
            System.out.println(JSON.toJSONString(timeWithIndex));
            System.out.println("Time Without Index:");
            System.out.println(JSON.toJSONString(timeWithoutIndex));
            System.out.println("");
        }
    }

    public static void xic(String indexPath, int j, long[] timeWithIndex, long[] timeWithoutIndex) throws IOException {
        long startWithIndex = System.currentTimeMillis();
        ColumnParser parser = new ColumnParser(indexPath);
        long startWithoutIndex = System.currentTimeMillis();
        for (int t = 0; t < targets.length; t++) {
            double target = targets[t];
            parser.calcXic(target - 0.015, target + 0.015, null, null, null);
        }
        timeWithIndex[j] = (System.currentTimeMillis() - startWithIndex);
        timeWithoutIndex[j] = (System.currentTimeMillis() - startWithoutIndex);
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
