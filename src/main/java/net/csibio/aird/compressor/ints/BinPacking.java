package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.BinaryPacking;
import me.lemire.integercompression.IntCompressor;
import net.csibio.aird.enums.IntCompType;

public class BinPacking implements IntComp {

  @Override
  public String getName() {
    return IntCompType.BP.getName();
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
    int[] compressedInts = new IntCompressor(new BinaryPacking()).compress(uncompressed);
    return compressedInts;
  }

  @Override
  public int[] decode(int[] compressed) {
    int[] uncompressed = new IntCompressor(new BinaryPacking()).uncompress(compressed);
    return uncompressed;
  }
}
