package net.csibio.aird.test.util;

import net.csibio.aird.util.AirdScanUtil;
import org.junit.Test;

public class AirdScanUtilTest {

  @Test
  public void scanIndexFiles_Test() {
    assert AirdScanUtil.isAirdFile("D:\\omicsdata\\proteomics\\C20181208yix_HCC_DIA_T_46A.aird");
    assert AirdScanUtil.isAirdFile("sssd.AirD");
  }
}
