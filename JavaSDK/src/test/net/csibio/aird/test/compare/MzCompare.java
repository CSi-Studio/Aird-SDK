package net.csibio.aird.test.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MzCompare {


    public static void main(String[] args) {

//    String aFile = "D:\\ADAP_mzML.csv";
//    String bFile = "D:\\ADAP_Aird6.csv";
//    String cFile = "D:\\ADAP_Numpress.csv";
        ArrayList<Peak> a = new ArrayList<>();
        ArrayList<Peak> b = new ArrayList<>();
        initMzmine2(a, b);
//    ArrayList<Peak> c = CsvUtil.readPeaks(cFile, "C");

//    String mzMLFile = "C:\\Users\\admin\\Desktop\\ADAP_mzML.csv";
//    LinkedList<Peak> mzMLData = CsvUtil.readPeaks(mzMLFile);
//    String npFile = "C:\\Users\\admin\\Desktop\\ADAP_Numpress.csv";
//    LinkedList<Peak> npData = CsvUtil.readPeaks(npFile);

//        comparePeaks(rawData,npData,tolerance);
        int diffAB = comparePeaks(a, b);
//    List<Peak> diffAC = comparePeaks(a, c);
//    List<Peak> diffBC = comparePeaks(b, c);
//        comparePeaks(npData,airdData,tolerance);
        System.out.println("Diff AB:" + diffAB);
//    System.out.println("Diff AC:" + diffAC.size());
//    System.out.println("Diff BC:" + diffBC.size());
    }

    public static int comparePeaks(List<Peak> peaksA, List<Peak> peaksB) {

        List<Peak> diffOnlyA = new CopyOnWriteArrayList<>();
        List<Peak> diffOnlyB = new CopyOnWriteArrayList<>();
        peaksA.parallelStream().forEach(peakA -> {
            boolean searched = false;
            for (Peak peakB : peaksB) {
                if (peakA.equals(peakB)) {
                    searched = true;
                }
            }
            if (!searched) {
                diffOnlyA.add(peakA);
            }
        });

        peaksB.parallelStream().forEach(peakB -> {
            boolean searched = false;
            for (Peak peakA : peaksA) {
                if (peakA.equals(peakB)) {
                    searched = true;
                }
            }
            if (!searched) {
                diffOnlyB.add(peakB);
            }
        });

        System.out.println("diffOnlyA:" + diffOnlyA.size() + "个");
        System.out.println("diffOnlyB:" + diffOnlyB.size() + "个");
        return diffOnlyA.size() + diffOnlyB.size();
//    String filePath = "C:\\Users\\admin\\Desktop\\mutual.csv";
//    String filePathA = "C:\\Users\\admin\\Desktop\\A.csv";
//    String filePathB = "C:\\Users\\admin\\Desktop\\B.csv";
//    String[] headers = {"rt", "mz", "area"};
//    CsvUtil.writeCSV(peaksA, filePathA, headers);
//    CsvUtil.writeCSV(peaksB, filePathB, headers);
//    CsvUtil.writeCSV(mutualDataA, filePath, headers);
    }

    public static void initOri(ArrayList<Peak> a, ArrayList<Peak> b) {
        String aFile = "D:\\aird_test\\ori_sa1_raw.csv";
        String bFile = "D:\\aird_test\\ori_sa1_aird.csv";
        a.addAll(CsvUtil.readPeaks(aFile, "A"));
        b.addAll(CsvUtil.readPeaks(bFile, "B"));
    }

    public static void initMin(ArrayList<Peak> a, ArrayList<Peak> b) {
        String aFile = "D:\\aird_test\\min_raw.csv";
        String bFile = "D:\\aird_test\\min_aird.csv";
        a.addAll(CsvUtil.readPeaks(aFile, "A"));
        b.addAll(CsvUtil.readPeaks(bFile, "B"));
    }

    public static void initAdap(ArrayList<Peak> a, ArrayList<Peak> b) {
        String aFile = "D:\\aird_test\\adap_raw.csv";
        String bFile = "D:\\aird_test\\adap_aird.csv";
        a.addAll(CsvUtil.readPeaks(aFile, "A"));
        b.addAll(CsvUtil.readPeaks(bFile, "B"));
    }

    public static void initBaseline(ArrayList<Peak> a, ArrayList<Peak> b) {
        String aFile = "D:\\aird_test\\baseline_raw.csv";
        String bFile = "D:\\aird_test\\baseline_aird.csv";
        a.addAll(CsvUtil.readPeaks(aFile, "A"));
        b.addAll(CsvUtil.readPeaks(bFile, "B"));
    }

    public static void initMzmine2(ArrayList<Peak> a, ArrayList<Peak> b) {
        String aFile = "D:\\aird_test\\raw_new.csv";
        String bFile = "D:\\aird_test\\aird_new.csv";
        a.addAll(CsvUtil.readPeaks(aFile, "A"));
        b.addAll(CsvUtil.readPeaks(bFile, "B"));
    }
}
