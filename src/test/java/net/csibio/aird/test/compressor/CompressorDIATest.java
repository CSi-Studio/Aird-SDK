package net.csibio.aird.test.compressor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.SpectrumF;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytes.Brotli;
import net.csibio.aird.compressor.bytes.Gzip;
import net.csibio.aird.compressor.bytes.LZMA2;
import net.csibio.aird.compressor.bytes.Snappier;
import net.csibio.aird.compressor.bytes.Zlib;
import net.csibio.aird.parser.v2.DIAParser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xerial.snappy.BitShuffle;

public class CompressorDIATest {

  static HashMap<String, String> fileMap = new HashMap<>();
  static HashMap<String, DIAParser> parserMap = new HashMap<>();
  static HashMap<String, List<SpectrumF>> spectrumListMap = new HashMap<>();
  static HashMap<String, List<byte[]>> spectrumBytesMap = new HashMap<>();
  static HashMap<String, List<int[]>> spectrumIntsMap = new HashMap<>();
  static int MB = 1024 * 1024;
  static int KB = 1024;

  @BeforeClass
  public static void init() throws IOException {
    initFile("File-A",
        "D:\\proteomics\\Project\\HYE_124_64var-6600\\HYE124_TTOF6600_64var_lgillet_I150211_008.json",
        16);
  }

  private static void initFile(String name, String indexPath, int indexNo) throws IOException {
    long start = System.currentTimeMillis();
    System.out.println("开始初始化数据");
    fileMap.put(name, indexPath);
    DIAParser parser = new DIAParser(indexPath);
    double precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_MZ).getPrecision();
    parserMap.put(name, parser);
    AirdInfo airdInfo6600 = parser.getAirdInfo();
    List<BlockIndex> indexList = airdInfo6600.getIndexList();
    BlockIndex index = indexList.get(indexNo);
    TreeMap<Float, SpectrumF> spectrumMap = parser.getSpectrumsAsFloat(index);
    List<SpectrumF> spectrumList = spectrumMap.values().stream().toList();
    spectrumListMap.put(name, spectrumList);
    List<byte[]> bytesList = new ArrayList<>();
    List<int[]> intsList = new ArrayList<>();
    for (int i = 0; i < spectrumList.size(); i++) {
      bytesList.add(ByteTrans.floatToByte(spectrumList.get(i).mzs()));
      int[] ints = new int[spectrumList.get(i).mzs().length];
      for (int j = 0; j < spectrumList.get(i).mzs().length; j++) {
        ints[j] = (int) (spectrumList.get(i).mzs()[j] * precision);
      }
      intsList.add(ints);
    }
    spectrumIntsMap.put(name, intsList);
    spectrumBytesMap.put(name, bytesList);
    System.out.println("数据初始化完毕,耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
  }

  @Test
  public void test_DIA() {
    parserMap.forEach((key, value) -> {
      System.out.println("开始测试文件:" + key + "-------------------------");
      List<byte[]> bytesList = spectrumBytesMap.get(key);
      List<SpectrumF> spectrumList = spectrumListMap.get(key);
      List<int[]> intsList = spectrumIntsMap.get(key);

      //测试原始大小
      AtomicLong originSize = new AtomicLong(0);
      bytesList.parallelStream().forEach(bytes -> {
        originSize.getAndAdd(bytes.length);
      });
      System.out.println("原始大小:" + originSize.get() / MB + "M");

      test_zlib(bytesList);
      test_brotli(bytesList);
      test_gzip(bytesList);
      test_snappy(intsList);
//      test_LZMA2(bytesList);
    });

  }

  //Test for Brotli
  private void test_brotli(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      byte[] compressed = Brotli.encode(bytes);
      compressedSize.getAndAdd(compressed.length);
    });
    System.out.println(
        "Brotli:" + (System.currentTimeMillis() - start) / 1000 + "秒|" + compressedSize.get() / MB
            + "M");
  }

  //Test for Zlib
  private void test_zlib(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(Zlib.encode(bytes).length);
    });
    System.out.println(
        "Zlib:" + (System.currentTimeMillis() - start) / 1000 + "秒|" + compressedSize.get() / MB
            + "M");
  }

  //Test for Gzip
  private void test_gzip(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(Gzip.encode(bytes).length);
    });
    System.out.println(
        "Gzip:" + (System.currentTimeMillis() - start) / 1000 + "秒|" + compressedSize.get() / MB
            + "M");
  }

  //Test for Snappy
  private void test_snappy(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      try {
        byte[] bytes = BitShuffle.shuffle(ints);
        compressedSize.getAndAdd(Snappier.encode(bytes).length);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    System.out.println(
        "Snappy:" + (System.currentTimeMillis() - start) / 1000 + "秒|" + compressedSize.get() / MB
            + "M");
  }

  //Test for LZMA2
  private void test_LZMA2(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(LZMA2.encode(bytes).length);
    });
    System.out.println(
        "LZMA2:" + (System.currentTimeMillis() - start) / 1000 + "秒|" + compressedSize.get() / MB
            + "M");
  }
}
