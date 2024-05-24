package net.csibio.aird.test.FileCompare;

import net.csibio.aird.AirdManager;
import net.csibio.aird.parser.BaseParser;
import net.csibio.aird.util.CsvUtil;
import net.csibio.aird.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DatasetTest {

    static int fileNum = 73; //总计有73个文件
    static String vendorPath = "D:\\Aird2.0\\Vendor";
    static String zdpdPath = "D:\\Aird2.0\\ZDPD";
    static String ccPath = "D:\\Aird2.0\\Aird";

    public static Map<Integer, Long> scanFolder(String folderPath, String targetSuffix) {
        Map<Integer, Long> results = new TreeMap<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (targetSuffix != null && !file.getName().endsWith(targetSuffix)) {
                    continue;
                }
                if (file.isDirectory()) {
                    // 如果是文件夹,递归扫描该文件夹
                    long totalSize = FileUtil.getFolderSize(file);
                    String fileName = file.getName();
                    int firstDot = fileName.indexOf(".");
                    int fileNum = Integer.parseInt(fileName.substring(0, firstDot));
                    results.put(fileNum, totalSize);
                } else {
                    // 如果是文件,则合并同名文件的大小
                    String fileName = file.getName();
                    int firstDot = fileName.indexOf(".");
                    int fileNum = Integer.parseInt(fileName.substring(0, firstDot));
                    long fileSize = file.length();
                    if (results.containsKey(fileNum)) {
                        fileSize += results.get(fileNum);
                    }
                    results.put(fileNum, fileSize);
                }
            }
        }
        return results;
    }

    public static void main(String[] args) throws Exception {

        //预热所有的编码
        AirdManager.getInstance().load(ccPath + "/" + 5 + ".json");
        AirdManager.getInstance().load(ccPath + "/" + 5 + ".index");
        System.out.println("代码预热结束");

        Map<Integer, Long> vendorMap = scanFolder(vendorPath, null);
        Map<Integer, Long> zdpdJsonMap = scanFolder(zdpdPath, "json");
        Map<Integer, Long> zdpdMap = scanFolder(zdpdPath, null);
        Map<Integer, Long> ccMap = scanFolder(ccPath, null);
        Map<Integer, Long> ccJsonMap = scanFolder(ccPath, "json");
        Map<Integer, Long> ccProtoMap = scanFolder(ccPath, "index");
        TreeMap<Integer, MsFile> fileMap = new TreeMap<>();
        List<MsFile> fileList = new ArrayList<>();
        for (int i = 1; i <= 58; i++) {
            MsFile file = new MsFile(i);
            try {
                long start = System.currentTimeMillis();
                AirdManager.getInstance().load(zdpdPath + "/" + file.fileNo + ".json");
                file.dtZdpdJson = System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
                AirdManager.getInstance().load(ccPath + "/" + file.fileNo + ".json");
                file.dtJson = System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
                BaseParser parser = AirdManager.getInstance().load(ccPath + "/" + file.fileNo + ".index");
                file.dtProto = System.currentTimeMillis() - start;
                file.acquisitionMethod = parser.getType();
                file.manufacturer = parser.getAirdInfo().getInstruments().get(0).getManufacturer();
                file.spectraCount = parser.getAirdInfo().getTotalCount();

                file.vendorSize = vendorMap.get(i) / 1024d / 1024; //MB
                file.zdpdSize = zdpdMap.get(i) / 1024d / 1024; //MB
                file.ccSize = ccMap.get(i) / 1024d / 1024; //MB
                file.zdpdJsonSize = zdpdJsonMap.get(i) / 1024d;  //KB
                file.ccJsonSize = ccJsonMap.get(i) / 1024d; //KB
                file.ccProtoSize = ccProtoMap.get(i) / 1024d; //KB

            } catch (Exception e) {
                continue;
            }

            fileMap.put(i, file);
            fileList.add(file);
        }
        System.out.println("总计文件：" + fileMap.size());
        String csvString = CsvUtil.toCsv(fileList);
        Files.write(Paths.get("D:\\data.csv"), csvString.getBytes(StandardCharsets.UTF_8));
        System.out.println("文件写入成功，内容已被覆盖。");
    }
}
