package net.csibio.aird.compressor.ints;

public interface IntComp {

  int[] encode(int[] uncompressed);

  int[] decode(int[] compressed);
}
