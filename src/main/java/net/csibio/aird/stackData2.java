package net.csibio.aird;

import lombok.Data;
import net.csibio.aird.util.CompressUtil;

import java.util.*;

//对数组的index进行移位缩短操作后，使用zlib压缩
public class stackData2 {
    public static void main(String[] args) {
        for (int k = 10; k < 11; k++) {
            int arrNum = (int) Math.pow(2, k);
            //生成有序数组
            List<int[]> arrGroup = new LinkedList<>();
            for (int i = 0; i < arrNum; i++) {
                int[] arr = new int[3000 + (int) (Math.random() * 100)];//模拟一张光谱3000多个m/z
                arr[0] = 1000000;//每个mz是百万级的整数
                for (int j = 1; j < arr.length; j++) {
                    arr[j] = arr[j - 1] + (int) (Math.random() * 100000);
                }
                arrGroup.add(arr);
            }
            Stack stack = stackEncode(arrGroup);
            List<int[]> stackDecode = stackDecode(stack);
            boolean a = Boolean.TRUE;
            for (int i = 0; i < arrGroup.size(); i++) {
                a = Arrays.equals(arrGroup.get(i), stackDecode.get(i));
                if (!a) {
                    break;
                }
            }
            System.out.println("对照压缩前后数组是否相同" + a);
        }
    }

    //目前至多测到2048层，把所有的索引以二进制位存储的方式存到map中
//    private static HashMap<Integer, byte[]> indexMap = new HashMap<>();
//    static {
//        for (int i = 0; i < 2048; i++) {
//            byte[] byteArr = new byte[11];
//            for (int j = 0; j < 11; j++) {
//                byteArr[j] = (byte) ((i >> j) & 1);
//            }
//            indexMap.put(i, byteArr);
//        }
//    }

    public static Stack stackEncode(List<int[]> arrGroup) {
        //生成堆叠数组和索引
        long t = System.currentTimeMillis();
        int stackLen = 0;//记录堆叠数总长度
        for (int[] arr : arrGroup) {
            stackLen += arr.length;
        }
        int[][] stackSort = new int[stackLen][2];//二维数组分别存储堆叠数字和层号

        int index = 0;
        int arrLen = 0;
        for (int[] arr : arrGroup) {
            for (int i = 0; i < arr.length; i++) {
                stackSort[i + arrLen][0] = arr[i];
                stackSort[i + arrLen][1] = index;
            }
            index++;
            arrLen += arr.length;
        }
        Arrays.sort(stackSort, Comparator.comparingInt(a -> a[0]));//根据堆叠数对二维数组升序排列

//        int arrsNum = arrGroup.size();
//        //初始化合并数组
//        int[] arr0 = arrGroup.get(0);
//        int arrLen = arr0.length;
//        for (int i = 0; i < arrLen; i++) {
//            stackSort[i][0] = arr0[i];
//            stackSort[i][1] = 0;
//        }
//        int index = 1;
//        for (int i = 1; i < arrsNum; i++) {
//            int[] arri = arrGroup.get(i);
//            int arriLen = arri.length;
//            int p = arrLen - 1;//合并数组的指针
//            int pi = arriLen - 1;//新加数组的指针
//            int tail = arriLen + arrLen - 1;//从尾部开始合并，避免覆盖
//            while (p >= 0 || pi >= 0) {
//                if (p == -1) {
//                    stackSort[tail][0] = arri[pi--];
//                    stackSort[tail][1] = index;
//                } else if (pi == -1) {
//                    stackSort[tail][0] = stackSort[p][0];
//                    stackSort[tail][1] = stackSort[p--][1];
//                } else if (stackSort[p][0] > arri[pi]) {
//                    stackSort[tail][0] = stackSort[p][0];
//                    stackSort[tail][1] = stackSort[p--][1];
//                } else {
//                    stackSort[tail][0] = arri[pi--];
//                    stackSort[tail][1] = index;
//                }
//                tail--;
//            }
//            arrLen += arriLen;
//            index++;
//        }

//        System.out.println("合并排序数组时间:" + (System.currentTimeMillis() - t));

        //取出stack数组和index数组
        int[] stackArr = new int[stackSort.length];
        int[] stackIndex = new int[stackSort.length];
        for (int i = 0; i < stackSort.length; i++) {
            stackArr[i] = stackSort[i][0];
            stackIndex[i] = stackSort[i][1];
        }

        //index移位存储
        long t0 = System.currentTimeMillis();
        int digit = (int) Math.ceil(Math.log(arrGroup.size()) / Math.log(2));
        int indexLen = (stackLen * digit - 1) / 8 + 1;
        byte[] value = new byte[8 * indexLen];
        for (int i = 0; i < stackLen; i++) {
            int fromIndex = digit * i;
//            byte[] byteIndex = indexMap.get(stackIndex[i]);
//            for (int k = 0; k < digit; k++) {
//                value[fromIndex + k] = byteIndex[k];
//            }
            for (int j = 0; j < digit; j++) {
                value[fromIndex + j] = (byte) ((stackIndex[i] >> j) & 1);
            }
        }
//        System.out.println("移位时间:" + (System.currentTimeMillis() - t0));

        //把8个byte并为1个byte，用byte数组存是因为zlib压缩的是byte
        long t1 = System.currentTimeMillis();
        byte[] indexShift = new byte[indexLen];
        for (int i = 0; i < indexLen; i++) {
            int temp = 0;
            for (int j = 0; j < 8; j++) {
                temp += value[8 * i + j] << j;
                indexShift[i] = (byte) temp;
            }
        }
//        System.out.println("合并byte时间:" + (System.currentTimeMillis() - t1));

        //数组用fastPFor压缩，index用zlib压缩，并记录层数
        Stack stack = new Stack();
        long t3 = System.currentTimeMillis();
        stack.comArr = CompressUtil.transToByte(CompressUtil.fastPforEncoder(stackArr));
//        System.out.println("Pfor时间：" + (System.currentTimeMillis() - t3));
        stack.comIndex = CompressUtil.zlibEncoder(indexShift);
        stack.digit = digit;
        return stack;
    }

    public static List<int[]> stackDecode(Stack stack) {
        int[] stackArr = CompressUtil.fastPforDecoder(CompressUtil.transToInteger(stack.getComArr()));
        int[] stackIndex = new int[stackArr.length];
        byte[] indexShift = CompressUtil.zlibDecoder(stack.getComIndex());
        int digit = stack.getDigit();

        //拆分byte为8个bit，并分别存储
        long t0 = System.currentTimeMillis();
        byte[] value = new byte[8 * indexShift.length];
        for (int i = 0; i < indexShift.length; i++) {
            for (int j = 0; j < 8; j++) {
                value[8 * i + j] = (byte) (((indexShift[i] & 0xff) >> j) & 1);
            }
        }
//        System.out.println("拆分时间:" + (System.currentTimeMillis() - t0));

        //还原为int类型的index
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < stackIndex.length; i++) {
            for (int j = 0; j < digit; j++) {
                stackIndex[i] += value[digit * i + j] << j;
            }
        }
//        System.out.println("移位时间:" + (System.currentTimeMillis() - t1));

        //合并数组和索引为一个二维数组
        int[][] stackSort = new int[stackArr.length][2];
        for (int i = 0; i < stackArr.length; i++) {
            stackSort[i][0] = stackArr[i];
            stackSort[i][1] = stackIndex[i];
        }
        Arrays.sort(stackSort, (a, b) -> (a[1] == b[1] ? a[0] - b[0] : a[1] - b[1]));

        //统计index数组中各个元素出现的次数
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < stackIndex.length; i++) {
            if (map.get(stackIndex[i]) != null) {
                map.put(stackIndex[i], map.get(stackIndex[i]) + 1);
            } else {
                map.put(stackIndex[i], 1);
            }
        }

        //为了不改变原来的stackIndex，clone一个，方便debug；可以不clone，直接对stackIndex排序
        int[] sortIndex = stackIndex.clone();
        Arrays.sort(sortIndex);
        int maxIndex = sortIndex[sortIndex.length - 1];

        //根据index拆分stackArr,还原数组
        List<int[]> arrGroup = new LinkedList<>();
        int fromIndex = 0;
        for (int i = 0; i < maxIndex + 1; i++) {
            int[] arr = new int[map.get(i)];
            for (int j = 0; j < map.get(i); j++) {
                arr[j] = stackSort[fromIndex + j][0];
            }
            fromIndex += map.get(i);
            arrGroup.add(arr);
        }
        return arrGroup;
    }

    //合并两个有序数组为一个
    public static int[] merge(int[] nums1, int n1, int[] nums2, int n2) {
        int p1 = n1 - 1, p2 = n2 - 1;
        int tail = n1 + n2 - 1;
        int cur;
        while (p1 >= 0 || p2 >= 0) {
            if (p1 == -1) {
                cur = nums2[p2--];
            } else if (p2 == -1) {
                cur = nums1[p1--];
            } else if (nums1[p1] > nums2[p2]) {
                cur = nums1[p1--];
            } else {
                cur = nums2[p2--];
            }
            nums1[tail--] = cur;
        }
        return nums1;
    }

    @Data
    public static class Stack {
        private byte[] comArr;
        private byte[] comIndex;
        private int digit;
    }
}
