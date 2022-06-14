package net.csibio.aird.test.compressor;

import net.csibio.aird.compressor.Xor;
import org.junit.Test;

import java.util.Arrays;

public class XorTest {

    @Test
    public void test() {
        int[] test = {1, 1, 2};
        int[] xor = Xor.xor(test);
        int[] recover = Xor.recover(xor);
        assert Arrays.equals(test, recover);
    }
}