/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;

@Data
public class Layers {

  /**
   * 使用fastPfor算法压缩以后的mz数组 compressed mz array with fastPfor
   */
  byte[] mzArray;

  /**
   * mz对应的层索引 layer index of mz
   */
  byte[] tagArray;

  /**
   * 存储单个索引所需的位数 storage digits occupied by an index
   */
  int digit;

  public Layers() {
  }

  public Layers(byte[] mzArray, byte[] indexArray, int digit) {
    this.mzArray = mzArray;
    this.tagArray = indexArray;
    this.digit = digit;
  }


}
