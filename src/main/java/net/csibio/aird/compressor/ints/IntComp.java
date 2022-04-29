package net.csibio.aird.compressor.ints;

public interface IntComp  {

  String getName();

  int[] encode(int[] uncompressed);

  int[] decode(int[] compressed);
}
