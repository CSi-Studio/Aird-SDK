/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import net.csibio.aird.bean.common.IntPair;
import net.csibio.aird.bean.common.Xic;

import java.util.Arrays;
import java.util.List;

/**
 * Math Algorithm for Aird
 */
public class AirdMathUtil {

    /**
     * 根据概率q计算分位点
     *
     * @param values target values
     * @param q      target
     * @return result
     **/
    public static double calcQuantile(double[] values, double q) {
        if (values.length == 0) {
            return 0;
        }

        if (values.length == 1) {
            return values[0];
        }

        if (q > 1) {
            q = 1;
        }

        if (q < 0) {
            q = 0;
        }

        double[] vals = values.clone();

        Arrays.sort(vals);

        int ind1 = (int) Math.floor((vals.length - 1) * q);
        int ind2 = (int) Math.ceil((vals.length - 1) * q);

        return (vals[ind1] + vals[ind2]) / 2;
    }

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
        double maxValue = Double.MIN_VALUE;
        for (double value : values) {
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
        double minValue = Double.MAX_VALUE;
        for (double value : values) {
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
    public static IntPair binarySearch(double[] x, double value) {
        if (x.length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.length - 1]) {
            low = x.length - 1;
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
    public static IntPair binarySearch(Double[] x, Double value) {
        if (x.length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.length - 1]) {
            low = x.length - 1;
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
        if (x.length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.length - 1]) {
            low = x.length - 1;
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
    public static IntPair binarySearch(Float[] x, Float value) {
        if (x.length == 1) {
            return new IntPair(0, 0);
        }
        int high = x.length - 1;
        int low = 0;
        int mid;

        if (value < x[0]) {
            high = 0;
        } else if (value > x[x.length - 1]) {
            low = x.length - 1;
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
    public static IntPair binarySearch(List<Double> x, double value) {
        if (x.size() == 1) {
            return new IntPair(0, 0);
        }
        int high = x.size() - 1;
        int low = 0;
        int mid;

        if (value < x.get(0)) {
            high = 0;
        } else if (value > x.get(x.size() - 1)) {
            low = x.size() - 1;
        } else {
            while (high - low != 1) {
                mid = low + (high - low + 1) / 2;
                if (x.get(mid) < value) {
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
            intensity += intensityList.get(i);
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
            intensity += (xic.getRts()[i] - xic.getRts()[i - 1]) * (xic.getInts()[i - 1] + xic.getInts()[i]) / 2;
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
            double leftIntensity = xic.getInts()[i - 1] - (leftBaseline + (i - leftIndex - 1) * baselineStep);
            double rightIntensity = xic.getInts()[i] - (leftBaseline + (i - leftIndex) * baselineStep);
            if (leftIntensity < 0d || rightIntensity < 0d) {
                continue;
            }
            //area of trapezoid
            intensity += (xic.getRts()[i] - xic.getRts()[i - 1]) * (leftIntensity + rightIntensity) / 2d;
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
        double max = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < doubles.length; i++) {
            if (doubles[i] > max) {
                max = doubles[i];
                index = i;
            }
        }
        return index;
    }

}
