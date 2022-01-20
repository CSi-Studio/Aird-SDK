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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.differential.IntegratedBinaryPacking;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import me.lemire.integercompression.differential.SkippableIntegratedComposition;
import net.csibio.aird.bean.Compressor;
import org.apache.commons.codec.binary.Base64;

/**
 * Compress Util
 *
 * @deprecated see net/csibio/aird/compressor package
 */
@Deprecated
public class CompressUtil {

  /**
   * compress the data with zlib algorithm
   *
   * @param data data to be compressed
   * @return compressed data
   */
  public static byte[] zlibEncoder(byte[] data) {
    byte[] output;

    Deflater compressor = new Deflater();

    compressor.reset();
    compressor.setInput(data);
    compressor.finish();
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
    try {
      byte[] buf = new byte[1024];
      while (!compressor.finished()) {
        int i = compressor.deflate(buf);
        bos.write(buf, 0, i);
      }
      output = bos.toByteArray();
    } catch (Exception e) {
      output = data;
      e.printStackTrace();
    } finally {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    compressor.end();
    return output;
  }

  /**
   * decompress the data with zlib
   *
   * @param data data to be decompressed
   * @return decompressed data
   */

  public static byte[] zlibDecoder(byte[] data) {
    byte[] output;
    Inflater inflater = new Inflater();
    inflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    try {
      byte[] buffer = new byte[10240];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        outputStream.write(buffer, 0, count);
      }
      output = outputStream.toByteArray();
    } catch (Exception e) {
      output = data;
      e.printStackTrace();
    } finally {
      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return output;
  }

  /**
   * decompress the data with zlib at a specified start and length
   *
   * @param data   data to be decoded
   * @param start  the start position of the data array
   * @param length the length for compressor to decode
   * @return decompressed data
   */
  public static byte[] zlibDecoder(byte[] data, int start, int length) {
    Inflater decompressor = new Inflater();
    decompressor.setInput(data, start, length);

    ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
    try {
      byte[] buff = new byte[1024];
      while (!decompressor.finished()) {
        int count = decompressor.inflate(buff);
        baos.write(buff, 0, count);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.close(baos);
    }
    decompressor.end();
    byte[] output = baos.toByteArray();

//        byte[] decompressedData = new byte[length * 10];
//        int i;
//
//        try {
//            i = decompresser.inflate(decompressedData);
//            decompressedData = ArrayUtils.subarray(decompressedData, 0, i);
//        } catch (DataFormatException e) {
//            e.printStackTrace();
//        }
    return output;
  }

  /**
   * ZDPD Encoder
   *
   * @param sortedFloats the sorted integers
   * @return the compressed data
   */
  public static byte[] ZDPDEncoder(float[] sortedFloats, Compressor compressor) {
    int precision = compressor.getPrecision();
    int[] sortedInts = new int[sortedFloats.length];
    for (int i = 0; i < sortedFloats.length; i++) {
      sortedInts[i] = (int) (precision * sortedFloats[i]);
    }
    int[] compressedInts = fastPforEncoder(sortedInts);
    return transToByte(compressedInts);
  }

  /**
   * ZDPD Encoder
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
  public static byte[] ZDPDEncoder(int[] sortedInts) {
    int[] compressedInts = fastPforEncoder(sortedInts);
    return transToByte(compressedInts);
  }

  /**
   * ZDPD Decoder
   *
   * @param compressedBytes the compressed data
   * @return the decompressed sorted integers
   */
  public static int[] ZDPDDecoder(byte[] compressedBytes) {
    int[] compressedInts = transToInteger(compressedBytes);
    return fastPforDecoder(compressedInts);
  }

  /**
   * decompress the data with fastpfor algorithm
   *
   * @param compressedInts 压缩对象
   * @return decompressed data
   */
  public static int[] fastPforDecoder(int[] compressedInts) {
    SkippableIntegratedComposition codec = new SkippableIntegratedComposition(
        new IntegratedBinaryPacking(), new IntegratedVariableByte());
    int size = compressedInts[0];
    // output vector should be large enough...
    int[] recovered = new int[size];
    IntWrapper inPoso = new IntWrapper(1);
    IntWrapper outPoso = new IntWrapper(0);
    IntWrapper recoffset = new IntWrapper(0);
    codec.headlessUncompress(compressedInts, inPoso, compressedInts.length, recovered, recoffset,
        size, outPoso);

    return recovered;
  }

  /**
   * compress the data with fastpfor algorithm
   *
   * @param sortedInt data to be decoded
   * @return compressed data
   */
  public static int[] fastPforEncoder(int[] sortedInt) {
    SkippableIntegratedComposition codec = new SkippableIntegratedComposition(
        new IntegratedBinaryPacking(), new IntegratedVariableByte());
    int[] compressed = new int[sortedInt.length + 1024];
    IntWrapper inputoffset = new IntWrapper(0);
    IntWrapper outputoffset = new IntWrapper(1);
    codec.headlessCompress(sortedInt, inputoffset, sortedInt.length, compressed, outputoffset,
        new IntWrapper(0));
    compressed[0] = sortedInt.length;
    compressed = Arrays.copyOf(compressed, outputoffset.intValue());

    return compressed;
  }

  /**
   * get the compressor for m/z
   *
   * @param compressors 压缩策略
   * @return the m/z compressor
   */
  public static Compressor getMzCompressor(List<Compressor> compressors) {
    if (compressors == null) {
      return null;
    }
    for (Compressor compressor : compressors) {
      if (compressor.getTarget().equals(Compressor.TARGET_MZ)) {
        return compressor;
      }
    }
    return null;
  }

  /**
   * get the intensity compressor for intensity
   *
   * @param compressors 压缩策略
   * @return the intensity compressor
   */
  public static Compressor getIntCompressor(List<Compressor> compressors) {
    if (compressors == null) {
      return null;
    }
    for (Compressor compressor : compressors) {
      if (compressor.getTarget().equals(Compressor.TARGET_INTENSITY)) {
        return compressor;
      }
    }
    return null;
  }

  /**
   * compress a float array with zlib and convert the binary data into string with Base64 algorithm
   *
   * @param target 压缩对象
   * @return base64 string
   */
  public static String transToString(float[] target) {
    FloatBuffer fbTarget = FloatBuffer.wrap(target);
    ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
    bbTarget.asFloatBuffer().put(fbTarget);
    byte[] targetArray = bbTarget.array();
    byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
    String targetStr = new String(new Base64().encode(compressedArray));
    return targetStr;
  }

  /**
   * compress an integer array with zlib and convert the binary data into string with Base64
   * algorithm
   *
   * @param target array to be compressed and transformed
   * @return base64 string
   */
  public static String transToString(int[] target) {
    IntBuffer ibTarget = IntBuffer.wrap(target);
    ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
    bbTarget.asIntBuffer().put(ibTarget);
    byte[] targetArray = bbTarget.array();
    byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
    String targetStr = new String(new Base64().encode(compressedArray));
    return targetStr;
  }

  /**
   * compress the int array with zlib algorithm
   *
   * @param target array to be compressed and transformed
   * @return compressed data
   */
  public static byte[] transToByte(short[] target) {
    ShortBuffer ibTarget = ShortBuffer.wrap(target);
    ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 2);
    bbTarget.order(ByteOrder.LITTLE_ENDIAN);
    bbTarget.asShortBuffer().put(ibTarget);
    byte[] targetArray = bbTarget.array();
    byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
    return compressedArray;
  }

  /**
   * compress the int array with zlib algorithm
   *
   * @param target array to be compressed and transformed
   * @return compressed data
   */
  public static byte[] transToByte(int[] target) {
    IntBuffer ibTarget = IntBuffer.wrap(target);
    ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
    bbTarget.order(ByteOrder.LITTLE_ENDIAN);
    bbTarget.asIntBuffer().put(ibTarget);
    byte[] targetArray = bbTarget.array();
    byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
    return compressedArray;
  }

  /**
   * compress the float array with zlib algorithm
   *
   * @param target array to be compressed and transformed
   * @return compressed data
   */
  public static byte[] transToByte(float[] target) {
    FloatBuffer fbTarget = FloatBuffer.wrap(target);
    ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
    bbTarget.asFloatBuffer().put(fbTarget);
    byte[] targetArray = bbTarget.array();
    byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
    return compressedArray;
  }

  /**
   * decompress the binary data with zlib algorithm
   *
   * @param value array to be decompressed and transformed
   * @return decompressed data
   */
  public static float[] transToFloat(byte[] value) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(value);
    byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(byteBuffer.array()));

    FloatBuffer floats = byteBuffer.asFloatBuffer();
    float[] floatValues = new float[floats.capacity()];
    for (int i = 0; i < floats.capacity(); i++) {
      floatValues[i] = floats.get(i);
    }

    byteBuffer.clear();
    return floatValues;
  }

  /**
   * decompress the binary data with zlib algorithm
   *
   * @param value array to be decompressed and transformed
   * @param order ByteOrder
   * @return decompressed data
   */
  public static int[] transToInteger(byte[] value, ByteOrder order) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
    if (order == null) {
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    } else {
      byteBuffer.order(order);
    }
    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }

    byteBuffer.clear();
    return intValues;
  }

  /**
   * decompress the binary data with zlib algorithm
   *
   * @param value array to be decompressed and transformed
   * @param order ByteOrder
   * @return decompressed data
   */
  public static short[] transToShort(byte[] value, ByteOrder order) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
    if (order == null) {
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    } else {
      byteBuffer.order(order);
    }
    ShortBuffer shorts = byteBuffer.asShortBuffer();
    short[] shortValues = new short[shorts.capacity()];
    for (int i = 0; i < shorts.capacity(); i++) {
      shortValues[i] = shorts.get(i);
    }

    byteBuffer.clear();
    return shortValues;
  }

  /**
   * decompress the binary data with zlib algorithm
   *
   * @param value array to be decompressed and transformed
   * @return decompressed data
   */
  public static int[] transToInteger(byte[] value) {
    return transToInteger(value, ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * decompress the binary data with algorithm
   *
   * @param value array to be decompressed and transformed
   * @return decompressed data
   */
  public static short[] transToShort(byte[] value) {
    return transToShort(value, ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * decompress the binary data with zlib algorithm
   *
   * @param value array to be decompressed and transformed
   * @return decompressed data
   */
  public static int[] bytesToInteger(byte[] value) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(value);
    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }

    byteBuffer.clear();
    return intValues;
  }

}
