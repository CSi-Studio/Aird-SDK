package net.csibio.aird.compressor.sortedintcomp;

public interface SortedIntComp {

    String getName();

    int[] encode(int[] uncompressed);

    int[] decode(int[] compressed);
}
