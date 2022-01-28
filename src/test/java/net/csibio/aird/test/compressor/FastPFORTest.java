package net.csibio.aird.test.compressor;

import net.csibio.aird.compressor.ints.FastPFor;
import org.junit.Test;

public class FastPFORTest {

  @Test
  public void test() {
    int[] test = new int[10000];
    for (int i = 0; i < 100; i++) {
      test[i] = i;
    }
    int[] result = FastPFor.encode(test);
    System.out.println(result.length + "");
  }
}
