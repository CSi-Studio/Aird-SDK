package net.csibio.aird.compressor.ints;

import net.csibio.aird.bean.Compressor;
import net.csibio.aird.compressor.ByteCompressor;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.CompressorType;

public class XDPD {

  /**
   * ZDPD Encoder
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
    int[] compressedInts = FastPFor.encode(sortedInts);
    byte[] bytes = ByteTrans.intToByte(compressedInts);
    return new ByteCompressor(byteCompType).encode(bytes);
  }

  public static byte[] encode(float[] sortedFloats, Compressor compressor,
      CompressorType byteCompType) {
    int precision = compressor.getPrecision();
    int[] sortedInts = new int[sortedFloats.length];
    for (int i = 0; i < sortedFloats.length; i++) {
      sortedInts[i] = (int) (precision * sortedFloats[i]);
    }
    return encode(sortedInts, byteCompType);
  }
}
