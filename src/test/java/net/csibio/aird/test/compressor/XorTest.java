package net.csibio.aird.test.compressor;

import java.util.Arrays;
import net.csibio.aird.compressor.Xor;
import org.junit.Test;

public class XorTest {

  @Test
  public void test() {
    int[] test = {1, 1, 2};
    int[] xor = Xor.xor(test);
    int[] recover = Xor.reverseXor(xor);
    assert Arrays.equals(test, recover);
  }

}
