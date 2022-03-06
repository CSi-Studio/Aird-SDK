package net.csibio.aird.test.compressor;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class 解压缩时间 {

    static HashMap<String, String> fileMap = new HashMap<>();
    static HashMap<String, List<DDAMs>> dataListDDAMap = new HashMap<>();
    static HashMap<String, TreeMap> dataListDIAMap = new HashMap<>();

    static int MB = 1024 * 1024;
    static int KB = 1024;

    @BeforeClass
    public static void init() throws Exception {

//        initFile("DDA-IVB-Zstd-Zstd",
//                "C:\\Users\\lms19\\Desktop\\AirdTest\\SampleB_4_IVB_Zstd_Zstd.json",
//                AirdType.DDA,
//                -1);
//        initFile("DDA-IVB-Zlib-Zlib",
//                "C:\\Users\\lms19\\Desktop\\AirdTest\\SampleB_4_IVB_Zlib_Zlib.json",
//                AirdType.DDA,
//                -1);
//        initFile("DDA-IVB-Sna-Sna",
//                "C:\\Users\\lms19\\Desktop\\AirdTest\\SampleB_4_IVB_Sna_Sna.json",
//                AirdType.DDA,
//                -1);

//        initFile("DDA-IVB-Bro-Bro",
//                "C:\\Users\\lms19\\Desktop\\AirdTest\\SampleB_4_IVB_Bro_Bro.json",
//                AirdType.DDA,
//                -1);

//        initFile("DDA-IBP-Zlib-Zlib",
//                "C:\\Users\\lms19\\Desktop\\AirdTest\\SampleB_4_IBP_Zlib_Zlib.json",
//                AirdType.DDA,
//                -1);

        initFile("DIA-IVB-Zlib-Zlib",
                "C:\\Users\\lms19\\Desktop\\AirdTest\\C20181208yix_HCC_DIA_T_46A_IVB_Zlib_Zlib.json",
                AirdType.DIA,
                -1);
        initFile("DIA-IVB-Zstd-Zstd",
                "C:\\Users\\lms19\\Desktop\\AirdTest\\C20181208yix_HCC_DIA_T_46A_IVB_Zstd_Zstd.json",
                AirdType.DIA,
                12);
        initFile("DIA-IBP-Zlib-Zlib",
                "C:\\Users\\lms19\\Desktop\\AirdTest\\C20181208yix_HCC_DIA_T_46A_IBP_Zlib_Zlib.json",
                AirdType.DIA,
                -1);
        initFile("DIA-IVB-Sna-Sna",
                "C:\\Users\\lms19\\Desktop\\AirdTest\\C20181208yix_HCC_DIA_T_46A_IVB_Sna_Sna.json",
                AirdType.DIA,
                -1);
        initFile("DIA-IVB-Bro-Bro",
                "C:\\Users\\lms19\\Desktop\\AirdTest\\C20181208yix_HCC_DIA_T_46A_IVB_Bro_Bro.json",
                AirdType.DIA,
                -1);
    }

    private static void initFile(String name, String indexPath, AirdType type, int indexNo)
            throws Exception {
        long start = System.currentTimeMillis();
        fileMap.put(name, indexPath);
        switch (type) {
            case DIA -> {
                DIAParser parser = new DIAParser(indexPath);
                AirdInfo airdInfo = parser.getAirdInfo();
                List<BlockIndex> indexList = airdInfo.getIndexList();
                if (indexNo != -1) {
                    BlockIndex index = indexList.get(indexNo);
                    TreeMap map = parser.getSpectraAsFloat(index);
                    dataListDIAMap.put(name, map);
                } else {
                    indexList.forEach(parser::getSpectraAsFloat);
                }
            }
            case DDA -> {
                DDAParser parser = new DDAParser(indexPath);
                List<DDAMs> msList = parser.readAllToMemory();
                dataListDDAMap.put(name, msList);
            }
        }
        System.out.println("初始化" + name + "完毕,耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    @Test
    public void test() {
        System.out.println(dataListDDAMap.size());
        System.out.println(dataListDIAMap.size());
    }

}
