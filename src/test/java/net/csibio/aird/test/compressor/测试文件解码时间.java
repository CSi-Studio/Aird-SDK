package net.csibio.aird.test.compressor;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.AirdScanUtil;
import org.junit.Test;

public class 测试文件解码时间 {

  static HashMap<String, String> fileMap = new HashMap<>();
  static HashMap<String, Integer> readTime = new HashMap<>();

  static List<String> compList = new ArrayList<>();
  static List<AirdType> methodList = new ArrayList<>();

  static String basePath = "D:\\proteomics\\IVB\\";

  @Test
  public void test() throws Exception {
    Brotli4jLoader.ensureAvailability();
    methodList = Lists.newArrayList(AirdType.DDA, AirdType.DIA);
    compList = Lists.newArrayList("Brotli", "Snappy", "Zstd", "Zlib");
//    compList = Lists.newArrayList("Zstd");
    for (AirdType method : methodList) {
      for (String comp : compList) {
        String folder = method.getName() + "-" + comp;
        List<File> files = AirdScanUtil.scanIndexFiles(basePath + folder);
        if (files != null && files.size() > 0) {
          for (File file : files) {
            initFile(file.getAbsolutePath(), file.getAbsolutePath(), method);
          }
        }
      }
    }
  }

  private static void initFile(String name, String indexPath, AirdType type)
      throws Exception {
    long start = System.currentTimeMillis();
    fileMap.put(name, indexPath);

    switch (type) {
      case DIA -> {
        DIAParser parser = new DIAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        List<BlockIndex> indexList = airdInfo.getIndexList();
        indexList.forEach(parser::getSpectra);
        parser.close();
        long total = System.currentTimeMillis() - start;
        System.out.println(
            name + "-" + parser.getA() + ":" + parser.getB() + ":" + (total - parser.getA()
                - parser.getB()) + "=" + total + "毫秒");
      }
      case DDA -> {
        DDAParser parser = new DDAParser(indexPath);
        parser.readAllToMemory();
        parser.close();
        long total = System.currentTimeMillis() - start;
        System.out.println(
            name + "-" + parser.getA() + ":" + parser.getB() + ":" + (total - parser.getA()
                - parser.getB()) + "=" + total + "毫秒");
      }
    }
  }
}
