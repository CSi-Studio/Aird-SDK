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

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

@Data
public class Compressor {

  /**
   * 压缩策略的指向对象,目前仅支持mz对象和intensity对象两种 The compressed targets. Now only support for m/z and
   * intensity
   */
  public static String TARGET_MZ = "mz";
  public static String TARGET_INTENSITY = "intensity";
  
  /**
   * 目前支持的压缩算法,包括无损的zlib,pFor算法以及有损的log10算法 The compression algorithms. Now support for zlib,
   * fastPFor and log10 algorithms
   */
  public static String METHOD_ZLIB = "zlib";
  public static String METHOD_STACK = "stack";
  public static String METHOD_PFOR = "pFor";
  public static String METHOD_LOG10 = "log10";

  /**
   * 数组中mz和intensity的精度,1000代表精确到小数点后4位,10代表精确到小数点后1位 The default precision for m/z is 4dp. The
   * default precision for intensity is 1dp
   */
  public static int PRECISION_MZ = 10000;
  public static int PRECISION_INT = 10;

  /**
   * 压缩对象,支持mz和intensity两种 Compression Target. Support for mz array and intensity array
   */
  String target;

  /**
   * 压缩方法,使用分号隔开 Compression Method, using comma to split the compression method with order.
   */
  List<String> methods;

  @Deprecated
  String method;

  /**
   * 数组的数值精度,10代表精确到小数点后1位,1000代表精确到小数点后三位,以此类推 The precision for mz and intensity.
   */
  Integer precision;

  /**
   * Use for Stack-ZDPD algorithm 2^digit = layers
   */
  Integer digit;

  /**
   * ByteOrder,Aird格式的默认ByteOrder为LITTLE_ENDIAN,此项为扩展项,目前仅支持默认值LITTLE_ENDIAN ByteOrder
   */
  String byteOrder;

  public Integer getPrecision() {
    if (precision != null) {
      return precision;
    }

    if (target.equals(TARGET_MZ)) {
      return 1000;
    }

    if (target.equals(TARGET_INTENSITY)) {
      return 10;
    }
    return null;
  }

  public ByteOrder fetchByteOrder() {
    return ByteOrder.LITTLE_ENDIAN;
  }

  public List<String> getMethods() {
    if (methods != null) {
      return methods;
    } else if (method != null) {
      String[] methodArray = method.split(",");
      return Arrays.asList(methodArray);
    }
    return new ArrayList<>();
  }
}
