package net.csibio.aird.test.compressor;

import net.csibio.aird.compressor.ints.IntegratedBinaryPack;
import net.csibio.aird.compressor.ints.IntegratedXVByte;
import org.junit.Test;

public class FastPFORTest {

  @Test
  public void test() {
    int size = 10;
    int[] test = new int[size];
    for (int i = 0; i < size; i++) {
      test[i] = (int) (Math.random() * 1000);
    }
    int[] encode = IntegratedBinaryPack.encode(test);
    byte[] encode2 = IntegratedXVByte.encode(test);

    int[] decode = IntegratedBinaryPack.decode(encode);
    int[] decode2 = IntegratedXVByte.decode(encode2);
    System.out.println("Hello");
  }
}
