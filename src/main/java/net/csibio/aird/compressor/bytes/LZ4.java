package net.csibio.aird.compressor.bytes;

import java.util.Arrays;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class LZ4 {

  public static byte[] encode(byte[] input) {
    LZ4Factory factory = LZ4Factory.safeInstance();

    try {
      final int decompressedLength = input.length;
      LZ4Compressor compressor = factory.highCompressor(9);
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
    int decompressedLength = input.length;
    int compressedLength = input.length;
    byte[] compressed = new byte[compressedLength];
    LZ4Factory factory = LZ4Factory.fastestInstance();
    LZ4FastDecompressor decompressor = factory.fastDecompressor();
    byte[] uncompressedByteArray = new byte[decompressedLength];
    decompressor.decompress(compressed, 0, uncompressedByteArray, 0, decompressedLength);
    return uncompressedByteArray;
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }
}
