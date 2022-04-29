package net.csibio.aird.compressor;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import net.csibio.aird.enums.ByteCompType;

public class IntegratedXVByte {

  /**
   * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * sorted integers.
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public byte[] encode(int[] sortedInts) {
    return encode(sortedInts, ByteCompType.Zlib);
  }

  /**
   * ZDPD Encoder
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public byte[] encode(int[] sortedInts, ByteCompType byteCompType) {
    int[] compressedInts = new IntegratedIntCompressor(new IntegratedVariableByte()).compress(
        sortedInts);
    byte[] bytes = ByteTrans.intToByte(compressedInts);
    return new ByteCompressor(byteCompType).encode(bytes);
  }

  /**
   * @param sortedFloats
   * @param precision
   * @param compType
   * @return
   */
  public byte[] encode(double[] sortedFloats, double precision,
      ByteCompType compType) {
    int[] sortedInts = new int[sortedFloats.length];
    for (int i = 0; i < sortedFloats.length; i++) {
      sortedInts[i] = (int) (precision * sortedFloats[i]);
    }
    return encode(sortedInts, compType);
  }

  public int[] decode(byte[] bytes) {
    return decode(bytes, ByteCompType.Zlib);
  }

  public int[] decode(byte[] bytes, ByteCompType type) {
    byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
    int[] zipInts = ByteTrans.byteToInt(decodeBytes);
    int[] sortedInts = new IntegratedIntCompressor(new IntegratedVariableByte()).uncompress(
        zipInts);
    return sortedInts;
  }
}
