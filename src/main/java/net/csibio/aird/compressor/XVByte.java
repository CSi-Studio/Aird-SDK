package net.csibio.aird.compressor;

import me.lemire.integercompression.IntCompressor;
import me.lemire.integercompression.VariableByte;
import net.csibio.aird.enums.ByteCompType;

public class XVByte {

  /**
   * VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * integers.
   *
   * @param ints the integers array
   * @return the compressed data
   */
  public static byte[] encode(int[] ints) {
    return encode(ints, ByteCompType.Zlib);
  }


  /**
   * XDPD Encoder
   *
   * @param ints the integers array
   * @return the compressed data
   */
  public static byte[] encode(int[] ints, ByteCompType byteCompType) {
    int[] compressedInts = new IntCompressor(new VariableByte()).compress(ints);
    byte[] bytes = ByteTrans.intToByte(compressedInts);
    return new ByteCompressor(byteCompType).encode(bytes);
  }

  public static byte[] encode(double[] floats, double precision,
      ByteCompType compType) {
    int[] ints = new int[floats.length];
    for (int i = 0; i < floats.length; i++) {
      ints[i] = (int) (precision * floats[i]);
    }
    return encode(ints, compType);
  }

  public static int[] decode(byte[] bytes) {
    return decode(bytes, ByteCompType.Zlib);
  }

  public static int[] decode(byte[] bytes, ByteCompType type) {
    byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
    int[] zipInts = ByteTrans.byteToInt(decodeBytes);
    int[] ints = new IntCompressor(new VariableByte()).uncompress(zipInts);
    return ints;
  }
}
