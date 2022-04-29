package net.csibio.aird.compressor.ints;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import net.csibio.aird.enums.SortedIntCompType;

/**
 * 入参必须是有序数组,经过SIMD优化的算法
 */
public class IntegratedBinaryPack implements IntComp {

  @Override
  public String getName() {
    return SortedIntCompType.IBP.getName();
  }

  /**
   * compress the data with fastpfor algorithm
   *
   * @param uncompressed sorted integers to be compressed
   * @return compressed data
   */
  @Override
  public int[] encode(int[] uncompressed) {
    int[] compressed = new IntegratedIntCompressor().compress(uncompressed);
    return compressed;
  }

  /**
   * decompress the data with fastpfor algorithm
   *
   * @param compressed 压缩对象
   * @return decompressed data
   */
  @Override
  public int[] decode(int[] compressed) {
    int[] uncompressed = new IntegratedIntCompressor().uncompress(compressed);
    return uncompressed;
  }
}
