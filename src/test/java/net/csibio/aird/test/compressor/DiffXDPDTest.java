package net.csibio.aird.test.compressor;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class DiffXDPDTest {

    static HashMap<String, String> fileMap = new HashMap<>();
    static HashMap<String, List<Spectrum<float[], float[], float[]>>> spectrumListMap = new HashMap<>();
    static HashMap<String, List<byte[]>> spectrumBytesMap = new HashMap<>();
    static HashMap<String, List<int[]>> spectrumIntsMap = new HashMap<>();
    static int MB = 1024 * 1024;
    static int KB = 1024;

    @BeforeClass
    public static void init() throws Exception {
        initFile("File-DDA-Wiff",
                "D:\\Aird_Test\\SampleA_1_with_zero.json",
                AirdType.DDA,
                -1);
    }

    private static void initFile(String name, String indexPath, AirdType type, int indexNo)
            throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("开始初始化数据");
        fileMap.put(name, indexPath);
        List<Spectrum<float[], float[], float[]>> spectrumList = Collections.synchronizedList(new ArrayList<>());
        double precision = 0;

        switch (type) {
            case DIA -> {
                DIAParser parser = new DIAParser(indexPath);
                precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_MZ).getPrecision();

                AirdInfo airdInfo = parser.getAirdInfo();
                List<BlockIndex> indexList = airdInfo.getIndexList();

                if (indexNo != -1) {
                    BlockIndex index = indexList.get(indexNo);
                    TreeMap<Float, Spectrum<float[], float[], float[]>> spectrumMap = parser.getSpectraAsFloat(index);
                    spectrumList.addAll(spectrumMap.values().stream().toList());
                } else {
                    indexList.forEach(index -> {
                        TreeMap<Float, Spectrum<float[], float[], float[]>> spectrumMap = parser.getSpectraAsFloat(index);
                        spectrumList.addAll(spectrumMap.values().stream().toList());
                    });
                }
            }
            case DDA -> {
                DDAParser parser = new DDAParser(indexPath);
                precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_MZ).getPrecision();
                List<DDAMs<float[], float[], float[]>> ms1List = parser.readAllToMemory();
                ms1List.forEach(ms1 -> {
                    spectrumList.add(ms1.getSpectrum());
                    if (ms1.getMs2List() != null && ms1.getMs2List().size() != 0) {
                        for (DDAMs<float[], float[], float[]> ms2 : ms1.getMs2List()) {
                            spectrumList.add(ms2.getSpectrum());
                        }
                    }
                });
            }
        }

        spectrumListMap.put(name, spectrumList);
        List<byte[]> bytesList = new ArrayList<>();
        List<int[]> intsList = new ArrayList<>();
        for (int i = 0; i < spectrumList.size(); i++) {
            bytesList.add(ByteTrans.floatToByte(spectrumList.get(i).getMzs()));
            int[] ints = new int[spectrumList.get(i).getMzs().length];
            for (int j = 0; j < spectrumList.get(i).getMzs().length; j++) {
                ints[j] = (int) (spectrumList.get(i).getMzs()[j] * precision);
            }
            intsList.add(ints);
        }
        spectrumIntsMap.put(name, intsList);
        spectrumBytesMap.put(name, bytesList);
        System.out.println("数据初始化完毕,耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    @Test
    public void test_DIA() {
        spectrumListMap.forEach((key, value) -> {
            System.out.println(
                    "----------------------开始测试文件:" + key + "包含光谱共" + spectrumIntsMap.get(key).size()
                            + "张-------------------------");
            List<byte[]> bytesList = spectrumBytesMap.get(key);
            List<Spectrum<float[], float[], float[]>> spectrumList = spectrumListMap.get(key);
            List<int[]> intsList = spectrumIntsMap.get(key);

            //测试原始大小
            AtomicLong originSize = new AtomicLong(0);
            bytesList.parallelStream().forEach(bytes -> {
                originSize.getAndAdd(bytes.length);
            });
            System.out.println("原始大小:" + originSize.get() / MB + "M");

            test_zlib(bytesList);
        });
    }

    //Test for Zlib
    private void test_zlib(List<byte[]> bytesList) {
        long start = System.currentTimeMillis();
        AtomicLong compressedSize = new AtomicLong(0);
        bytesList.parallelStream().forEach(bytes -> {
            compressedSize.getAndAdd(new ZlibWrapper().encode(bytes).length);
        });
        System.out.println(
                "Zlib:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
                        + "M");
    }
}
