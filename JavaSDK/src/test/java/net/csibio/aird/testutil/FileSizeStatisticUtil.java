package net.csibio.aird.testutil;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSizeStatisticUtil {
    public static String path = "D:\\Aird-SliceTestDataSets\\ComboComp";
    public static String fileName = "ComboComp";

    public static void main(String[] args) {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("3dp");
        fileNames.add("4dp");
        fileNames.add("5dp");
//        fileNames.add("3dp-centroid");
//        fileNames.add("4dp-centroid");
//        fileNames.add("5dp-centroid");
        for (String name : fileNames) {
            fileName = name;
            analyze();
        }
    }

    public static void analyze() {
        File directory = new File(path + "\\" + fileName);
        String extension = ".aird";
        String extensionJson = ".json";
        String extensionCJson = ".cjson";
        File[] files = directory.listFiles();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Long> fileSizes = new ArrayList<>();
        ArrayList<String> jsonnames = new ArrayList<>();
        ArrayList<Long> jsonfileSizes = new ArrayList<>();
        ArrayList<String> cjsonnames = new ArrayList<>();
        ArrayList<Long> cjsonfileSizes = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(extension)) {
                System.out.println("File name: " + file.getName() + ", Size: " + file.length() + " bytes");
                names.add(file.getName());
                fileSizes.add(file.length());
            }
            if (file.isFile() && file.getName().endsWith(extensionJson)) {
                System.out.println("File name: " + file.getName() + ", Size: " + file.length() + " bytes");
                jsonnames.add(file.getName());
                jsonfileSizes.add(file.length());
            }
            if (file.isFile() && file.getName().endsWith(extensionCJson)) {
                System.out.println("File name: " + file.getName() + ", Size: " + file.length() + " bytes");
                cjsonnames.add(file.getName());
                cjsonfileSizes.add(file.length());
            }
        }
        writeExcel(names, fileSizes, jsonnames, jsonfileSizes, cjsonnames, cjsonfileSizes);
    }

    public static void writeExcel(ArrayList<String> importNames, ArrayList<Long> importFileSizes,
                                  ArrayList<String> importJsonNames, ArrayList<Long> importJsonFileSizes,
                                  ArrayList<String> importCJsonNames, ArrayList<Long> importCJsonFileSizes) {
        String[] title = {"AirdFileNames", "AirdFileSize", "JsonFileNames", "JsonFileSize", "CJsonFileNames", "CJsonFileSize"};
        File fileSizeSummary = new File(path + "\\" + fileName + ".xlsx");
        if (fileSizeSummary.exists()) {
            fileSizeSummary.delete();
        }
        try {
            fileSizeSummary.createNewFile();
            WritableWorkbook workbook = Workbook.createWorkbook(fileSizeSummary);
            WritableSheet worksheet = workbook.createSheet("sheet1", 0);
            Label workLabel = null;
//            for (int i = 0; i < title.length; i++){
//                workLabel = new Label(i,0,title[i]);
//                worksheet.addCell(workLabel);
//            }
            for (int j = 0; j < importNames.size(); j++) {
                workLabel = new Label(0, j, importNames.get(j));
                worksheet.addCell(workLabel);
            }
            for (int k = 0; k < importFileSizes.size(); k++) {
                workLabel = new Label(1, k, (importFileSizes.get(k)).toString());
                worksheet.addCell(workLabel);
            }
            for (int l = 0; l < importJsonNames.size(); l++) {
                workLabel = new Label(2, l, importJsonNames.get(l));
                worksheet.addCell(workLabel);
            }
            for (int m = 0; m < importJsonFileSizes.size(); m++) {
                workLabel = new Label(3, m, (importJsonFileSizes.get(m)).toString());
                worksheet.addCell(workLabel);
            }
            for (int i = 0; i < importCJsonNames.size(); i++) {
                workLabel = new Label(4, i, importCJsonNames.get(i));
                worksheet.addCell(workLabel);
            }
            for (int n = 0; n < importCJsonFileSizes.size(); n++) {
                workLabel = new Label(5, n, (importCJsonFileSizes.get(n)).toString());
                worksheet.addCell(workLabel);
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            System.out.println("File read has errors!");
        }
    }

}