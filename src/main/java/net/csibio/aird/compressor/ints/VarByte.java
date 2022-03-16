package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.IntCompressor;
import me.lemire.integercompression.VariableByte;

public class VarByte implements IntComp {

  /**
   * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * sorted integers.
   *
   * @param uncompressed the sorted integers
   * @return the compressed data
   */
  @Override
  public int[] encode(int[] uncompressed) {
    int[] compressedInts = new IntCompressor(new VariableByte()).compress(uncompressed);
    return compressedInts;
  }

  @Override
  public int[] decode(int[] compressed) {
    int[] uncompressed = new IntCompressor(new VariableByte()).uncompress(compressed);
    return uncompressed;
  }
}
