package net.csibio.aird.compressor.ints;

import net.csibio.aird.compressor.ByteCompressor;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.CompressorType;

public class XDPD {

  /**
   * XDPD Encoder, default byte compressor is Zlib
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public static byte[] encode(int[] sortedInts) {
    return encode(sortedInts, CompressorType.Zlib);
  }

  /**
   * ZDPD Encoder
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public static byte[] encode(int[] sortedInts, CompressorType byteCompType) {
    int[] compressedInts = IntegratedBinaryPack.encode(sortedInts);
    byte[] bytes = ByteTrans.intToByte(compressedInts);
    return new ByteCompressor(byteCompType).encode(bytes);
  }

  public static byte[] encode(double[] sortedFloats, double precision,
      CompressorType byteCompType) {
    int[] sortedInts = new int[sortedFloats.length];
    for (int i = 0; i < sortedFloats.length; i++) {
      sortedInts[i] = (int) (precision * sortedFloats[i]);
    }
    return encode(sortedInts, byteCompType);
  }

  public static int[] decode(byte[] bytes) {
    return decode(bytes, CompressorType.Zlib);
  }

//  public static int[] decode(byte[] bytes, int precision) {
//    return decode(bytes, precision, CompressorType.Zlib);
//  }

  public static int[] decode(byte[] bytes, CompressorType type) {
    byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
    int[] zipInts = ByteTrans.byteToInt(decodeBytes);
    int[] sortedInts = IntegratedBinaryPack.decode(zipInts);
//    double[] sortedDouble = new double[sortedInts.length];
//    for (int i = 0; i < sortedInts.length; i++) {
//      sortedDouble[i] = sortedInts[i] / precision;
//    }
    return sortedInts;
  }
}
