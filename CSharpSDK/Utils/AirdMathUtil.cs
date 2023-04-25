using System;
using System.Collections.Generic;
using AirdSDK.Beans.Common;
using CSharpFastPFOR.Port;

namespace AirdSDK.Utils;

public class AirdMathUtil
{
    
    /**
     * 二分查找法：找到目标数据在给定序列中的精准位置，若无则返回-1 要求数组按照从小到大的方法排序好
     *
     * @param numbers the search array
     * @param key     search key
     * @param low     left index
     * @param high    right index
     * @return the search result index
     */
    public static int binarySearch(double[] numbers, double key, int low, int high) {
        if (key < numbers[low] || key > numbers[high] || low > high) {
            return -1;
        }
        int middle = (low + high) / 2;
        if (numbers[middle] > key) {
            return binarySearch(numbers, key, low, middle - 1);
        } else if (numbers[middle] < key) {
            return binarySearch(numbers, key, middle + 1, high);
        } else {
            return middle;
        }
    }

    /**
     * get the max value of the search array
     *
     * @param values search array
     * @return the max value
     */
    public static double max(double[] values) {
        double maxValue = double.MinValue;
        foreach (double value in values) {
            if (value >= maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    /**
     * get the min value of the search array
     *
     * @param values search array
     * @return the min value
     */
    public static double min(double[] values) {
        double minValue = double.MaxValue;
        foreach (double value in values) {
            if (value <= minValue) {
                minValue = value;
            }
        }
        return minValue;
    }

    /**
     * Get index of first target bigger than mzStart
     *
     * @param x     search array
     * @param value search target
     * @return the left and right index of the target value
     */
    public static IntPair binarySearch(int[] x, int value) {
        if (x.Length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.Length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.Length - 1]) {
            low = x.Length - 1;
        } else {
            while (high - low != 1) {
                mid = low + (high - low + 1) / 2;
                if (x[mid] < value) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
        }
        return new IntPair(low, high);
    }

    /**
     * Get index of first target bigger than mzStart
     *
     * @param x     search array
     * @param value search target
     * @return the left and right index of the target value
     */
    public static IntPair binarySearch(double[] x, double value) {
        if (x.Length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.Length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.Length - 1]) {
            low = x.Length - 1;
        } else {
            while (high - low != 1) {
                mid = low + (high - low + 1) / 2;
                if (x[mid] < value) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
        }
        return new IntPair(low, high);
    }

    /**
     * Get index of first target bigger than mzStart
     *
     * @param x     search array
     * @param value search target
     * @return the left and right index of the target value
     */
    public static IntPair binarySearch(float[] x, float value) {
        if (x.Length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.Length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.Length - 1]) {
            low = x.Length - 1;
        } else {
            while (high - low != 1) {
                mid = low + (high - low + 1) / 2;
                if (x[mid] < value) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
        }

        return new IntPair(low, high);
    }

    /**
     * Get index of first target bigger than mzStart
     *
     * @param x     search array
     * @param value search target
     * @return the left and right index of the target value
     */
    public static IntPair binarySearch(List<double> x, double value) {
        if (x.Count == 1) {
            return new IntPair(0, 0);
        }
        int high = x.Count - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.Count - 1]) {
            low = x.Count - 1;
        } else {
            while (high - low != 1) {
                mid = low + (high - low + 1) / 2;
                if (x[mid] < value) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
        }

        return new IntPair(low, high);
    }

    /**
     * accumulate all the values' sum for the given range
     *
     * @param intensityList target values
     * @param leftIndex     left index of the given range
     * @param rightIndex    right index of the given range
     * @return the accumulate sum
     */
    public static double accumulate(List<Double> intensityList, int leftIndex, int rightIndex) {
        double intensity = 0f;
        for (int i = leftIndex; i < rightIndex; i++) {
            intensity += intensityList[i];
        }
        return intensity;
    }

    /**
     * accumulate all the values' sum for the given range
     *
     * @param xic        target xic instance
     * @param leftIndex  left index of the given range
     * @param rightIndex right index of the given range
     * @return the accumulate sum
     */
    public static double integrate(Xic xic, int leftIndex, int rightIndex) {
        double intensity = 0d;
        for (int i = leftIndex + 1; i <= rightIndex; i++) {
            //area of trapezoid
            intensity += (xic.rts[i] - xic.rts[i - 1]) * (xic.ints[i - 1] + xic.ints[i]) / 2;
        }
        return intensity * 60d;
    }

    /**
     * accumulate all the values' sum for the given range
     *
     * @param xic           target xic instance
     * @param leftIndex     left index of the given range
     * @param rightIndex    right index of the given range
     * @param leftBaseline  left baseline
     * @param rightBaseline right baseline
     * @return the accumulate sum
     */
    public static double integrate(Xic xic, int leftIndex, int rightIndex, double leftBaseline, double rightBaseline) {
        double intensity = 0d;
        double baselineStep = (rightBaseline - leftBaseline) / (rightIndex - leftIndex);
        for (int i = leftIndex + 1; i <= rightIndex; i++) {
            double leftIntensity = xic.ints[i - 1] - (leftBaseline + (i - leftIndex - 1) * baselineStep);
            double rightIntensity = xic.ints[i] - (leftBaseline + (i - leftIndex) * baselineStep);
            if (leftIntensity < 0d || rightIntensity < 0d) {
                continue;
            }
            //area of trapezoid
            intensity += (xic.rts[i] - xic.rts[i - 1]) * (leftIntensity + rightIntensity) / 2d;
        }
        return intensity * 60d;
    }

    /**
     * find max index of a give array
     *
     * @param doubles given array
     * @return the max value index
     */
    public static int findMaxIndex(double[] doubles) {
        double max = double.MinValue;
        int index = -1;
        for (int i = 0; i < doubles.Length; i++) {
            if (doubles[i] > max) {
                max = doubles[i];
                index = i;
            }
        }
        return index;
    }
}