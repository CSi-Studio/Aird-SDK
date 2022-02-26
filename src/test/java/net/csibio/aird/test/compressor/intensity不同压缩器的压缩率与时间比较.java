package net.csibio.aird.test.compressor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.CompressorType;
import net.csibio.aird.compressor.bytes.Brotli;
import net.csibio.aird.compressor.bytes.Gzip;
import net.csibio.aird.compressor.bytes.LZ4;
import net.csibio.aird.compressor.bytes.Snappier;
import net.csibio.aird.compressor.bytes.ZSTD;
import net.csibio.aird.compressor.bytes.Zlib;
import net.csibio.aird.compressor.ints.XVByte;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.ArrayUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class intensity不同压缩器的压缩率与时间比较 {

  static HashMap<String, String> fileMap = new HashMap<>();
  static HashMap<String, List<Spectrum<float[]>>> spectrumListMap = new HashMap<>();
  static HashMap<String, List<byte[]>> spectrumBytesMap = new HashMap<>();
  static HashMap<String, List<float[]>> spectrumIntsMap = new HashMap<>();
  static HashMap<String, List<int[]>> spectrumIntsIntMap = new HashMap<>();
  static int MB = 1024 * 1024;
  static int KB = 1024;

  @BeforeClass
  public static void init() throws Exception {

//    initFile("File-DIA-Raw-zero",
//        "C:\\C20181218yix_HCC_DIA_T_46B_with_zero.json",
//        AirdType.DIA_SWATH,
//        16);
//    initFile("File-DIA-WIFF-zero",
//        "C:\\napedro_L120224_001_SW_with_zero.json",
//        AirdType.DIA_SWATH,
//        16);
//    initFile("File-DIA-WIFF-no-zero",
//        "D:\\proteomics\\Project\\HYE_124_64var-6600\\HYE124_TTOF6600_64var_lgillet_I150211_013.json",
//        AirdType.DIA_SWATH,
//        20);
//    initFile("File-DDA-Raw",
//        "D:\\Aird_Test\\SA1_6_with_zero.json",
//        AirdType.DDA,
//        -1);
//    initFile("File-DDA-Wiff",
//        "D:\\Aird_Test\\SampleA_1_with_zero.json",
//        AirdType.DDA,
//        -1);

    initFile("File-DIA-WIFF-zero",
        "C:\\HYE110_TTOF6600_64fix_lgillet_I160310_001.json",
        AirdType.DIA,
        0);
  }

  private static void initFile(String name, String indexPath, AirdType type, int indexNo)
      throws Exception {
    long start = System.currentTimeMillis();
    fileMap.put(name, indexPath);
    List<Spectrum<float[]>> spectrumList = Collections.synchronizedList(new ArrayList<>());
    double precision = 10;

    switch (type) {
      case DIA -> {
        DIAParser parser = new DIAParser(indexPath);
        precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_INTENSITY)
            .getPrecision();

        AirdInfo airdInfo = parser.getAirdInfo();
        List<BlockIndex> indexList = airdInfo.getIndexList();

        if (indexNo != -1) {
          BlockIndex index = indexList.get(indexNo);
          TreeMap<Float, Spectrum<float[]>> spectrumMap = parser.getSpectraAsFloat(index);
          spectrumList.addAll(spectrumMap.values().stream().toList());
        } else {
          indexList.forEach(index -> {
            TreeMap<Float, Spectrum<float[]>> spectrumMap = parser.getSpectraAsFloat(index);
            spectrumList.addAll(spectrumMap.values().stream().toList());
          });
        }
      }
      case DDA -> {
        DDAParser parser = new DDAParser(indexPath);
        precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_INTENSITY)
            .getPrecision();
        List<DDAMs> ms1List = parser.readAllToMemory();
        ms1List.forEach(ms1 -> {
          spectrumList.add(ArrayUtil.trans(ms1.getSpectrum()));
          if (ms1.getMs2List() != null && ms1.getMs2List().size() != 0) {
            for (DDAMs ms2 : ms1.getMs2List()) {
              spectrumList.add(ArrayUtil.trans(ms2.getSpectrum()));
            }
          }
        });
      }
    }

    spectrumListMap.put(name, spectrumList);
    List<byte[]> bytesList = new ArrayList<>();
    List<float[]> intsList = new ArrayList<>();
    for (int i = 0; i < spectrumList.size(); i++) {
      bytesList.add(ByteTrans.floatToByte(spectrumList.get(i).getInts()));
      float[] ints = new float[spectrumList.get(i).getInts().length];
      intsList.add(ints);
    }
    spectrumIntsMap.put(name, intsList);
    spectrumBytesMap.put(name, bytesList);
    System.out.println("初始化" + name + "完毕,耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
  }

  @Test
  public void test_DIA() {
    spectrumListMap.forEach((key, value) -> {
      System.out.println(
          "开始测试文件:" + key + "包含光谱共" + spectrumIntsMap.get(key).size()
              + "张,-------------------------");
      List<byte[]> bytesList = spectrumBytesMap.get(key);
      List<Spectrum<float[]>> spectrumList = spectrumListMap.get(key);
      List<float[]> intsList = spectrumIntsMap.get(key);

      //测试原始大小
      AtomicLong originSize = new AtomicLong(0);
      bytesList.parallelStream().forEach(bytes -> {
        originSize.getAndAdd(bytes.length);
      });
      int realSize = (int) (originSize.get() / MB);
      System.out.println("原始大小:" + realSize + "M");

      test_zlib(bytesList);
      test_brotli(bytesList);
      test_gzip(bytesList);
      test_snappy(bytesList);
      test_zstd(bytesList);
//      test_lz4(bytesList);
//      test_fastpfor(intsList);
//      test_fastpfor2(intsList);
//      System.out.println("");
//      test_xdpd_zlib(intsList);
//      test_xdpd_brotli(intsList);
//      test_xdpd_gzip(intsList);
//      test_xdpd_snappy(intsList);
//      test_xdpd_zstd(intsList);
//      test_xdpd_lz4(intsList);
//      System.out.println("");
//      test_xdpd2_zlib(intsIntList);
//      test_xdpd2_brotli(intsIntList);
//      test_xdpd2_gzip(intsIntList);
//      test_xdpd2_snappy(intsIntList);
//      test_xdpd2_zstd(intsIntList);
//      test_xdpd2_lz4(intsList);
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
        "Brotli:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
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
        "Zlib:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
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
        "Gzip:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for Snappy
  private void test_snappy(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(Snappier.encode(bytes).length);
    });
    System.out.println(
        "Snappy:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for zstd
  private void test_zstd(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(ZSTD.encode(bytes).length);
    });
    System.out.println(
        "ZSTD:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD2-Zlib
  private void test_xdpd2_zlib(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      byte[] bytes = XVByte.encode(ints);
      compressedSize.getAndAdd(bytes.length);
      int[] unzipInts = XVByte.decode(bytes);
      boolean isSame = Arrays.equals(ints, unzipInts);
      assert isSame;
    });
    System.out.println(
        "XDPD2-Zlib:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD2-Gzip
  private void test_xdpd2_gzip(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      byte[] bytes = XVByte.encode(ints, CompressorType.Gzip);
      compressedSize.getAndAdd(bytes.length);
      int[] sortedInts = XVByte.decode(bytes, CompressorType.Gzip);
      boolean isSame = Arrays.equals(ints, sortedInts);
      assert isSame;
    });
    System.out.println(
        "XDPD2-Gzip:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD2-Brotli
  private void test_xdpd2_brotli(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      byte[] bytes = XVByte.encode(ints, CompressorType.Brotli);
      compressedSize.getAndAdd(bytes.length);
      int[] sortedInts = XVByte.decode(bytes, CompressorType.Brotli);
      boolean isSame = Arrays.equals(ints, sortedInts);
      assert isSame;
    });
    System.out.println(
        "XDPD2-Brotli:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD2-Snappy
  private void test_xdpd2_snappy(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      byte[] bytes = XVByte.encode(ints, CompressorType.Snappy);
      compressedSize.getAndAdd(bytes.length);
      int[] sortedInts = XVByte.decode(bytes, CompressorType.Snappy);
      boolean isSame = Arrays.equals(ints, sortedInts);
      assert isSame;
    });
    System.out.println(
        "XDPD2-Snappy:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD2-ZSTD
  private void test_xdpd2_zstd(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      byte[] bytes = XVByte.encode(ints, CompressorType.ZSTD);
      compressedSize.getAndAdd(bytes.length);
      int[] sortedInts = XVByte.decode(bytes, CompressorType.ZSTD);
      boolean isSame = Arrays.equals(ints, sortedInts);
      assert isSame;
    });
    System.out.println(
        "XDPD2-ZSTD:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD2-LZ4
  private void test_xdpd2_lz4(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      byte[] bytes = XVByte.encode(ints, CompressorType.LZ4);
      compressedSize.getAndAdd(bytes.length);
      int[] sortedInts = XVByte.decode(bytes, CompressorType.LZ4);
      boolean isSame = Arrays.equals(ints, sortedInts);
      assert isSame;
    });
    System.out.println(
        "ZDPD2-LZ4:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for LZMA2
  private void test_LZMA2(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(LZ4.encode(bytes).length);
    });
    System.out.println(
        "LZMA2:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }
}
