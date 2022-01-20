package net.csibio.aird.test.compressor;

import java.util.HashMap;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompressorDDATest {

  static HashMap<String, String> fileMap = new HashMap<>();

  @BeforeClass
  public static void init() {
    fileMap.put("DIA_6600",
        "D:\\proteomics\\Project\\HYE_124_64var-6600\\HYE124_TTOF6600_64var_lgillet_I150211_008.json");
  }

  @Test
  public void test_DIA_brotli() {

  }

}
