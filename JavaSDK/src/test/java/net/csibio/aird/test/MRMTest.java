package net.csibio.aird.test;

import com.alibaba.fastjson2.JSON;
import io.github.msdk.datamodel.Chromatogram;
import net.csibio.aird.bean.ChromatogramIndex;
import net.csibio.aird.bean.common.MrmPair;
import net.csibio.aird.bean.common.RawFileInfo;
import net.csibio.aird.parser.MRMParser;
import net.csibio.aird.test.mzml.MzMLFileImportMethod;
import net.csibio.aird.test.mzml.data.MzMLRawDataFile;
import net.csibio.aird.util.FileSizeUtil;
import net.csibio.aird.util.FileUtil;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import static com.alibaba.fastjson2.JSON.toJSONString;

public class MRMTest {

    @Test
    public void testMRM() throws Exception {
//        MRMParser parser = new MRMParser("D:\\TestMRM\\CDCA-STD-20230512-BLKIS.json");
        MRMParser parser = new MRMParser("D:\\TestMRM\\CDCA-STD-20230512-BLK (2).json");
        ChromatogramIndex index = parser.getChromatogramIndex();
        List<MrmPair> pairs = parser.getAllMrmPairs();
        System.out.println(toJSONString(pairs.get(50).getRts()));
        System.out.println(toJSONString(pairs.get(50).getInts()));
    }

    @Test
    public void testMRMFileSize() throws Exception {
        String[] formats = new String[]{"aird", "mzML", "vendor"};
        String[] datasets = new String[]{"PXD031038", "PXD009543"};
        String path = "D:\\MRMProTest\\";
        for (String format : formats) {
            TreeMap<String, Long> map = new TreeMap<>();
            for (String dataset : datasets) {
                String finalPath = path + format + "\\" + dataset;
                TreeMap<String, Long> sizeMap = FileSizeUtil.sum(finalPath);
                sizeMap.forEach((key, value) -> {
                    map.put(key, value / 1024);
                });
            }
            System.out.println(format);
            System.out.println(JSON.toJSONString(map.keySet()));
            System.out.println(map.values());
            System.out.println("");
        }
    }

    @Test
    public void testMRMFileReadingSpeed() throws Exception {
        String[] formats = new String[]{"AirdRepository"};
        String[] datasets = new String[]{"SmallMoleculeQuantification", "PXD031038", "PXD009543"};
        String path = "E:\\";

        for (String format : formats) {
            Long timeT = 0L;
            Long eicT = 0L;
            Long filesT = 0L;
            for (String dataset : datasets) {
                Long time = 0L;
                Long eic = 0L;
                String rootPath = path + format + "\\" + dataset;
                File root = new File(rootPath);
                File[] files = root.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith("json")) {
                        long start = System.currentTimeMillis();
                        MRMParser parser = new MRMParser(file.getPath());
                        List<MrmPair> pairs = parser.getAllMrmPairs();
                        time += (System.currentTimeMillis() - start);
                        eic += pairs.size();
                    }
                }
                System.out.println("数据集：" + dataset);
                System.out.println("总计耗时：" + time);
                System.out.println("总计色谱图：" + eic);
                System.out.println("单色谱读取耗时:" + time * 1.0 / eic);
                System.out.println("单文件读取耗时:" + time * 1.0 / files.length);
                timeT += time;
                eicT += eic;
                filesT += files.length;
            }
            System.out.println("格式：" + format);
            System.out.println("总计耗时：" + timeT);
            System.out.println("总计色谱图：" + eicT);
            System.out.println("单色谱读取耗时:" + timeT * 1.0 / eicT);
            System.out.println("单文件读取耗时:" + timeT * 1.0 / filesT);
        }
    }

    @Test
    public void testMzMLMRMFileReadingSpeed() throws Exception {
        String[] formats = new String[]{"MzMLRepository"};
        String[] datasets = new String[]{"SmallMoleculeQuantification", "PXD031038", "PXD009543"};
        String path = "E:\\";

        for (String format : formats) {
            Long timeT = 0L;
            Long eicT = 0L;
            Long filesT = 0L;
            for (String dataset : datasets) {
                Long time = 0L;
                Long eic = 0L;
                String rootPath = path + format + "\\" + dataset;
                File root = new File(rootPath);
                File[] files = root.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".mzML")) {
                        long start = System.currentTimeMillis();
                        MzMLFileImportMethod method = new MzMLFileImportMethod(file.getPath());
                        MzMLRawDataFile mzMLFile = method.execute();
                        List<Chromatogram> eics = mzMLFile.getChromatograms();
                        eics.forEach(chromatogram -> {
                            chromatogram.getIntensityValues();
                            chromatogram.getRetentionTimes();
                        });
                        time += (System.currentTimeMillis() - start);
                        eic += eics.size();
                    }
                }
                System.out.println("数据集：" + dataset);
                System.out.println("总计耗时：" + time);
                System.out.println("总计色谱图：" + eic);
                System.out.println("单色谱读取耗时:" + time * 1.0 / eic);
                System.out.println("单色谱读取耗时:" + time * 1.0 / files.length);
                timeT += time;
                eicT += eic;
                filesT += files.length;
            }
            System.out.println("格式：" + format);
            System.out.println("总计耗时：" + timeT);
            System.out.println("总计色谱图：" + eicT);
            System.out.println("单色谱读取耗时:" + timeT * 1.0 / eicT);
            System.out.println("单文件读取耗时:" + timeT * 1.0 / filesT);
        }
    }
}
