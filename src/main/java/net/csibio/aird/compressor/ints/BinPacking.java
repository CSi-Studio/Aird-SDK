package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.BinaryPacking;
import me.lemire.integercompression.IntCompressor;

public class BinPacking implements IntComp {

  /**
   * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * sorted integers.
   *
   * @param uncompressed the sorted integers
   * @return the compressed data
   */
  @Override
  public int[] encode(int[] uncompressed) {
    int[] compressedInts = new IntCompressor(new BinaryPacking()).compress(uncompressed);
    return compressedInts;
  }

  @Override
  public int[] decode(int[] compressed) {
    int[] uncompressed = new IntCompressor(new BinaryPacking()).uncompress(compressed);
    return uncompressed;
  }
}
