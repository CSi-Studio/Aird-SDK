package net.csibio.aird.test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteCompressor;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.CompressorType;
import net.csibio.aird.compressor.ints.XDPD;
import net.csibio.aird.parser.v2.DIAParser;
import net.csibio.aird.util.ArrayUtil;
import org.junit.Test;

public class SizeTest {

  String DIA_INDEX_FILE_PATH = "D:\\proteomics\\Project\\HYE_110_64var\\HYE110_TTOF6600_64var_lgillet_I160305_001.json";
  //  String DIA_INDEX_FILE_PATH = "D:\\proteomics\\Project\\HYE_110_64Var_Full\\HYE110_TTOF6600_64var_lgillet_I160305_001_with_zero.json";
  //  String DIA_INDEX_FILE_PATH = "D:\\proteomics\\C20181208yix_HCC_DIA_T_46A_with_zero.json";
  int MB = 1024 * 1024;
  int KB = 1024;

  @Test
  public void testForDIA() {
    DIAParser diaParser = new DIAParser(DIA_INDEX_FILE_PATH);
    AirdInfo airdInfo = diaParser.getAirdInfo();
    Compressor mzCompressor = airdInfo.fetchCompressor(Compressor.TARGET_MZ);
    Compressor intCompressor = airdInfo.fetchCompressor(Compressor.TARGET_INTENSITY);
    List<BlockIndex> indexList = diaParser.getAirdInfo().getIndexList();

    AtomicLong mzOri = new AtomicLong(0);
    AtomicLong intOri = new AtomicLong(0);
    AtomicLong spectrumSize = new AtomicLong(0);
    List<CompressorType> byteTypes = Arrays.asList(CompressorType.Zlib, CompressorType.Gzip,
        CompressorType.LZMA2);
    ConcurrentHashMap<String, AtomicLong> mzMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, AtomicLong> intMap = new ConcurrentHashMap<>();
    for (CompressorType value : byteTypes) {
      mzMap.put(value.name(), new AtomicLong(0));
      intMap.put(value.name(), new AtomicLong(0));
    }
    mzMap.put("ZDPD", new AtomicLong(0));
    mzMap.put("LDPD", new AtomicLong(0));
    
    for (int k = 17; k < 18; k++) {
      BlockIndex index = indexList.get(k);
      TreeMap<Float, Spectrum> spectrumMap = diaParser.getSpectrums(index);
      List<Spectrum> spectrumList = spectrumMap.values().stream().toList();
      spectrumSize.getAndAdd(spectrumList.size());
      System.out.println("当前block中spectrum数目:" + spectrumList.size());
      spectrumList.parallelStream().forEach(spectrum -> {
        byte[] mzBytes = ByteTrans.floatToByte(spectrum.mzs());
        byte[] intBytes = ByteTrans.floatToByte(spectrum.ints());

        mzOri.getAndAdd(mzBytes.length);
        intOri.getAndAdd(intBytes.length);
        for (CompressorType value : byteTypes) {
          ByteCompressor compressor = new ByteCompressor(value);

          byte[] mzCompBytes = compressor.encode(mzBytes);
          float[] mzs = ByteTrans.byteToFloat(compressor.decode(mzCompBytes));
          if (!ArrayUtil.isSame(spectrum.mzs(), mzs)) {
            System.out.println("mz结果不一致:" + value.name());
          }
          mzMap.get(value.name()).getAndAdd(mzCompBytes.length);

          byte[] intCompBytes = compressor.encode(intBytes);
          float[] ints = ByteTrans.byteToFloat(compressor.decode(intCompBytes));
          if (!ArrayUtil.isSame(spectrum.ints(), ints)) {
            System.out.println("intensity结果不一致" + value.name());
          }
          intMap.get(value.name()).getAndAdd(intCompBytes.length);
        }

        mzMap.get("ZDPD")
            .getAndAdd(XDPD.encode(spectrum.mzs(), mzCompressor, CompressorType.Zlib).length);
        mzMap.get("LDPD")
            .getAndAdd(XDPD.encode(spectrum.mzs(), mzCompressor, CompressorType.LZMA2).length);
      });
      System.out.println("当前处理:" + k + "/" + indexList.size());
    }

    StringBuilder algorithms = new StringBuilder("    Ori/");
    for (CompressorType value : byteTypes) {
      algorithms.append(value.name()).append("/");
    }
    algorithms.append("ZDPD/").append("LDPD/");
    System.out.println(algorithms);
    StringBuilder mzs = new StringBuilder("mz: " + mzOri.get() / MB + "/");
    StringBuilder ints = new StringBuilder("int:" + intOri.get() / MB + "/");
    for (CompressorType value : byteTypes) {
      mzs.append(mzMap.get(value.name()).get() / MB).append(" /");
      ints.append(intMap.get(value.name()).get() / MB).append(" /");
    }
    mzs.append(mzMap.get("ZDPD").get() / MB).append(" /");
    mzs.append(mzMap.get("LDPD").get() / MB).append(" /");
    System.out.println(mzs);
    System.out.println(ints);
  }
}
