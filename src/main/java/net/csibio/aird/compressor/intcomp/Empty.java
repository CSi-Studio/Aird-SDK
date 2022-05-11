package net.csibio.aird.compressor.intcomp;

import net.csibio.aird.enums.IntCompType;

public class Empty implements IntComp {

    @Override
    public String getName() {
        return IntCompType.Empty.getName();
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
