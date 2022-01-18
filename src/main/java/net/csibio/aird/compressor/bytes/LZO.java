package net.csibio.aird.compressor.bytes;

import java.util.Arrays;

public class LZO {

  public static byte[] encode(byte[] input) {
    return null;
  }

  public static byte[] decode(byte[] input) {
    return null;
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }
}
