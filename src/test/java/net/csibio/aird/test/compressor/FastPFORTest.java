package net.csibio.aird.test.compressor;

import net.csibio.aird.compressor.ints.FastPFor;
import org.junit.Test;

public class FastPFORTest {

  @Test
  public void test() {
    int[] test = new int[10000];
    for (int i = 0; i < 10000; i++) {
      if (i > 5000) {
        test[i] = 5000000 + i;
      } else {
        test[i] = i;
      }
    }
    int[] compressed = FastPFor.encode(test);
    System.out.println(compressed.length + "");
  }
}
