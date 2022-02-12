package net.csibio.aird.compressor.bytes;

import com.github.luben.zstd.Zstd;
import java.util.Arrays;

public class ZSTD {

  public static byte[] encode(byte[] input) {
    return Zstd.compress(input, 9);
  }

  public static byte[] decode(byte[] input) {
    int size = (int) Zstd.decompressedSize(input);
    byte[] array = new byte[size];
    Zstd.decompress(array, input);
    return array;
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }
}
