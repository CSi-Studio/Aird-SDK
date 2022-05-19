package net.csibio.aird.util;

import java.util.Arrays;
import java.util.List;
import net.csibio.aird.bean.common.IntPair;
import net.csibio.aird.bean.common.Xic;

/**
 * @Author: An Shaowei
 * @Date: 2019/12/18 14:55
 * @Description: 数学方法
 */
public class AirdMathUtil {

  /**
   * @param values
   * @param q
   * @Description: 根据概率q计算分位点
   * @return: double
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
   * @param key
   * @return
   */
  public static int binarySearchExact(double[] numbers, double key, int low, int high) {
    if (key < numbers[low] || key > numbers[high] || low > high) {
      return -1;
    }
    int middle = (low + high) / 2;
    if (numbers[middle] > key) {
      return binarySearchExact(numbers, key, low, middle - 1);
    } else if (numbers[middle] < key) {
      return binarySearchExact(numbers, key, middle + 1, high);
    } else {
      return middle;
    }
  }

  public static double max(double[] values) {
    double maxValue = Double.MIN_VALUE;
    for (double value : values) {
      if (value >= maxValue) {
        maxValue = value;
      }
    }
    return maxValue;
  }

  public static double min(double[] values) {
    double minValue = Double.MAX_VALUE;
    for (double value : values) {
      if (value <= minValue) {
        minValue = value;
      }
    }
    return minValue;
  }

  //Get index of first mz bigger than mzStart
  public static IntPair search(double[] x, double value) {
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

  //Get index of first mz bigger than mzStart
  public static IntPair search(Double[] x, Double value) {
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

  //Get index of first mz bigger than mzStart
  public static IntPair search(float[] x, float value) {
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

  //Get index of first mz bigger than mzStart
  public static IntPair search(Float[] x, Float value) {
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

  public static IntPair search(List<Double> x, double value) {
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

  public static double accumulate(List<Double> intensityList, int leftIndex, int rightIndex) {
    double intensity = 0f;
    for (int i = leftIndex; i < rightIndex; i++) {
      intensity += intensityList.get(i);
    }
    return intensity;
  }

  public static double integrate(Xic xic, int leftIndex, int rightIndex) {
    double intensity = 0d;
    for (int i = leftIndex + 1; i <= rightIndex; i++) {
      //area of trapezoid
      intensity +=
          (xic.getRts()[i] - xic.getRts()[i - 1]) * (xic.getInts()[i - 1] + xic.getInts()[i]) / 2;
    }
    return intensity * 60d;
  }

  public static double integrate(Xic xic, int leftIndex, int rightIndex, double leftBaseline,
      double rightBaseline) {
    double intensity = 0d;
    double baselineStep = (rightBaseline - leftBaseline) / (rightIndex - leftIndex);
    for (int i = leftIndex + 1; i <= rightIndex; i++) {
      double leftIntensity =
          xic.getInts()[i - 1] - (leftBaseline + (i - leftIndex - 1) * baselineStep);
      double rightIntensity = xic.getInts()[i] - (leftBaseline + (i - leftIndex) * baselineStep);
      if (leftIntensity < 0d || rightIntensity < 0d) {
        continue;
      }
      //area of trapezoid
      intensity += (xic.getRts()[i] - xic.getRts()[i - 1]) * (leftIntensity + rightIntensity) / 2d;
    }
    return intensity * 60d;
  }

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
