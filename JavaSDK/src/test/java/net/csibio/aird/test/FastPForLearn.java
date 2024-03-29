/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.test;

import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.VariableByte;
import me.lemire.integercompression.differential.IntegratedIntCompressor;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;

import java.util.Random;

/**
 * Test Code for fastpfor
 */
public class FastPForLearn {

    public static void main(String[] args) {
        int[] test = new int[900];
        int[] test2 = new int[900];
        int[] finalTest = new int[1800];
        for (int i = 0; i < test.length; i++) {
            test[i] = (i == 0 ? 0 : test[i - 1]) + (new Random().nextInt(10));
            finalTest[i] = test[i];
        }
        for (int i = 0; i < test2.length; i++) {
            test2[i] = test[test.length - 1] + i * 4 + (new Random().nextBoolean() ? 1 : 2);
            finalTest[i + 900] = test2[i];
        }

        int[] newTest = new IntegratedBinPackingWrapper().encode(test);
        int[] oldTest = new IntegratedBinPackingWrapper().decode(newTest);
//        new IntegratedIntCompressor(new FastPFOR());

//        int[] newTest2 = CompressUtil.fastPForEncoder(test2);
//        int[] newFinalTest = CompressUtil.fastPForEncoder(finalTest);

        System.out.println(newTest.length);
//        System.out.println(newTest2.length);
//        System.out.println(newFinalTest.length);
    }


}
