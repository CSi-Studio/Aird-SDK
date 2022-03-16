package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.differential.IntegratedVariableByte;

public class IntegratedVarByte implements IntComp {

  /**
   * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * sorted integers.
   *
   * @param uncompressed the sorted integers
   * @return the compressed data
   */
  @Override
  public int[] encode(int[] uncompressed) {
    int[] compressedInts = new IntegratedIntCompressor(new IntegratedVariableByte()).compress(
        uncompressed);
    return compressedInts;
  }

  @Override
  public int[] decode(int[] compressed) {
    int[] uncompressed = new IntegratedIntCompressor(new IntegratedVariableByte()).uncompress(
        compressed);
    return uncompressed;
  }
}
