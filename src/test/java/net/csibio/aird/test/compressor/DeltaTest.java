package net.csibio.aird.test.compressor;

import java.util.Arrays;
import net.csibio.aird.compressor.Delta;
import org.junit.Test;

public class DeltaTest {

  @Test
  public void test() {
    int[] test = {1, 3, 44, 6, 7, 8, 93};
    int[] temp = Delta.delta(test);
    int[] recover = Delta.recover(temp);
    assert Arrays.equals(test, recover);
  }
}
