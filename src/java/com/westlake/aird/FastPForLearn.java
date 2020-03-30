package com.westlake.aird;

import com.westlake.aird.util.CompressUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FastPForLearn {

    public static void main(String[] args) {
        int[] test = new int[900];
        int[] test2 = new int[900];
        int[] finalTest = new int[1800];
        for (int i = 0; i < test.length; i++) {
            test[i] = i * 4000000 + (new Random().nextBoolean() ? 1 : 2);
            finalTest[i] = test[i];
        }
        for (int i = 0; i < test2.length; i++) {
            test2[i] = test[test.length - 1] + i * 4 + (new Random().nextBoolean() ? 1 : 2);
            finalTest[i+900] = test2[i];
        }

        int[] newTest = CompressUtil.fastPForEncoder(test);
//        int[] newTest2 = CompressUtil.fastPForEncoder(test2);
//        int[] newFinalTest = CompressUtil.fastPForEncoder(finalTest);

        System.out.println(newTest.length);
//        System.out.println(newTest2.length);
//        System.out.println(newFinalTest.length);
    }


}
