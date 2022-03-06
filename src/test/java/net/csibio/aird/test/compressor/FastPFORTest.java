package net.csibio.aird.test.compressor;

import net.csibio.aird.compressor.IntegratedXVByte;
import net.csibio.aird.compressor.ints.IntegratedBinaryPack;
import org.junit.Test;

public class FastPFORTest {

    @Test
    public void test() {
        int size = 10;
        int[] test = new int[size];
        for (int i = 0; i < size; i++) {
            test[i] = (int) (Math.random() * 1000);
        }
        int[] encode = new IntegratedBinaryPack().encode(test);
        byte[] encode2 = new IntegratedXVByte().encode(test);

        int[] decode = new IntegratedBinaryPack().decode(encode);
        int[] decode2 = new IntegratedXVByte().decode(encode2);
        System.out.println("Hello");
    }
}
