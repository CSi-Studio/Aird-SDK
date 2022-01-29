package net.csibio.aird.compressor;

public final class Delta {

  /**
   * Apply differential coding (in-place).
   *
   * @param data data to be modified
   */
  public static int[] delta(int[] data) {
    int[] res = new int[data.length];
    res[0] = data[0];
    for (int i = 1; i < data.length; i++) {
      res[i] = data[i] - data[i - 1];
    }
    return res;
  }

  /**
   * Undo differential coding (in-place). Effectively computes a prefix sum.
   *
   * @param data to be modified.
   */
  public static int[] recover(int[] data) {
    int[] res = new int[data.length];
    res[0] = data[0];
    for (int i = 1; i < data.length; ++i) {
      res[i] = data[i] + res[i - 1];
    }
    return res;
  }

}
