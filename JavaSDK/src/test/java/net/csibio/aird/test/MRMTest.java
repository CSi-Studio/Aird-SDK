package net.csibio.aird.test;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.ChromatogramIndex;
import net.csibio.aird.bean.common.MrmPair;
import net.csibio.aird.parser.MRMParser;
import net.csibio.aird.util.FileSizeUtil;
import org.junit.Test;

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
        String[] formats = new String[]{"aird", "mzML", "vendor"};
        String[] datasets = new String[]{"SmallMoleculeQuantification", "PXD031038", "PXD009543"};
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
}
