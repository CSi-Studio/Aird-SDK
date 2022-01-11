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
