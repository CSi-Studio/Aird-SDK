package net.csibio.aird.compressor.bytes;

import java.util.Arrays;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

public class LZ4 {

  public static byte[] encode(byte[] input) {
    LZ4Factory factory = LZ4Factory.safeInstance();

    try {
      final int decompressedLength = input.length;
      LZ4Compressor compressor = factory.highCompressor();
      int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
      byte[] compressed = new byte[maxCompressedLength];
      compressor.compress(input, 0, decompressedLength, compressed, 0, maxCompressedLength);
      return compressed;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }

  public static byte[] decode(byte[] input) {
    return null;
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }
}
