package net.csibio.aird.compressor.intcomp;

import me.lemire.integercompression.IntCompressor;
import me.lemire.integercompression.VariableByte;
import net.csibio.aird.enums.IntCompType;

public class VarByteWrapper implements IntComp {

    @Override
    public String getName() {
        return IntCompType.VB.getName();
    }

    /**
     * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
     * sorted integers.
     *
     * @param uncompressed the sorted integers
     * @return the compressed data
     */
    @Override
    public int[] encode(int[] uncompressed) {
        int[] compressedInts = new IntCompressor(new VariableByte()).compress(uncompressed);
        return compressedInts;
    }

    @Override
    public int[] decode(int[] compressed) {
        int[] uncompressed = new IntCompressor(new VariableByte()).uncompress(compressed);
        return uncompressed;
    }
}
