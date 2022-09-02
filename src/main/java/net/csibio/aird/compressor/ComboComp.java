package net.csibio.aird.compressor;

import net.csibio.aird.compressor.bytecomp.ByteComp;
import net.csibio.aird.compressor.intcomp.IntComp;
import net.csibio.aird.compressor.sortedintcomp.SortedIntComp;

public class ComboComp {

    public static byte[] encode(ByteComp byteComp, int[] target) {
        return byteComp.encode(ByteTrans.intToByte(target));
    }

    public static int[] decode(ByteComp byteComp, byte[] target) {
        return ByteTrans.byteToInt(byteComp.decode(target));
    }

    public static byte[] encode(IntComp intComp, ByteComp byteComp, int[] target) {
        return byteComp.encode(ByteTrans.intToByte(intComp.encode(target)));
    }

    public static int[] decode(IntComp intComp, ByteComp byteComp, byte[] target) {
        return intComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
    }

    public static byte[] encode(SortedIntComp sortedIntComp, ByteComp byteComp, int[] target) {
        return byteComp.encode(ByteTrans.intToByte(sortedIntComp.encode(target)));
    }

    public static int[] decode(SortedIntComp sortedIntComp, ByteComp byteComp, byte[] target) {
        return sortedIntComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
    }
}
