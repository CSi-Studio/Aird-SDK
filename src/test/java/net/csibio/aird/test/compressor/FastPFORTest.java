package net.csibio.aird.test.compressor;

import net.csibio.aird.compressor.ints.BinaryPack;
import net.csibio.aird.compressor.ints.XVByte;
import org.junit.Test;

public class FastPFORTest {

  @Test
  public void test() {
    int size = 10;
    int[] test = new int[size];
    for (int i = 0; i < size; i++) {
      test[i] = (int) (Math.random() * 1000);
    }
    int[] encode = BinaryPack.encode(test);
    byte[] encode2 = XVByte.encode(test);

    int[] decode = BinaryPack.decode(encode);
    int[] decode2 = XVByte.decode(encode2);
    System.out.println("Hello");
  }
}
