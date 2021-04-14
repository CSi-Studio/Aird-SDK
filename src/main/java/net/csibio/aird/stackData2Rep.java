package net.csibio.aird;

import lombok.Data;
import net.csibio.aird.util.CompressUtil;
import org.apache.commons.math3.util.FastMath;

import java.util.*;

//对数组的index进行移位缩短操作后，使用zlib压缩
public class stackData2Rep {
    public static void main(String[] args) {
        for (int k = 8; k < 9; k++) {
            int arrNum = (int) Math.pow(2, k);
            //生成有序数组
            List<int[]> arrGroup = new ArrayList<>();
            for (int i = 0; i < arrNum; i++) {
                int[] arr = new int[8000 + (int) (Math.random() * 100)];//模拟一张光谱3000多个m/z
                arr[0] = 1000000;//每个mz是百万级的整数
                for (int j = 1; j < arr.length; j++) {
                    arr[j] = arr[j - 1] + (int) (Math.random() * 100000);
                }
                arrGroup.add(arr);
            }

            long t1 =System.currentTimeMillis();
            Stack stack = stackEncode(arrGroup);
            System.out.println("压缩时间：" + (System.currentTimeMillis() - t1));
            System.out.println();

            long t2 = System.currentTimeMillis();
//            List<int[]> stackDecodeAbort = stackDecodeAbort(stack);
            List<int[]> stackDecode = stackDecode(stack);
            System.out.println("解压时间：" + (System.currentTimeMillis() - t2));
            System.out.println();

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

    //pair：默认采取pair成对排序，True也采取pair排序；false时采用QueueSort
    public static Stack stackEncode(List<int[]> arrGroup, boolean pair) {
        int stackLen = 0;//记录堆叠数总长度
        for (int[] arr : arrGroup) {
            stackLen += arr.length;
        }

        List<int[]> tempArrGroup = new ArrayList<>();
        for (int i = 0; i < arrGroup.size(); i++) {
            tempArrGroup.add(Arrays.copyOf(arrGroup.get(i), arrGroup.get(i).length));
        }

        //合并排序数组
        int[][] stackSort;
        if (pair) {
            stackSort = getPairSortArray(tempArrGroup);
        } else {
            stackSort = getQueueSortArray(tempArrGroup);
        }
        //取出stack数组和index数组
        int[] stackArr = new int[stackLen];
        int[] stackIndex = new int[stackLen];
        for (int i = 0; i < stackLen; i++) {
            stackArr[i] = stackSort[i][0];
            stackIndex[i] = stackSort[i][1];
        }
        //index移位存储
        int digit = (int) Math.ceil(Math.log(arrGroup.size()) / Math.log(2));
        int indexLen = (stackLen * digit - 1) / 8 + 1;
        byte[] value = new byte[8 * indexLen];
        for (int i = 0; i < stackLen; i++) {
            int fromIndex = digit * i;
            for (int j = 0; j < digit; j++) {
                value[fromIndex + j] = (byte) ((stackIndex[i] >> j) & 1);
            }
        }
        //把8个byte并为1个byte，用byte数组存是因为zlib压缩的是byte
        byte[] indexShift = new byte[indexLen];
        for (int i = 0; i < indexLen; i++) {
            int temp = 0;
            for (int j = 0; j < 8; j++) {
                temp += value[8 * i + j] << j;
                indexShift[i] = (byte) temp;
            }
        }
        //数组用fastPFor压缩，index用zlib压缩，并记录层数
        Stack stack = new Stack();
        stack.comArr = CompressUtil.transToByte(CompressUtil.fastPforEncoder(stackArr));
        stack.comIndex = CompressUtil.zlibEncoder(indexShift);
        stack.digit = digit;
        return stack;
    }

    public static Stack stackEncode(List<int[]> arrGroup) {
        int stackLen = 0;//记录堆叠数总长度
        for (int[] arr : arrGroup) {
            stackLen += arr.length;
        }

        List<int[]> tempArrGroup = new ArrayList<>();
        for (int i = 0; i < arrGroup.size(); i++) {
            tempArrGroup.add(Arrays.copyOf(arrGroup.get(i), arrGroup.get(i).length));
        }

        //合并排序数组
        long t = System.currentTimeMillis();
//        int[][] stackSort = getFullSortArray(tempArrGroup);
//        int[][] stackSort = getQueueSortArray(tempArrGroup);
        int[][] stackSort = getPairSortArray(tempArrGroup);
        System.out.println("合并排序数组时间:" + (System.currentTimeMillis() - t));

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
            for (int j = 0; j < digit; j++) {
                value[fromIndex + j] = (byte) ((stackIndex[i] >> j) & 1);
            }
        }

        //把8个byte并为1个byte，用byte数组存是因为zlib压缩的是byte
        byte[] indexShift = new byte[indexLen];
        for (int i = 0; i < indexLen; i++) {
            int temp = 0;
            for (int j = 0; j < 8; j++) {
                temp += value[8 * i + j] << j;
                indexShift[i] = (byte) temp;
            }
        }
        System.out.println("索引移位时间:" + (System.currentTimeMillis() - t0));

        //数组用fastPFor压缩，index用zlib压缩，并记录层数
        Stack stack = new Stack();
        long t1 = System.currentTimeMillis();
        stack.comArr = CompressUtil.transToByte(CompressUtil.fastPforEncoder(stackArr));
        System.out.println("zlib+Pfor压缩数组时间：" + (System.currentTimeMillis() - t1));
        long t2 =System.currentTimeMillis();
        stack.comIndex = CompressUtil.zlibEncoder(indexShift);
        System.out.println("zlib压缩索引时间：" + (System.currentTimeMillis() - t2));
        stack.digit = digit;
        return stack;
    }

    public static List<int[]> stackDecode(Stack stack) {
//        long t1 = System.currentTimeMillis();
        int[] stackArr = CompressUtil.fastPforDecoder(CompressUtil.transToIntegerLongArray(stack.getComArr()));
//        System.out.println("pfor+zlib解压数组时间：" + (System.currentTimeMillis() - t1));
        int[] stackIndex = new int[stackArr.length];
//        long t2 = System.currentTimeMillis();
        byte[] indexShift = CompressUtil.zlibDecoderLongArray(stack.getComIndex());
//        System.out.println("zlib索引解压时间：" + (System.currentTimeMillis() - t2));
        int digit = stack.getDigit();

        //拆分byte为8个bit，并分别存储
//        long t = System.currentTimeMillis();
        byte[] value = new byte[8 * indexShift.length];
        for (int i = 0; i < indexShift.length; i++) {
            for (int j = 0; j < 8; j++) {
                value[8 * i + j] = (byte) (((indexShift[i] & 0xff) >> j) & 1);
            }
        }
        //还原为int类型的index
        for (int i = 0; i < stackIndex.length; i++) {
            for (int j = 0; j < digit; j++) {
                stackIndex[i] += value[digit * i + j] << j;
            }
        }
//        System.out.println("还原index时间:" + (System.currentTimeMillis() - t));

        //统计index数组中各个元素出现的次数
//        long t3 = System.currentTimeMillis();
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int index : stackIndex) {
            map.merge(index, 1, Integer::sum);
        }
//        System.out.println("统计长度时间：" + (System.currentTimeMillis() - t3));

        //根据index拆分stackArr,还原数组
//        long t4 = System.currentTimeMillis();
        List<int[]> arrGroup = new ArrayList<>();
        int arrNum = map.keySet().size();
        for (int i = 0; i < arrNum; i++) {
            arrGroup.add(new int[map.get(i)]);
        }
        int[] p = new int[arrNum];
        for (int i = 0; i < stackIndex.length; i++) {
            arrGroup.get(stackIndex[i])[p[stackIndex[i]]++] = stackArr[i];
        }
//        System.out.println("拆分数组时间：" + (System.currentTimeMillis() - t4));
        return arrGroup;
    }



    @Data
    public static class Stack {
        private byte[] comArr;
        private byte[] comIndex;
        private int digit;
    }


    private static int[][] getFullSortArray(List<int[]> arrGroup) {
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
        return stackSort;
    }

    //Comparison in pairs
    private static int[][] getPairSortArray(List<int[]> arrGroup) {
        List<int[]> indexGroup = new ArrayList<>();
        indexGroup.add(new int[arrGroup.get(0).length]);
        for (int i = 1; i < arrGroup.size(); i++) {
            int[] indexes = new int[arrGroup.get(i).length];
            Arrays.fill(indexes, i);
            indexGroup.add(indexes);
        }
        int mergeTimes = (int) FastMath.log(2, arrGroup.size());
        for (int i = 1; i <= mergeTimes; i++) {
            int stepWidth = (int) Math.pow(2, i);
            int tempMergeTime = arrGroup.size() / stepWidth;

            //multi threads
//            List<Integer> tempMergeTimeList = new ArrayList<>();
//            for (int j = 0; j < tempMergeTime; j ++) {
//                tempMergeTimeList.add(j);
//            }
//            tempMergeTimeList.parallelStream().forEach(j -> {

            //single thread
            for (int j = 0; j < tempMergeTime; j++) {
                int leftIndex = j * stepWidth;
                int rightIndex = leftIndex + stepWidth / 2;
                int[] dataArr1 = arrGroup.get(leftIndex);
                int[] dataArr2 = arrGroup.get(rightIndex);
                int[] indexArr1 = indexGroup.get(leftIndex);
                int[] indexArr2 = indexGroup.get(rightIndex);
                int[] dataArr = new int[dataArr1.length + dataArr2.length];
                int[] indexArr = new int[dataArr.length];
                int index1 = 0, index2 = 0, index = 0;
                for (int k = 0; k < dataArr.length; k++) {
                    if (index1 >= dataArr1.length) {
                        dataArr[index] = dataArr2[index2];
                        indexArr[index] = indexArr2[index2];
                        index2++;
                        index++;
                        continue;
                    }
                    if (index2 >= dataArr2.length) {
                        dataArr[index] = dataArr1[index1];
                        indexArr[index] = indexArr1[index1];
                        index1++;
                        index++;
                        continue;
                    }

                    if (dataArr1[index1] <= dataArr2[index2]) {
                        dataArr[index] = dataArr1[index1];
                        indexArr[index] = indexArr1[index1];
                        index1++;
                    } else {
                        dataArr[index] = dataArr2[index2];
                        indexArr[index] = indexArr2[index2];
                        index2++;
                    }
                    index++;
                }
                indexGroup.set(leftIndex, indexArr);
                arrGroup.set(leftIndex, dataArr);
            }//single thread
//            });//multi threads
        }
        int[] arr = arrGroup.get(0);
        int[] index = indexGroup.get(0);
        int[][] resultArr = new int[arr.length][2];
        for (int i = 0; i < resultArr.length; i++) {
            resultArr[i][0] = arr[i];
            resultArr[i][1] = index[i];
        }
        return resultArr;
    }

    //Maintain a sorted queue
    private static int[][] getQueueSortArray(List<int[]> arrGroup) {
        int stackLen = 0;//记录堆叠数总长度
        for (int[] arr : arrGroup) {
            stackLen += arr.length;
        }
        int[][] resultArr = new int[stackLen][2];
        int[] indexes = new int[arrGroup.size()];
        Arrays.fill(indexes, 1);
        PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return Integer.compare(o1[0], o2[0]);
            }
        });
        for (int i = 0; i < arrGroup.size(); i++) {
            priorityQueue.offer(new int[]{arrGroup.get(i)[0], i});
        }
        for (int i = 0; i < stackLen; i++) {
            int[] node = priorityQueue.poll();
            resultArr[i] = node;
            int groupIndex = node[1];
            int index = indexes[groupIndex];
            if (index < arrGroup.get(groupIndex).length) {
                priorityQueue.offer(new int[]{arrGroup.get(groupIndex)[index], groupIndex});
                indexes[groupIndex]++;
            }
        }
        return resultArr;
    }
}
