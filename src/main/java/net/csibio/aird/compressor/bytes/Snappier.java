package net.csibio.aird.compressor.bytes;

import java.io.IOException;
import java.util.Arrays;
import org.xerial.snappy.Snappy;

public class Snappier {

  public static byte[] encode(byte[] input) {
    try {
      return Snappy.compress(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] decode(byte[] input) {
    try {
      return Snappy.uncompress(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }

}