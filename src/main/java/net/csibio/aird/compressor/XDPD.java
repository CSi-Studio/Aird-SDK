package net.csibio.aird.compressor;

import net.csibio.aird.compressor.ints.IntegratedBinaryPack;
import net.csibio.aird.enums.ByteCompType;

public class XDPD {

  /**
   * XDPD Encoder, default byte compressor is Zlib
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public static byte[] encode(int[] sortedInts) {
    return encode(sortedInts, ByteCompType.Zlib);
  }

  /**
   * ZDPD Encoder
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public static byte[] encode(int[] sortedInts, ByteCompType byteCompType) {
    int[] compressedInts = new IntegratedBinaryPack().encode(sortedInts);
    byte[] bytes = ByteTrans.intToByte(compressedInts);
    return new ByteCompressor(byteCompType).encode(bytes);
  }

  public static byte[] encode(double[] sortedFloats, double precision,
      ByteCompType byteCompType) {
    int[] sortedInts = new int[sortedFloats.length];
    for (int i = 0; i < sortedFloats.length; i++) {
      sortedInts[i] = (int) (precision * sortedFloats[i]);
    }
    return encode(sortedInts, byteCompType);
  }

  public static int[] decode(byte[] bytes) {
    return decode(bytes, ByteCompType.Zlib);
  }

//  public static int[] decode(byte[] bytes, int precision) {
//    return decode(bytes, precision, CompressorType.Zlib);
//  }

  public static int[] decode(byte[] bytes, ByteCompType type) {
    byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
    int[] zipInts = ByteTrans.byteToInt(decodeBytes);
    int[] sortedInts = new IntegratedBinaryPack().decode(zipInts);
//    double[] sortedDouble = new double[sortedInts.length];
//    for (int i = 0; i < sortedInts.length; i++) {
//      sortedDouble[i] = sortedInts[i] / precision;
//    }
    return sortedInts;
  }
}
