package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.IntCompressor;
import me.lemire.integercompression.VariableByte;
import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.differential.IntegratedVariableByte;

public class VarByte implements IntComp{

  /**
   * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * sorted integers.
   *
   * @param uncompressed the sorted integers
   * @return the compressed data
   */
  public int[] encode(int[] uncompressed) {
    int[] compressedInts = new IntCompressor(new VariableByte()).compress(uncompressed);
    return compressedInts;
  }

  public int[] decode(int[] compressed) {
    int[] uncompressed = new IntCompressor(new VariableByte()).compress(compressed);
    return uncompressed;
  }
}
