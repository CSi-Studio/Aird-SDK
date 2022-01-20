package net.csibio.aird.test.compressor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.SpectrumF;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.CompressorType;
import net.csibio.aird.compressor.bytes.Brotli;
import net.csibio.aird.compressor.bytes.Gzip;
import net.csibio.aird.compressor.bytes.LZ4;
import net.csibio.aird.compressor.bytes.Snappier;
import net.csibio.aird.compressor.bytes.ZSTD;
import net.csibio.aird.compressor.bytes.Zlib;
import net.csibio.aird.compressor.ints.FastPFor;
import net.csibio.aird.compressor.ints.XDPD;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.v2.DIAParser;
import net.csibio.aird.util.ArrayUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xerial.snappy.BitShuffle;

public class CompressorDIATest {

  static HashMap<String, String> fileMap = new HashMap<>();
  static HashMap<String, List<SpectrumF>> spectrumListMap = new HashMap<>();
  static HashMap<String, List<byte[]>> spectrumBytesMap = new HashMap<>();
  static HashMap<String, List<int[]>> spectrumIntsMap = new HashMap<>();
  static int MB = 1024 * 1024;
  static int KB = 1024;

  @BeforeClass
  public static void init() throws Exception {
    initFile("File-DIA-HYE-A",
        "D:\\proteomics\\Project\\HYE_124_64var-6600\\HYE124_TTOF6600_64var_lgillet_I150211_008.json",
        AirdType.DIA_SWATH,
        16);
    initFile("File-DDA-Raw",
        "D:\\Aird_Test\\SA1_6_with_zero.json",
        AirdType.DDA,
        -1);
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
    List<SpectrumF> spectrumList = Collections.synchronizedList(new ArrayList<SpectrumF>());
    double precision = 0;

    switch (type) {
      case DIA_SWATH -> {
        DIAParser parser = new DIAParser(indexPath);
        precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_MZ).getPrecision();

        AirdInfo airdInfo = parser.getAirdInfo();
        List<BlockIndex> indexList = airdInfo.getIndexList();

        if (indexNo != -1) {
          BlockIndex index = indexList.get(indexNo);
          TreeMap<Float, SpectrumF> spectrumMap = parser.getSpectrumsAsFloat(index);
          spectrumList.addAll(spectrumMap.values().stream().toList());
        } else {
          indexList.forEach(index -> {
            TreeMap<Float, SpectrumF> spectrumMap = parser.getSpectrumsAsFloat(index);
            spectrumList.addAll(spectrumMap.values().stream().toList());
          });
        }
      }
      case DDA -> {
        DDAParser parser = new DDAParser(indexPath);
        precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_MZ).getPrecision();
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
    spectrumListMap.forEach((key, value) -> {
      System.out.println(
          "开始测试文件:" + key + "包含光谱共" + spectrumIntsMap.get(key).size()
              + "张,-------------------------");
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
      test_zstd(bytesList);
      test_lz4(bytesList);

      System.out.println("");
      test_fastpfor(intsList);
      System.out.println("");
      test_xdpd_zlib(intsList);
      test_xdpd_brotli(intsList);
      test_xdpd_gzip(intsList);
      test_xdpd_snappy(intsList);
      test_xdpd_zstd(intsList);
      test_xdpd_lz4(intsList);

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

  //Test for LZ4
  private void test_lz4(List<byte[]> bytesList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    bytesList.parallelStream().forEach(bytes -> {
      compressedSize.getAndAdd(ZSTD.encode(bytes).length);
    });
    System.out.println(
        "LZ4:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for FastPFOR
  private void test_fastpfor(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(FastPFor.encode(ints).length * 4L);
    });
    System.out.println(
        "FastPFOR:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD-Zlib
  private void test_xdpd_zlib(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.Zlib).length);
    });
    System.out.println(
        "XDPD-Zlib:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD-Gzip
  private void test_xdpd_gzip(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.Gzip).length);
    });
    System.out.println(
        "XDPD-Gzip:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD-Brotli
  private void test_xdpd_brotli(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.Brotli).length);
    });
    System.out.println(
        "ZDPD-Brotli:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD-Snappy
  private void test_xdpd_snappy(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.Snappy).length);
    });
    System.out.println(
        "ZDPD-Snappy:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD-ZSTD
  private void test_xdpd_zstd(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.ZSTD).length);
    });
    System.out.println(
        "ZDPD-ZSTD:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  //Test for XDPD-LZ4
  private void test_xdpd_lz4(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.LZ4).length);
    });
    System.out.println(
        "ZDPD-LZ4:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
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
