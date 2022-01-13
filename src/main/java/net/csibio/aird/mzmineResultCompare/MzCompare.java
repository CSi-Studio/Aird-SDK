package net.csibio.aird.mzmineResultCompare;

import java.util.LinkedList;

public class MzCompare {
    public static void main(String[] args)  {
        float tolerance = (float) 0.001;
        String rawFile = "C:\\Users\\admin\\Desktop\\ADAP_raw.csv";
        LinkedList<Peak> rawData = CsvUtil.readPeaks(rawFile);
        String airdFile = "C:\\Users\\admin\\Desktop\\ADAP_Aird4.csv";
        LinkedList<Peak> airdData = CsvUtil.readPeaks(airdFile);
        String mzMLFile = "C:\\Users\\admin\\Desktop\\ADAP_mzML.csv";
        LinkedList<Peak> mzMLData = CsvUtil.readPeaks(mzMLFile);
        String npFile = "C:\\Users\\admin\\Desktop\\ADAP_Numpress.csv";
        LinkedList<Peak> npData = CsvUtil.readPeaks(npFile);

//        comparePeaks(rawData,npData,tolerance);
        comparePeaks(rawData,airdData,tolerance);
//        comparePeaks(npData,airdData,tolerance);

    }

    public static void comparePeaks(LinkedList<Peak> peaksA, LinkedList<Peak> peaksB, float tolerance) {
        LinkedList<Peak> mutualDataA = new LinkedList<>();
        LinkedList<Peak> mutualDataB = new LinkedList<>();

        for (Peak peakA : peaksA) {
            for (Peak peakB : peaksB) {
                if (peakA.equals(peakB, tolerance)) {
                    mutualDataA.add(peakA);
                    mutualDataB.add(peakB);
                }
            }
        }
        peaksA.removeAll(mutualDataA);
        peaksB.removeAll(mutualDataB);

        String filePath="C:\\Users\\admin\\Desktop\\mutual.csv";
        String filePathA="C:\\Users\\admin\\Desktop\\A.csv";
        String filePathB="C:\\Users\\admin\\Desktop\\B.csv";
        String[] headers = { "rt", "mz", "area" };
        CsvUtil.writeCSV(peaksA,filePathA,headers);
        CsvUtil.writeCSV(peaksB,filePathB,headers);
        CsvUtil.writeCSV(mutualDataA,filePath,headers);
    }
}
