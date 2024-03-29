/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.eic;

import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.opencl.XIC;

import java.util.List;

/**
 * Extractor 是一个用于进行XIC计算的核心函数,支持使用CPU或者是GPU进行大规模的计算 Extractor is an core algorithm class for XIC
 * computation with both CPU and GPU
 */
public class Extractor {

    /**
     * 计算 mz在[mzStart, mzEnd]范围对应的intensity的总和 Get the Low Bound Index for mzStart and High Bound
     * Index for mzEnd in pairs.mzArray, then accumulate the intensity from the LowBoundIndex to
     * HighBoundIndex
     *
     * @param spectrum mzArray is an ordered array
     * @param mzStart  target mz start
     * @param mzEnd    target mz end
     * @return the intensity sum as extractor result
     */
    public static double accumulation(Spectrum spectrum, Double mzStart, Double mzEnd) {

        double[] mzArray = spectrum.getMzs();
        double[] intensityArray = spectrum.getInts();

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

    /**
     * 使用GPU进行大批量的二分查找
     *
     * @param pairsList     需要被查找的光谱图mz原始数据队列,长度为n
     * @param targetMzArray 需要搜索的目标mz队列,长度为m
     * @param mzWindow      mz搜索宽度,一般为0.05或者0.03
     * @return 每一个目标m/z在各个原始数据队列中的intensity累加值,阵列大小为n*m
     */
    public static double[][] accumulationWithGPU(List<Spectrum> pairsList, double[] targetMzArray, double mzWindow) {
        double[][] resMatrix = new double[pairsList.size()][targetMzArray.length];
        //每一个批次处理的光谱数
        int countInBatch = 5;

        //准备补齐至countInBatch的整倍数
        int delta = countInBatch - pairsList.size() % countInBatch;
        if (delta != countInBatch) {
            for (int k = 0; k < delta; k++) {
                pairsList.add(new Spectrum(new double[0], new double[0]));
            }
        }

        XIC.initialize(countInBatch);
        for (int i = 0; i < pairsList.size(); i = i + countInBatch) {
            double[] results = XIC.lowerBoundWithGPU(pairsList.subList(i, i + countInBatch), targetMzArray, mzWindow);
            resMatrix[i] = results;
        }
        XIC.shutdown();
        return resMatrix;
    }

    /**
     * 找到从小到大排序的第一个大于等于目标值的索引 当目标值大于等于范围中的最大值时,返回-1 左闭右开区间
     *
     * @param array  目标数组
     * @param target 目标值
     * @return 目标索引
     */
    public static int lowerBound(float[] array, float target) {
        int high = array.length - 1;

        if (target <= array[0]) {
            return 0;
        }
        if (target >= array[high]) {
            return -1;
        }

        int low = 0;
        while (low + 1 < high) {
            int mid = (low + high) >>> 1;
            if (target < array[mid]) {
                high = mid;
            } else if (target > array[mid]) {
                low = mid;
            } else {
                return mid;
            }
        }

        return high;
    }

    /**
     * 找到从小到大排序的第一个大于等于目标值的索引 当目标值大于等于范围中的最大值时,返回-1 左闭右开区间
     *
     * @param array  目标数组
     * @param target 目标值
     * @return 目标索引
     */
    public static int lowerBound(double[] array, double target) {
        int high = array.length - 1;

        if (target <= array[0]) {
            return 0;
        }
        if (target >= array[high]) {
            return -1;
        }

        int low = 0;
        while (low + 1 < high) {
            int mid = (low + high) >>> 1;
            if (target < array[mid]) {
                high = mid;
            } else if (target > array[mid]) {
                low = mid;
            } else {
                return mid;
            }
        }

        return high;
    }
}
