package net.csibio.aird.compressor.intcomp;

public interface IntComp {

    String getName();

    int[] encode(int[] uncompressed);

    int[] decode(int[] compressed);
}
