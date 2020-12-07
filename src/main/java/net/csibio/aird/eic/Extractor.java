package net.csibio.aird.eic;

import net.csibio.aird.bean.MzIntensityPairs;

import java.util.List;

public class Extractor {

    /**
     * 计算 mz在[start, end]范围对应的intensity和
     *
     * @param pairs   其中mzArray已经从小到到已经排好序
     * @param mzStart
     * @param mzEnd
     * @return
     */
    public static float accumulation(MzIntensityPairs pairs, Float mzStart, Float mzEnd) {

        float[] mzArray = pairs.getMzArray();
        float[] intensityArray = pairs.getIntensityArray();

        float result = 0f;
        try {
            //Index of first mz bigger than mzStart
            int index = lowerBound(mzArray, mzStart);
            //No element is bigger than mzStart in mzArray
            if (index == -1) {
                return 0f;
            }
            int iterIndex = index;

            //Accumulate when iterIndex in (mzStart, mzEnd). Return 0 if rightIndex's mz is bigger than mzEnd.
            while (mzArray[iterIndex] <= mzEnd) {
                result += intensityArray[iterIndex];
                iterIndex++;
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static float[] accumulationWithGPU(List<MzIntensityPairs> pairsList, float[] mzStartArray, float[] mzEndArray) {

        int[] results = null;
        System.out.println("targets数目:"+mzStartArray.length);
        System.out.println("pairs数目:"+pairsList.size());
        for (int i = 0; i < pairsList.size(); i++) {
            results = LowerBound.doSearch(pairsList.get(i).getMzArray(), mzStartArray);
//            float[] mzArray = pairsList.get(i).getMzArray();
//            float[] intensityArray = pairsList.get(i).getIntensityArray();
//            float[] intensitySumArray = new float[mzStartArray.length];
//            for (int j = 0; j < results.length; i++) {
//                if (results[j] == -1) {
//                    intensitySumArray[j] = 0f;
//                }
//                int iterIndex = results[j];
//                float intensitySum = 0;
//                float mzEnd = mzEndArray[j];
//
//                //Accumulate when iterIndex in (mzStart, mzEnd). Return 0 if rightIndex's mz is bigger than mzEnd.
//                while (mzArray[iterIndex] <= mzEnd) {
//                    intensitySum += intensityArray[iterIndex];
//                    iterIndex++;
//                }
//                intensitySumArray[j] = intensitySum;
//            }
        }
        return null;
    }

    /**
     * 找到从小到大排序的第一个大于等于目标值的索引
     * 当目标值大于等于范围中的最大值时,返回-1
     * 左闭右开区间
     *
     * @param array
     * @param target
     * @return
     */
    public static int lowerBound(float[] array, Float target) {
        int rightIndex = array.length - 1;

        if (target <= array[0]) {
            return 0;
        }
        if (target >= array[rightIndex]) {
            return -1;
        }

        int leftIndex = 0;
        while (leftIndex + 1 < rightIndex) {
            int tmp = (leftIndex + rightIndex) >>> 1;
            if (target < array[tmp]) {
                rightIndex = tmp;
            } else if (target > array[tmp]) {
                leftIndex = tmp;
            } else {
                return tmp;
            }
        }

        return rightIndex;
    }

    /**
     * 找到从小到大排序的第一个大于目标值的索引
     * 当目标值小于范围中的最小值时,返回-1
     * 当目标值大于范围中的最大值时,返回-2
     *
     * @param array
     * @param target
     * @return
     */
    public static int findIndex(float[] array, float target, boolean isLeftIndex) {
        if (array == null) {
            return 0;
        }
        int leftIndex = 0, rightIndex = array.length - 1;
        if (isLeftIndex) {
            if (target < array[0]) {
                return 0;
            }
            if (target > array[rightIndex]) {
                return -1;
            }
        } else {
            if (target < array[0]) {
                return -1;
            }
            if (target > array[rightIndex]) {
                return rightIndex;
            }
        }

        while (leftIndex + 1 < rightIndex) {
            int tmp = (leftIndex + rightIndex) / 2;
            if (target < array[tmp]) {
                rightIndex = tmp;
            } else if (target > array[tmp]) {
                leftIndex = tmp;
            } else {
                return tmp;
            }
        }
        if (isLeftIndex) {
            return rightIndex;
        } else {
            return leftIndex;
        }
    }

    public static int findIndex(float[] array, float target, int leftIndex, int rightIndex, boolean isLeftIndex) {
        if (array == null) {
            return 0;
        }
        if (isLeftIndex) {
            if (target < array[0]) {
                return leftIndex;
            }
            if (target > array[rightIndex]) {
                return -1;
            }
        } else {
            if (target < array[0]) {
                return -1;
            }
            if (target > array[rightIndex]) {
                return rightIndex;
            }
        }

        while (leftIndex + 1 < rightIndex) {
            int tmp = (leftIndex + rightIndex) / 2;
            if (target < array[tmp]) {
                rightIndex = tmp;
            } else if (target > array[tmp]) {
                leftIndex = tmp;
            } else {
                return tmp;
            }
        }
        if (isLeftIndex) {
            return rightIndex;
        } else {
            return leftIndex;
        }
    }

    /**
     * 找到从小到大排序的第一个大于目标值的索引
     * 当目标值小于范围中的最小值时,返回-1
     * 当目标值大于范围中的最大值时,返回-2
     *
     * @param array
     * @param target
     * @return
     */
    public static int findIndex(Double[] array, Double target, boolean isLeftIndex) {
        if (array == null) {
            return 0;
        }
        int leftIndex = 0, rightIndex = array.length - 1;
        if (isLeftIndex) {
            if (target < array[0]) {
                return 0;
            }
            if (target > array[rightIndex]) {
                return -1;
            }
        } else {
            if (target < array[0]) {
                return -1;
            }
            if (target > array[rightIndex]) {
                return rightIndex;
            }
        }

        while (leftIndex + 1 < rightIndex) {
            int tmp = (leftIndex + rightIndex) / 2;
            if (target < array[tmp]) {
                rightIndex = tmp;
            } else if (target > array[tmp]) {
                leftIndex = tmp;
            } else {
                return tmp;
            }
        }
        if (isLeftIndex) {
            return rightIndex;
        } else {
            return leftIndex;
        }
    }
}
