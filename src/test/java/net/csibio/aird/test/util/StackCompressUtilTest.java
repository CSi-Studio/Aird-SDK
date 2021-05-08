package net.csibio.aird.test.util;

import net.csibio.aird.bean.Layers;
import net.csibio.aird.util.StackCompressUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackCompressUtilTest {

    @Test
    public void stackZDPD_Test1() {
        List<int[]> arrGroup = new ArrayList<>();
        for (int j = 0; j < 256; j++) {
            int[] mz = new int[1000 + (int) Math.random() * 100];
            mz[0] = 10000;
            for (int i = 1; i < mz.length; i++) {
                mz[i] = mz[i - 1] + (int) Math.random() * 1000;
            }
            arrGroup.add(mz);
        }
        Layers layers = StackCompressUtil.stackEncode(arrGroup, false);
        List<int[]> decompressArrGroup = StackCompressUtil.stackDecode(layers);
        boolean pass = true;
        for (int i = 0; i < arrGroup.size(); i++) {
            pass = pass && Arrays.equals(arrGroup.get(i), decompressArrGroup.get(i));
            if (!pass) {
                break;
            }
        }
        assert pass;
    }

    @Test
    public void stackZDPD_Test2() {
        List<int[]> arrGroup = new ArrayList<>();
        int arrNum = 1 + (int) Math.random() * 10000;
        for (int j = 0; j < arrNum; j++) {
            int[] mz = new int[1000 + (int) Math.random() * 100];
            mz[0] = 10000;
            for (int i = 1; i < mz.length; i++) {
                mz[i] = mz[i - 1] + (int) Math.random() * 1000;
            }
            arrGroup.add(mz);
        }
        Layers layers = StackCompressUtil.stackEncode(arrGroup, false);
        List<int[]> decompressArrGroup = StackCompressUtil.stackDecode(layers);
        boolean pass = true;
        for (int i = 0; i < arrGroup.size(); i++) {
            pass = pass && Arrays.equals(arrGroup.get(i), decompressArrGroup.get(i));
            if (!pass) {
                break;
            }
        }
        assert pass;
    }
}
