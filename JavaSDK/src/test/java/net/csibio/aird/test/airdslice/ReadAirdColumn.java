package net.csibio.aird.test.airdslice;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadAirdColumn {

    static String parentFolder = "D:\\AirdMatrixTest\\Aird\\";
    static String[] precisionFolders = new String[]{
//            "3dp\\",
//            "4dp\\",
            "5dp\\",
//            "3dp-centroid\\",
//            "4dp-centroid\\",
//            "5dp-centroid\\",
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

    static String[] fileTags = new String[]{
            "File1",
            "File2",
            "File3",
            "File4",
            "File5",
            "File6",
            "File7",
            "File8",
            "File9",
            "File10",
            "File11",
            "File12",
            "File13",
            "File14",
            "File15",
            "File16"
    };

    //    static double[] targets = new double[]{967.97259, 487.3524, 753.61337, 711.56642, 864.63014, 755.55625, 647.51225, 829.79845, 637.51225, 429.79845};
    static double[] targets = new double[]{967.97259};
    static double[] targets2 = new double[]{967.97259};
//    double[] targets = new double[]{864.63014};

    @Test
    public void speedTest() throws Exception {
        JSON.toJSONString(fileTags);
        TreeMap<Integer, double[]> diffTargetsMap = new TreeMap<>();
        double[] total = generateRandomTargets(100, 400, 1500);
        diffTargetsMap.put(1, Arrays.copyOfRange(total,0,1));
        diffTargetsMap.put(10, Arrays.copyOfRange(total,0,10));
        diffTargetsMap.put(20, Arrays.copyOfRange(total,0,20));
        diffTargetsMap.put(30, Arrays.copyOfRange(total,0,30));
        diffTargetsMap.put(40, Arrays.copyOfRange(total,0,40));
        diffTargetsMap.put(50, Arrays.copyOfRange(total,0,50));
        diffTargetsMap.put(60, Arrays.copyOfRange(total,0,60));
        diffTargetsMap.put(70, Arrays.copyOfRange(total,0,70));
        diffTargetsMap.put(80, Arrays.copyOfRange(total,0,80));
        diffTargetsMap.put(90, Arrays.copyOfRange(total,0,90));
        diffTargetsMap.put(100, Arrays.copyOfRange(total,0,100));

        TreeMap<Integer, double[]> diffTargetsMap2 = new TreeMap<>();
        double[] total2 = generateRandomTargets(100, 400, 1500);
        diffTargetsMap2.put(1, Arrays.copyOfRange(total2,0,1));
        diffTargetsMap2.put(10, Arrays.copyOfRange(total2,0,10));
        diffTargetsMap2.put(20, Arrays.copyOfRange(total2,0,20));
        diffTargetsMap2.put(30, Arrays.copyOfRange(total2,0,30));
        diffTargetsMap2.put(40, Arrays.copyOfRange(total2,0,40));
        diffTargetsMap2.put(50, Arrays.copyOfRange(total2,0,50));
        diffTargetsMap2.put(60, Arrays.copyOfRange(total2,0,60));
        diffTargetsMap2.put(70, Arrays.copyOfRange(total2,0,70));
        diffTargetsMap2.put(80, Arrays.copyOfRange(total2,0,80));
        diffTargetsMap2.put(90, Arrays.copyOfRange(total2,0,90));
        diffTargetsMap2.put(100, Arrays.copyOfRange(total2,0,100));
        //预热代码用
        xic("C:\\Users\\lms19\\Desktop\\jaeger3_0002.cjson","1", diffTargetsMap, diffTargetsMap2, new TreeMap<>());
        for (int i = 0; i < precisionFolders.length; i++) {
            String precisionFolder = precisionFolders[i];
            System.out.println("Precision Mode:" + precisionFolder);
            TreeMap<String, EChartsSeries> timeMap = new TreeMap<>();
            for (int j = 0; j < filenames.length; j++) {
                String filename = filenames[j];
                String fileTag = fileTags[j];
                String indexPath = parentFolder + precisionFolder + filename;
                xic(indexPath, fileTag, diffTargetsMap, diffTargetsMap2, timeMap);
            }
            timeMap.forEach((tag, series)->{
                System.out.print(JSON.toJSONString(series));
                System.out.println(",");
            });
            System.out.println("");
        }
    }

    public static void xic(String indexPath, String fileTag, TreeMap<Integer, double[]> diffTargetsMap, TreeMap<Integer, double[]> diffTargetsMap2, TreeMap<String, EChartsSeries> timeMap) throws IOException {
        ColumnParser parser = new ColumnParser(indexPath);

        AtomicInteger iter = new AtomicInteger();
        double[] times = new double[diffTargetsMap.size()];
        diffTargetsMap.forEach((count, targets) -> {
            long startWithoutIndex = System.nanoTime();
            for (int t = 0; t < targets.length; t++) {
                double target = targets[t];
                try {
                    parser.calcXic(target - 0.015, target + 0.015, null, null, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            times[iter.get()] = (System.nanoTime() - startWithoutIndex) / 1000000d;
            iter.getAndIncrement();
        });

//        iter.set(0);
//        diffTargetsMap2.forEach((count, targets) -> {
//            long startWithoutIndex = System.nanoTime();
//            for (int t = 0; t < targets.length; t++) {
//                double target = targets[t];
//                try {
//                    parser.calcXic(target - 0.015, target + 0.015, null, null, null);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            times[iter.get()] = (times[iter.get()] + (System.nanoTime() - startWithoutIndex) / 1000000d)/2;
//            iter.getAndIncrement();
//        });

        timeMap.put(fileTag, new EChartsSeries(fileTag, times));
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
