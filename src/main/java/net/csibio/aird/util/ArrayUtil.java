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

import java.util.Iterator;
import java.util.Set;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.structure.SortInt;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Pair;

/**
 * Array Util
 */
public class ArrayUtil {

  /**
   * @param originalArray 原数组 original array
   * @param currentLayer  当前层数 current layer
   * @return 排序后的int数组 the sorted integer array
   */
  public static SortInt[] transToSortIntArray(int[] originalArray, int currentLayer) {
    SortInt[] sortInts = new SortInt[originalArray.length];
    for (int i = 0; i < originalArray.length; i++) {
      sortInts[i] = new SortInt(originalArray[i], currentLayer);
    }
    return sortInts;
  }

  public static Spectrum<float[]> trans(Spectrum<double[]> spectrum) {
    float[] floats = new float[spectrum.getMzs().length];
    for (int i = 0; i < spectrum.getMzs().length; i++) {
      floats[i] = (float) spectrum.getMzs()[i];
    }
    return new Spectrum<float[]>(floats, spectrum.getInts());
  }

  public static double[] fromFloatToDouble(float[] array) {
    if (array == null) {
      return null;
    }
    double[] newArray = new double[array.length];
    for (int i = 0; i < array.length; i++) {
      newArray[i] = array[i];
    }
    return newArray;
  }

  public static int[] fromDoubleToInt(double[] array, double precision) {
    if (array == null) {
      return null;
    }
    int[] newArray = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      newArray[i] = (int) (array[i] * precision);
    }
    return newArray;
  }

  /**
   * @param sortInts         已经排序的int数组 the sorted integer array
   * @param totalLayersCount 堆叠占位数,例如2层堆叠需要1位表示(即0和1),4层堆叠需要2位数表示(即00,01,10,11)
   * @return 恢复堆叠数组为正常数组
   */
  public static Pair<int[], byte[]> transToOriginArrayAndLayerNote(SortInt[] sortInts,
      int totalLayersCount) {

    int[] mz = new int[sortInts.length];
    Integer layerNote = 0b00;

    byte[] layerNoteBytes = new byte[(int) (Math.ceil(sortInts.length / 8d * totalLayersCount))];
    int count = 0;
    int addWhenMatch = 8;
    int currentByteLocation = 0;

    for (int i = 0; i < sortInts.length; i++) {
      mz[i] = sortInts[i].getNumber();
      layerNote = layerNote << totalLayersCount;
      layerNote |= sortInts[i].getLayer();
      count += totalLayersCount;

      if (count < addWhenMatch) {
        continue;
      }

      if (count == addWhenMatch) {
        layerNoteBytes[currentByteLocation] = layerNote.byteValue();
      } else {
        layerNoteBytes[currentByteLocation] = Integer.valueOf(layerNote >> (count - addWhenMatch))
            .byteValue();
        layerNote = layerNote << (32 - (count - addWhenMatch)) >>> (32 - (count - addWhenMatch));
      }
      currentByteLocation++;
      count -= addWhenMatch;
    }
    //最后补齐
    if (count != 0) {
      layerNoteBytes[currentByteLocation] = layerNote.byteValue();
    }
    return new Pair<>(mz, ArrayUtils.subarray(layerNoteBytes, 0, currentByteLocation + 1));
  }

  /**
   * Gets the average value of the difference between adjacent numbers in the array
   *
   * @param array target array
   * @return the average value
   */
  public static long avgDelta(int[] array) {
    long delta = 0;
    for (int i = 0; i < array.length - 1; i++) {
      if ((array[i + 1] - array[i]) == 0) {
        delta++;
      }
    }
    return delta;
  }

  /**
   * convert the Float type into float type
   *
   * @param floatSet target set
   * @return float array
   */
  public static float[] toPrimitive(Set<Float> floatSet) {
    if (floatSet.size() == 0) {
      return null;
    }
    float[] fArray = new float[floatSet.size()];
    int i = 0;
    Iterator<Float> iterator = floatSet.iterator();
    while (iterator.hasNext()) {
      fArray[i] = iterator.next();
      i++;
    }

    return fArray;
  }

  public static boolean isSame(float[] a, float[] b) {
    if (a.length != b.length) {
      return false;
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i] != b[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean isSame(byte[] a, byte[] b) {
    if (a.length != b.length) {
      return false;
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i] != b[i]) {
        return false;
      }
    }
    return true;
  }
}
