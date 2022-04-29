package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import net.csibio.aird.enums.SortedIntCompType;

public class IntegratedVarByte implements IntComp {

  @Override
  public String getName() {
    return SortedIntCompType.IVB.getName();
  }

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
