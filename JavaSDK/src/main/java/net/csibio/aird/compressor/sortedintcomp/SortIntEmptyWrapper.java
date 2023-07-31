package net.csibio.aird.compressor.sortedintcomp;

public class SortIntEmptyWrapper implements SortedIntComp{
    @Override
    public String getName() {
        return "Empty";
    }

    @Override
    public int[] encode(int[] uncompressed) {
        return uncompressed;
    }

    @Override
    public int[] decode(int[] compressed) {
        return compressed;
    }
}
