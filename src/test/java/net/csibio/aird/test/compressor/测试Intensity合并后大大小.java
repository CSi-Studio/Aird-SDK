package net.csibio.aird.test.compressor;

import com.github.luben.zstd.ZstdDictTrainer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytes.Zlib;
import net.csibio.aird.compressor.bytes.Zstd;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

public class 测试Intensity合并后大大小 {

  static int MB = 1024 * 1024;
  static int KB = 1024;

  String indexPath = "D:\\Aird_Test\\SampleA_1_with_zero.json";

  @Test
  public void tryIt() throws Exception {
    long start = System.currentTimeMillis();
    DDAParser parser = new DDAParser(indexPath);
    List<DDAMs> ms1List = parser.readAllToMemory();
    AtomicLong zlibIntsSize = new AtomicLong(0);
    AtomicLong zstdIntsSize = new AtomicLong(0);
    List<byte[]> bytesList = new ArrayList<>();
    List<byte[]> samples = new ArrayList<>();
    int totalSize = 0;
    int sampleCount = 0;
    for (int i = 0; i < ms1List.size(); i++) {
      float[] ints = ms1List.get(i).getSpectrum().getInts();
      byte[] bytes = ByteTrans.floatToByte(ints);
      bytesList.add(bytes);
      if (sampleCount < 10) {
        samples.add(bytes);
        totalSize += bytes.length;
        sampleCount++;
      }
    }
    ZstdDictTrainer dictTrainer = new ZstdDictTrainer(totalSize, 32 * 1024);
    for (int i = 0; i < samples.size(); i++) {
      dictTrainer.addSample(samples.get(i));
    }
    byte[] dict = dictTrainer.trainSamples();
    System.out.println("dict size:" + dict.length / KB);
    long time = System.currentTimeMillis();
    List<byte[]> zlibList = new ArrayList<>();
    bytesList.parallelStream().forEach(bytes -> {
      byte[] zip = new Zlib().encode(bytes);
      zlibIntsSize.getAndAdd(zip.length);
      zlibList.add(zip);
    });
    System.out.println(
        "Zlib:" + (System.currentTimeMillis() - time) + "/" + zlibIntsSize.get() / MB);

    time = System.currentTimeMillis();
    zlibList.parallelStream().forEach(bytes -> {
      new Zlib().decode(bytes);
    });
    System.out.println(
        "Zlib Decode Time:" + (System.currentTimeMillis() - time));

    time = System.currentTimeMillis();
    List<byte[]> zstdList = new ArrayList<>();
    bytesList.parallelStream().forEach(bytes -> {
      byte[] zip = new Zstd().encode(bytes);
      zstdIntsSize.getAndAdd(zip.length);
      zstdList.add(zip);
//      byte[] unzip = ZSTD.decode(zip, dict);
//      for (int i = 0; i < bytes.length; i++) {
//        if (!Objects.equals(bytes[i], unzip[i])) {
//          assert false;
//        }
//      }
    });
    System.out.println(
        "ZSTD:" + (System.currentTimeMillis() - time) + "/" + zlibIntsSize.get() / MB);

    time = System.currentTimeMillis();
    zstdList.parallelStream().forEach(bytes -> {
      new Zstd().decode(bytes);
    });
    System.out.println(
        "Zstd Decode Time:" + (System.currentTimeMillis() - time));
    System.out.println("耗时:" + (System.currentTimeMillis() - start));
  }
}
