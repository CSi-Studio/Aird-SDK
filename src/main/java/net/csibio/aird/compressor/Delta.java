/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
