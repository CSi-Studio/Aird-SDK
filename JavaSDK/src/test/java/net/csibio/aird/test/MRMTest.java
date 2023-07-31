package net.csibio.aird.test;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.ChromatogramIndex;
import net.csibio.aird.bean.common.MrmPair;
import net.csibio.aird.parser.MRMParser;
import net.csibio.aird.testutil.FileSizeUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

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
    public void testMRMFileSize() {
        String path = "D:\\MRMProTest\\aird\\SmallMoleculeQuantification";
//        String path = "D:\\MRMProTest\\mzML\\PXD031038";
//        String path = "D:\\MRMProTest\\mzML\\PXD009543";
        HashMap<String, Long> sizeMap = FileSizeUtil.sumSizes(path);

        sizeMap.forEach((key,value)->{
            sizeMap.put(key, value/1024);
        });
        System.out.println(sizeMap.values());
    }
}
