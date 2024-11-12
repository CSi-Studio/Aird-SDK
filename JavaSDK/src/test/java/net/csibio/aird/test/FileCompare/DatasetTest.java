package net.csibio.aird.test.FileCompare;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import net.csibio.aird.AirdManager;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ComboComp;
import net.csibio.aird.parser.BaseParser;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.util.ArrayUtil;
import net.csibio.aird.util.CsvUtil;
import net.csibio.aird.util.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class DatasetTest {

    static int fileNum = 73; //总计有73个文件
    static String vendorPath = "D:\\Aird2.0\\Vendor";
    static String zdpdPath = "D:\\Aird2.0\\ZDPD";
    static String ccPath = "D:\\Aird2.0\\Aird-NoComp";
    static String ccHighComp = "D:\\Aird2.0\\Aird-HighComp";
    static String ccHighDT = "D:\\Aird2.0\\Aird-HighDT";
    static String ccBalance = "D:\\Aird2.0\\Aird";
    static String vendor = "D:\\Aird2.0\\Vendor";

    @Test
    public void test() throws Exception {

        TreeMap<Integer, Integer[]> map = new TreeMap<>();
        map.put(10, generateUniqueRandomArray(0, 139054, 10));
        map.put(20, generateUniqueRandomArray(0, 139054, 20));
        map.put(50, generateUniqueRandomArray(0, 139054, 50));
        map.put(100, generateUniqueRandomArray(0, 139054, 100));
        map.put(200, generateUniqueRandomArray(0, 139054, 200));
        map.put(500, generateUniqueRandomArray(0, 139054, 500));
        map.put(1000, generateUniqueRandomArray(0, 139054, 1000));
        map.put(2000, generateUniqueRandomArray(0, 139054, 2000));
        map.put(5000, generateUniqueRandomArray(0, 139054, 5000));
        map.put(10000, generateUniqueRandomArray(0, 139054, 10000));
        map.put(20000, generateUniqueRandomArray(0, 139054, 20000));
        map.put(50000, generateUniqueRandomArray(0, 139054, 50000));
        DDAParser parser = new DDAParser("D:\\Aird2.0\\ZDPD\\18.json");
        map.forEach((k, v) -> {
            long start = System.currentTimeMillis();
            Spectrum[] spectrum = parser.getSpectraByNums(v);
            long delta = System.currentTimeMillis() - start;

            long length = 0;
            for (Spectrum s : spectrum) {
                length += s.getMzs().length;
            }
            System.out.print(length / delta + ",");

        });
        System.out.println();
        DDAParser parser1 = new DDAParser("D:\\Aird2.0\\Aird\\18.json");
        map.forEach((k, v) -> {
            long start = System.currentTimeMillis();
            Spectrum[] spectrum = parser1.getSpectraByNums(v);
            long delta = System.currentTimeMillis() - start;

            long length = 0;
            for (Spectrum s : spectrum) {
                length += s.getMzs().length;
            }
            System.out.print(length / delta + ",");
        });
        System.out.println();
    }


    /**
     * 论文中的图样
     * ggplot(data_df, aes(x = type, y = sizeRatio, fill = type)) +
     * +         geom_violin(trim = FALSE) +
     * +      geom_boxplot(width = 0.1, fill = 'white', outlier.shape = NA) +
     * +          labs(x = '', y = 'File Size Ratio (vs Vendor File Size)', title = 'A. File size ratio of Aird format to vendor format in different combinations') +
     * +          theme_minimal() + theme(axis.text.x = element_text(size = 12),
     * +                                  axis.text.y = element_text(size = 12),
     * +                                  plot.title = element_text(hjust = 0, size = 18),
     * +                                  axis.title.x = element_text(size = 16),
     * +                                  axis.title.y = element_text(size = 16),
     * +                                  legend.text = element_text(size = 16),
     * +                                  legend.title = element_blank(),
     * +                                  legend.position = "top")
     *
     * @throws Exception
     */
    @Test
    public void getCC() throws Exception {
        Map<Integer, Long> protoHighCompMap = scanFolder(ccHighComp, "index");
        Map<Integer, Long> protoHighCompMap2 = scanFolder(ccHighComp, "aird");
        Map<Integer, Long> protoHighDTMap = scanFolder(ccHighDT, "index");
        Map<Integer, Long> protoHighDTMap2 = scanFolder(ccHighDT, "aird");
        Map<Integer, Long> protoBalance = scanFolder(ccBalance, "index");
        Map<Integer, Long> protoBalance2 = scanFolder(ccBalance, "aird");
        Map<Integer, Long> vendorMap = scanFolder(vendor, null);

        int testSpectra = 2500;
        List<MsFile> msFiles = new ArrayList<>();
        for (int i = 1; i <= 58; i++) {
            MsFile file = new MsFile(i);
            int totalSpectra = 0;

            BaseParser parserHighComp = AirdManager.getInstance().load(ccHighComp + "/" + file.fileNo + ".index");
            totalSpectra = parserHighComp.getAirdInfo().getTotalCount().intValue();
            int midNum = totalSpectra / 2;
            long start = System.nanoTime();
            for (int k = (midNum - testSpectra); k < (midNum + testSpectra); k++) {
                Spectrum spectrum = parserHighComp.getSpectrum(midNum);
            }
            file.dtHighComp = System.nanoTime() - start;
            file.mzHighComp = String.join("-", parserHighComp.mzCompressor.getMethods());
            file.intensityHighComp = String.join("-", parserHighComp.intCompressor.getMethods());
            file.mobiHighComp = String.join("-", parserHighComp.mobiCompressor.getMethods());
            file.sizeHighComp = protoHighCompMap.get(i) + protoHighCompMap2.get(i);

            BaseParser parserHighDT = AirdManager.getInstance().load(ccHighDT + "/" + file.fileNo + ".index");
            start = System.nanoTime();
            for (int k = (midNum - testSpectra); k < (midNum + testSpectra); k++) {
                Spectrum spectrum = parserHighDT.getSpectrum(midNum);
            }
            file.dtHighDT = System.nanoTime() - start;
            file.mzHighDT = String.join("-", parserHighDT.mzCompressor.getMethods());
            file.intensityHighDT = String.join("-", parserHighDT.intCompressor.getMethods());
            file.mobiHighDT = String.join("-", parserHighDT.mobiCompressor.getMethods());
            file.sizeHighDT = protoHighDTMap.get(i) + protoHighDTMap2.get(i);

            file.sizeUp = (file.sizeHighDT - file.sizeHighComp) * 1.0 / file.sizeHighComp;
            file.dtUp = (file.dtHighComp - file.dtHighDT) * 1.0 / file.dtHighDT;

            BaseParser parser = AirdManager.getInstance().load(ccBalance + "/" + file.fileNo + ".index");
            start = System.nanoTime();
            for (int k = (midNum - testSpectra); k < (midNum + testSpectra); k++) {
                Spectrum spectrum = parser.getSpectrum(midNum);
            }
            file.vendor = vendorMap.get(i);
            file.dtBalance = System.nanoTime() - start;
            file.mzBalance = String.join("-", parser.mzCompressor.getMethods());
            file.intensityBalance = String.join("-", parser.intCompressor.getMethods());
            file.mobiBalance = String.join("-", parser.mobiCompressor.getMethods());
            file.sizeBalance = protoBalance.get(i) + protoBalance2.get(i);
            msFiles.add(file);
        }

        List<SelectedTarget> targets = new ArrayList<>();
        for (MsFile msFile : msFiles) {
            SelectedTarget targetHighComp = new SelectedTarget(msFile.getFileNo());
            targetHighComp.setType("High Compression Ratio");
            targetHighComp.setSizeRatio(msFile.getSizeHighComp() * 1.0 / msFile.getVendor());
            targetHighComp.setDtRatio(msFile.getDtHighComp() * 1.0 / msFile.getDtBalance());
            targets.add(targetHighComp);

            SelectedTarget targetHighDT = new SelectedTarget(msFile.getFileNo());
            targetHighDT.setType("High Decompression Time");
            targetHighDT.setSizeRatio(msFile.getSizeHighDT() * 1.0 / msFile.getVendor());
            targetHighDT.setDtRatio(msFile.getDtHighDT() * 1.0 / msFile.getDtBalance());
            targets.add(targetHighDT);

            SelectedTarget targetBalance = new SelectedTarget(msFile.getFileNo());
            targetBalance.setType("Balance");
            targetBalance.setSizeRatio(msFile.getSizeBalance() * 1.0 / msFile.getVendor());
            targetBalance.setDtRatio(msFile.getDtBalance() * 1.0 / msFile.getDtBalance());
            targets.add(targetBalance);
        }


        System.out.println("总计文件：" + msFiles.size());
        String csv = CsvUtil.toCsv(msFiles);
        Files.write(Paths.get("D:\\dataCCCompare.csv"), csv.getBytes(StandardCharsets.UTF_8));
        System.out.println("CSV文件写入成功。");

        String json = JSON.toJSONString(targets);
        Files.write(Paths.get("D:\\dataCCCompare.json"), json.getBytes(StandardCharsets.UTF_8));
        System.out.println("JSON文件写入成功。");
    }

    @Test
    public void test2() throws IOException {
        List<Map<String, Object>> list = CsvUtil.readCSV("D:\\dataCCCompare.csv");
        List<SelectedTarget> targets = new ArrayList<>();
        List<Double> sizeUp = new ArrayList<>();
        List<Double> dtUp = new ArrayList<>();

        for (Map<String, Object> map : list) {
            sizeUp.add(Double.parseDouble(map.get("sizeHighComp").toString())/Double.parseDouble(map.get("sizeHighDT").toString()));
            dtUp.add(Double.parseDouble(map.get("dtHighComp").toString())/Double.parseDouble(map.get("dtHighDT").toString()));
            SelectedTarget targetHighComp = new SelectedTarget(Integer.parseInt(map.get("fileNo").toString()));
            targetHighComp.setType("Smaller Size");
            targetHighComp.setSizeRatio(Double.parseDouble(map.get("HighCompRatio").toString()));
            targetHighComp.setDtRatio(Double.parseDouble(map.get("highCompVsBalance").toString()));
            targets.add(targetHighComp);

            SelectedTarget targetHighDT = new SelectedTarget(Integer.parseInt(map.get("fileNo").toString()));
            targetHighDT.setType("Less Decompression Time");
            targetHighDT.setSizeRatio(Double.parseDouble(map.get("HighDTRatio").toString()));
            targetHighDT.setDtRatio(Double.parseDouble(map.get("highDTVsBalance").toString()));
            targets.add(targetHighDT);

            SelectedTarget targetBalance = new SelectedTarget(Integer.parseInt(map.get("fileNo").toString()));
            targetBalance.setType("Balance");
            targetBalance.setSizeRatio(Double.parseDouble(map.get("BalanceRatio").toString()));
            targetBalance.setDtRatio(1);
            targets.add(targetBalance);
        }

        System.out.println("SizeUP:"+ ArrayUtil.join(",", sizeUp));
        System.out.println("DTUP:"+ArrayUtil.join(",", sizeUp));
        System.out.println("MaxMinAve for Size Up List:");
        System.out.println("MaxMinAve for DT Up List:");
        String json = JSON.toJSONString(targets);
        Files.write(Paths.get("D:\\dataCCCompare-new.json"), json.getBytes(StandardCharsets.UTF_8));
        System.out.println("JSON文件写入成功。");
    }



    public Integer[] generateUniqueRandomArray(int minValue, int maxValue, int count) {
        if (maxValue - minValue + 1 < count) {
            throw new IllegalArgumentException("Range is too small to generate the requested number of unique integers.");
        }

        List<Integer> numbers = new ArrayList<>();
        for (int i = minValue; i <= maxValue; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers, new Random()); // 随机打乱列表中的元素
        return numbers.subList(0, count).toArray(new Integer[0]); // 取前count个元素转为数组
    }



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
        AirdManager.getInstance().load(zdpdPath + "/" + 15 + ".json");
        AirdManager.getInstance().load(zdpdPath + "/" + 15 + ".index");
        AirdManager.getInstance().load(ccPath + "/" + 15 + ".json");
        AirdManager.getInstance().load(ccPath + "/" + 15 + ".index");
        System.out.println("代码预热结束");

        Map<Integer, Long> vendorMap = scanFolder(vendorPath, null);
        Map<Integer, Long> zdpdMap = scanFolder(zdpdPath, null);
        Map<Integer, Long> ccMap = scanFolder(ccPath, null);
        Map<Integer, Long> zdpdJsonMap = scanFolder(zdpdPath, "json");
        Map<Integer, Long> ccJsonMap = scanFolder(ccPath, "json");
        Map<Integer, Long> ccProtoMap = scanFolder(ccPath, "index");
        TreeMap<Integer, MsFile> fileMap = new TreeMap<>();
        System.out.println("FileNo, m/z, intensity, ion mobility");
        for (int i = 1; i <= 58; i++) {
            MsFile file = new MsFile(i);
            try {
                long start = 0;

                int total = 5;
                long time = 0;
                for (int j = 0; j < total; j++) {
                    start = System.nanoTime();
                    BaseParser parser1 = AirdManager.getInstance().load(ccPath + "/" + file.fileNo + ".json");
                    time += System.nanoTime() - start;
                }
                file.dtJson = time / total;

                time = 0;
                for (int j = 0; j < total; j++) {
                    start = System.nanoTime();
                    BaseParser parser2 = AirdManager.getInstance().load(zdpdPath + "/" + file.fileNo + ".json");
                    time += System.nanoTime() - start;

                }
                file.dtZdpdJson = time / total;

                time = 0;
                BaseParser parser = null;
                for (int j = 0; j < total; j++) {
                    start = System.nanoTime();
                    parser = AirdManager.getInstance().load(ccPath + "/" + file.fileNo + ".index");
                    time += System.nanoTime() - start;
                }
                file.dtProto = time / total;

                file.ccJsonCompressedSize = (parser.getAirdInfo().getIndexEndPtr() - parser.getAirdInfo().getIndexStartPtr()) / 1024d;
                file.acquisitionMethod = parser.getType();
                file.manufacturer = parser.getAirdInfo().getInstruments().get(0).getManufacturer();
                file.spectraCount = parser.getAirdInfo().getTotalCount();

                file.vendor = vendorMap.get(i) / 1024 / 1024; //MB
                file.zdpd = zdpdMap.get(i) / 1024 / 1024; //MB
                file.comboComp = ccMap.get(i) / 1024 / 1024; //MB
                file.zdpdJsonSize = zdpdJsonMap.get(i) / 1024d;  //KB
                file.ccJsonSize = ccJsonMap.get(i) / 1024d; //KB
                file.ccProtoSize = ccProtoMap.get(i) / 1024d; //KB
                file.jsonZdpdVsCC = file.zdpdJsonSize / (file.ccJsonSize + file.ccJsonCompressedSize);
                file.jsonVsProto = file.ccJsonSize / file.ccProtoSize;
                file.dtZdpdVsCC = file.dtZdpdJson * 1.0 / file.dtJson;
                file.dtJsonVsProto = file.dtJson * 1.0 / file.dtProto;

                file.mzCC = String.join("-", parser.mzCompressor.getMethods());
                file.intensityCC = String.join("-", parser.intCompressor.getMethods());
                file.mobiCC = String.join("-", parser.mobiCompressor.getMethods());
                file.rtCC = String.join("-", parser.rtCompressor.getMethods());
                System.out.println(file.fileNo + "," + file.mzCC + "," + file.intensityCC + "," + file.mobiCC);
                file.tag = file.fileNo + "-" + file.manufacturer + "-" + file.acquisitionMethod;
            } catch (Exception e) {
                continue;
            }

            fileMap.put(i, file);
        }
        mergeAllCt(fileMap);
        mergeFileSize(fileMap);
        List<MsFile> fileList = new ArrayList<>(fileMap.values());

        System.out.println("总计文件：" + fileMap.size());
        String csv = CsvUtil.toCsv(fileList);
        Files.write(Paths.get("D:\\data.csv"), csv.getBytes(StandardCharsets.UTF_8));
        System.out.println("CSV文件写入成功。");

        String json = JSON.toJSONString(fileList);
        Files.write(Paths.get("D:\\data.json"), json.getBytes(StandardCharsets.UTF_8));
        System.out.println("JSON文件写入成功。");
    }

    public static void mergeFileSize(TreeMap<Integer, MsFile> fileMap) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader("D:\\FileSize.csv"))) {
            String[] line = reader.readNext();
            while ((line = reader.readNext()) != null) {
                try {
                    MsFile file = fileMap.get(Integer.parseInt(line[0]));
                    file.setMzML(Long.parseLong(line[8]) / 1024 / 1024);
                    file.setMzMLb(Long.parseLong(line[10]) / 1024 / 1024);
                    file.setMzML_Numpress(Long.parseLong(line[12]) / 1024 / 1024);
                    file.setMzMLb_Numpress(Long.parseLong(line[14]) / 1024 / 1024);
                    file.setCtMzML(Integer.parseInt(line[7]) / 1000);
                    file.setCtMzMLb(Integer.parseInt(line[9]) / 1000);
                    file.setCtMzMLNum(Integer.parseInt(line[11]) / 1000);
                    file.setCtMzMLbNum(Integer.parseInt(line[13]) / 1000);
                    try {
                        file.setMspack(Long.parseLong(line[16]) / 1024 / 1024);
                        file.setCtMspack(Integer.parseInt(line[15]) / 1000);
                    } catch (Exception e) {
                        file.setMspack(0);
                        file.setCtMspack(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mergeAllCt(TreeMap<Integer, MsFile> fileMap) {
        String json = FileUtil.readFile("D:\\job.json");
        JSONArray array = JSON.parseArray(json);
        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            String fileNo = jsonObject.getString("AirdFileName:");
            MsFile file = fileMap.get(Integer.parseInt(fileNo));
            String conversionTimeStr = jsonObject.getString("ConversionTime:");
            int conversionTime = convertTimeToSeconds(conversionTimeStr);
            int predictionTime = (int) (jsonObject.getDouble("PredictionTime:") / 1000);
            if (jsonObject.getString("ConfigName:").equals("ZDPD")) {
                file.setCtZdpd(conversionTime);
            }
            if (jsonObject.getString("ConfigName:").equals("Aird2")) {
                file.setCtCC(conversionTime - predictionTime);
            }
            file.compressor = jsonObject.getString("Compressor:");
        }
    }

    public static int convertTimeToSeconds(String timeStr) {
        // 分割时间字符串
        String[] parts = timeStr.split(":");

        // 检查时间格式是否正确（假设是hh:mm:ss格式，至少需要有三部分）
        if (parts.length != 3) {
            return Integer.parseInt(timeStr.replace("ms", "")) / 1000;
        }

        // 将每部分转换为整数
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        // 计算总秒数
        int totalSeconds = hours * 3600 + minutes * 60 + seconds;

        return totalSeconds;
    }

}
