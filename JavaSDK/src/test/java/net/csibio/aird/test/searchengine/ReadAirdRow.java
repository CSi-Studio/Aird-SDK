package net.csibio.aird.test.searchengine;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.parser.BaseParser;
import net.csibio.aird.parser.ColumnParser;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class ReadAirdRow {

    static String parentFolder = "D:\\AirdMatrixTest\\ComboComp\\";
    static String[] precisionFolders = new String[]{
            "3dp\\",
            "4dp\\",
            "5dp\\",
    };
    static String[] filenames = new String[]{
            "File1.SA1.json",
            "File2.SampleA_1.json",
            "File3.PestMix1_8PlasmaDDA20-50.json",
            "File4.jaeger3_0001.json",
            "File5.1.json",
            "File6.02122020_QCPool_Set1_P_2.json",
            "File7.SID599_H.json",
            "File8.RP POS-QC-1.json",
            "File9.MS01.json",
            "File10.EL01_pos.json",
            "File11.2_feces_fibrosis1_positive.json",
            "File12.20220818_HFX_cy_MCF_phospho_TNF_DDA_1_120min.json",
            "File13.20220818_HFX_cy_MCF_phospho_TNF_DDA_2_120min.json",
            "File14.20220905_HFX_cy_MCF_phospho_TNF_DIA_1_120min.json",
            "File15.20220905_HFX_cy_MCF_phospho_TNF_DIA_3_120min.json",
            "File16.20220905_HFX_cy_MCF_phospho_TNF_DIA_4_120min.json",
    };

//    static double[] targets = new double[]{967.97259, 487.3524, 753.61337, 711.56642, 864.63014, 755.55625, 647.51225, 829.79845, 637.51225, 429.79845};
        static double[] targets = new double[]{967.97259};
//    double[] targets = new double[]{864.63014};

    @Test
    public void speedTest() throws Exception {
        double[] randomTargets = generateRandomTargets(1, 400, 1500);
        //预热代码用
        xic("D:\\AirdTest\\ComboComp\\File1.json", 0, new long[1], new long[1]);
        for (int i = 0; i < precisionFolders.length; i++) {
            String precisionFolder = precisionFolders[i];
            System.out.println("Precision Mode:" + precisionFolder);
            long[] timeWithIndex = new long[filenames.length];
            long[] timeWithoutIndex = new long[filenames.length];
            for (int j = 0; j < filenames.length; j++) {
                String filename = filenames[j];
                String indexPath = parentFolder + precisionFolder + filename;
                xic(indexPath, j, timeWithIndex, timeWithoutIndex);
//                long startWithIndex = System.currentTimeMillis();
//                ColumnParser parser = new ColumnParser(indexPath);
//                long startWithoutIndex = System.currentTimeMillis();
//                for (int t = 0; t < targets.length; t++) {
//                    double target = targets[t];
//                    parser.calcXic(target - 0.015, target + 0.015, null, null, null);
//                }
//                timeWithIndex[j] = (System.currentTimeMillis() - startWithIndex);
//                timeWithoutIndex[j] = (System.currentTimeMillis() - startWithoutIndex);
            }
            System.out.println("Time With Index:");
            System.out.println(JSON.toJSONString(timeWithIndex));
            System.out.println("Time Without Index:");
            System.out.println(JSON.toJSONString(timeWithoutIndex));
            System.out.println("");
        }
    }

    public static void xic(String indexPath, int j, long[] timeWithIndex, long[] timeWithoutIndex) throws Exception {
        long startWithIndex = System.currentTimeMillis();
        BaseParser parser = BaseParser.buildParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        long startWithoutIndex = System.currentTimeMillis();
        if (airdInfo.getType().equals(AirdType.DDA.getName())) {
            DDAParser ddaParser = (DDAParser) parser;
            TreeMap<Double, Spectrum> ms1Map = ddaParser.getMs1SpectraMap();
            for (int t = 0; t < targets.length; t++) {
                double target = targets[t];
                ddaParser.calcXic(ms1Map, target - 0.015, target + 0.015);
            }
        } else if (airdInfo.getType().equals(AirdType.DIA.getName())) {
            DIAParser diaParser = (DIAParser) parser;
            TreeMap<Double, Spectrum> ms1Map = diaParser.getSpectra(airdInfo.getIndexList().get(0));
            for (int t = 0; t < targets.length; t++) {
                double target = targets[t];
                diaParser.calcXic(ms1Map, target - 0.015, target + 0.015);
            }
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
