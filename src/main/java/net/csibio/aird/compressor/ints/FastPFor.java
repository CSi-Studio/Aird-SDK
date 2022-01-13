package net.csibio.aird.compressor.ints;

import java.util.Arrays;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.differential.IntegratedBinaryPacking;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import me.lemire.integercompression.differential.SkippableIntegratedComposition;
import net.csibio.aird.bean.Compressor;

public class FastPFor {

  public static int[] encode(float[] sortedFloats, Compressor compressor) {
    int[] sortedIns = new int[sortedFloats.length];
    for (int i = 0; i < sortedFloats.length; i++) {
      sortedIns[i] = (int) (compressor.getPrecision() * sortedFloats[i]);
    }
    return sortedIns;
  }

  /**
   * compress the data with fastpfor algorithm
   *
   * @param sortedInt data to be decoded
   * @return compressed data
   */
  public static int[] encode(int[] sortedInt) {
    SkippableIntegratedComposition codec = new SkippableIntegratedComposition(
        new IntegratedBinaryPacking(), new IntegratedVariableByte());
    int[] compressed = new int[sortedInt.length + 1024];
    IntWrapper inputoffset = new IntWrapper(0);
    IntWrapper outputoffset = new IntWrapper(1);
    codec.headlessCompress(sortedInt, inputoffset, sortedInt.length, compressed, outputoffset,
        new IntWrapper(0));
    compressed[0] = sortedInt.length;
    compressed = Arrays.copyOf(compressed, outputoffset.intValue());

    return compressed;
  }

  /**
   * decompress the data with fastpfor algorithm
   *
   * @param compressedInts 压缩对象
   * @return decompressed data
   */
  public static int[] decode(int[] compressedInts) {
    SkippableIntegratedComposition codec = new SkippableIntegratedComposition(
        new IntegratedBinaryPacking(), new IntegratedVariableByte());
    int size = compressedInts[0];
    // output vector should be large enough...
    int[] recovered = new int[size];
    IntWrapper inPoso = new IntWrapper(1);
    IntWrapper outPoso = new IntWrapper(0);
    IntWrapper recoffset = new IntWrapper(0);
    codec.headlessUncompress(compressedInts, inPoso, compressedInts.length, recovered, recoffset,
        size, outPoso);

    return recovered;
  }
}
