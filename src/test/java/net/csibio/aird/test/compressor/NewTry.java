package net.csibio.aird.test.compressor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.compressor.CompressorType;
import net.csibio.aird.compressor.ints.IntegratedXVByte;
import net.csibio.aird.compressor.ints.XDPD;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

public class NewTry {

  static int MB = 1024 * 1024;
  static int KB = 1024;

  String indexPath = "D:\\Aird_Test\\SampleA_1_with_zero.json";

  @Test
  public void tryIt() throws Exception {
    long start = System.currentTimeMillis();
    DDAParser parser = new DDAParser(indexPath);
    double precision = parser.getAirdInfo().fetchCompressor(Compressor.TARGET_MZ).getPrecision();
    List<int[]> mzListTotal = new ArrayList<>();
    List<DDAMs> ms1List = parser.readAllToMemory();
    AtomicLong totalMzs = new AtomicLong(0);
    ms1List.forEach(ms1 -> {
      int[] ints = new int[ms1.getSpectrum().getMzs().length];
      for (int i = 0; i < ms1.getSpectrum().getMzs().length; i++) {
        ints[i] = (int) (ms1.getSpectrum().getMzs()[i] * precision);
      }
      mzListTotal.add(ints);
      totalMzs.getAndAdd(ms1.getSpectrum().getMzs().length);
      if (ms1.getMs2List() != null && ms1.getMs2List().size() != 0) {
        for (DDAMs ms2 : ms1.getMs2List()) {
          int[] ints2 = new int[ms2.getSpectrum().getMzs().length];
          for (int i = 0; i < ms2.getSpectrum().getMzs().length; i++) {
            ints2[i] = (int) (ms2.getSpectrum().getMzs()[i] * precision);
          }
          mzListTotal.add(ints2);
          totalMzs.getAndAdd(ms2.getSpectrum().getMzs().length);
        }
      }
    });
    System.out.println("初始化数据耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
    System.out.println("数值精度:" + precision);
    System.out.println("光谱:" + mzListTotal.size() + "张,mz:" + totalMzs.get() * 4L / MB + "M");
    test_xdpd_zlib(mzListTotal);
    test_xdpd2_zlib(mzListTotal);
//    test_new_alog(mzListTotal);
  }


  private void test_new_alog(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      int[] left = new int[ints.length];
      int[] right = new int[ints.length];
      for (int i = 0; i < ints.length; i++) {
        left[i] = ints[i] / 1000;
        right[i] = ints[i] - left[i] * 1000;
      }

      byte[] leftBytes = IntegratedXVByte.encode(left, CompressorType.Zlib);
      compressedSize.getAndAdd(leftBytes.length);
      byte[] rightBytes = IntegratedXVByte.encode(right, CompressorType.Zlib);
      compressedSize.getAndAdd(rightBytes.length);

//      double[] leftDouble = XPFor.decode(leftBytes, 1, CompressorType.Zlib, new FastPFOR(),
//          true);
//      double[] rightDouble = XPFor.decode(rightBytes, 1, CompressorType.Zlib, new FastPFOR(),
//          false);
//      int[] decomp = new int[ints.length];
//      for (int i = 0; i < ints.length; i++) {
//        decomp[i] = (int) (leftDouble[i] * 1000 + rightDouble[i]);
//      }
    });
    System.out.println(
        "New-Algo:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }

  private void test_xdpd_zlib(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(XDPD.encode(ints, CompressorType.Zlib).length);
    });
    System.out.println(
        "XDPD2-Zlib:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }


  private void test_xdpd2_zlib(List<int[]> intsList) {
    long start = System.currentTimeMillis();
    AtomicLong compressedSize = new AtomicLong(0);
    intsList.parallelStream().forEach(ints -> {
      compressedSize.getAndAdd(
          IntegratedXVByte.encode(ints, CompressorType.Zlib).length);
    });
    System.out.println(
        "XDPD2-Zlib:" + (System.currentTimeMillis() - start) + "|" + compressedSize.get() / MB
            + "M");
  }
}
