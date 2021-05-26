package net.csibio.aird.test;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Layers;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;
import net.csibio.aird.util.StackCompressUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestGen2ComRatio {
    public static void main(String[] args) throws IOException {
        int lengthNum = 10;
        int lengthStep = 5000;
        String filePath = "/Users/jinyinwang/Documents/Test/理论数据压缩对比.csv";
        String filePath2 = "/Users/jinyinwang/Documents/Test/理论数据.csv";

        long[] arrSize = new long[lengthNum];
        long[] tagSize = new long[lengthNum];
        long[] arrSize2 = new long[lengthNum];
        long[] tagSize2 = new long[lengthNum];
        long[] arrSize3 = new long[lengthNum];
        long[] tagSize3 = new long[lengthNum];
        long[] arrSize4 = new long[lengthNum];
        long[] tagSize4 = new long[lengthNum];

        int[] arrLength = new int[lengthNum];
        for (int k = 0; k < arrLength.length; k++) {
            arrLength[k] = lengthStep * (k + 1);

            List<int[]> arrGroup = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                int[] arr = new int[arrLength[k]];
//            int[] arr = new int[(int) (l * (1 + Math.random() * 0.1))];
                arr = stepData(arr, 8000000 / arrLength[k], false);
                arrGroup.add(arr);
            }

            List<int[]> arrGroup2 = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                int[] arr = new int[arrLength[k]];
//            int[] arr = new int[(int) (l * (1 + Math.random() * 0.1))];
                arr = stepData(arr, 8000000 / arrLength[k], true);
                arrGroup2.add(arr);
            }

            List<int[]> arrGroup3 = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                int[] arr = new int[arrLength[k]];
//            int[] arr = new int[(int) (l * (1 + Math.random() * 0.1))];
                arr = randomData(arr);
                arrGroup3.add(arr);
            }

            List<int[]> arrGroup4 = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                int[] arr = new int[arrLength[k]];
                arr = setData(arr, 500);
                arrGroup4.add(arr);
            }
            Layers layer = StackCompressUtil.stackEncode(arrGroup, true);
            Layers layer2 = StackCompressUtil.stackEncode(arrGroup2, true);
            Layers layer3 = StackCompressUtil.stackEncode(arrGroup3, true);
            Layers layer4 = StackCompressUtil.stackEncode(arrGroup4, true);

            arrSize[k] = layer.getMzArray().length;
            tagSize[k] = layer.getTagArray().length;
            arrSize2[k] = layer2.getMzArray().length;
            tagSize2[k] = layer2.getTagArray().length;
            arrSize3[k] = layer3.getMzArray().length;
            tagSize3[k] = layer3.getTagArray().length;
            arrSize4[k] = layer4.getMzArray().length;
            tagSize4[k] = layer4.getTagArray().length;

            if (k == 0) {
                File file2 = new File(filePath2);
                FileWriter out2 = new FileWriter(file2);
                out2.write("固定步长,波动步长,纯随机,聚合");
                out2.write("\r\n");
                for (int i = 0; i < arrGroup.get(0).length; i++) {
                    out2.write(arrGroup.get(0)[i] + ","+ arrGroup2.get(0)[i] + ","+ arrGroup3.get(0)[i] + ","+ arrGroup4.get(0)[i]);
                    out2.write("\r\n");
                }
            }
        }

        File file = new File(filePath);
        FileWriter out = new FileWriter(file);
        out.write(",固定步长,,波动步长,,纯随机,,聚合");
        out.write("\r\n");
        out.write(" arrLength,arrSize,tagSize,arrSize2,tagSize2,arrSize3,tagSize3,arrSize4,tagSize4");
        out.write("\r\n");
        for (int i = 0; i < lengthNum; i++) {
            out.write(arrLength[i] + "," + arrSize[i] + "," + tagSize[i] + "," + arrSize2[i] + "," + tagSize2[i] + "," + arrSize3[i] + "," + tagSize3[i] + "," + arrSize4[i] + "," + tagSize4[i]);
            out.write("\r\n");
        }
        out.close();


    }

    public static int[] stepData(int[] arr, int step, boolean waveStep) {
        arr[0] = 4000000;
        if (waveStep) {
            for (int i = 1; i < arr.length; i++) {
                arr[i] = arr[i - 1] + (int) (step * (1 + Math.random() * 0.01));
            }
        } else {
            for (int i = 1; i < arr.length; i++) {
                arr[i] = arr[i - 1] + step;
            }
        }
        return arr;
    }

    public static int[] randomData(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 4000000 + (int) (8000000 * (Math.random()));
        }
        Arrays.sort(arr);
        return arr;
    }

    public static int[] setData(int[] arr, int setNum) {
        int[] centerInt = new int[setNum];
        for (int i = 0; i < setNum; i++) {
            centerInt[i] = 4000000 + (int) (8000000 * (Math.random()));
        }

        int numPerSet = arr.length / setNum;
        for (int i = 0; i < setNum; i++) {
            arr[i * numPerSet] = centerInt[i];
            for (int j = 1; j < numPerSet; j++) {
                arr[i * numPerSet + j] = arr[i * numPerSet + j - 1] + (int) (300 * (Math.random()));
            }
        }
        Arrays.sort(arr);
        return arr;
    }
}
