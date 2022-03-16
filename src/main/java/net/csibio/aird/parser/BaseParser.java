/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.TreeMap;
import lombok.Data;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.CompressorType;
import net.csibio.aird.compressor.bytes.Brotli;
import net.csibio.aird.compressor.bytes.ByteComp;
import net.csibio.aird.compressor.bytes.Snappier;
import net.csibio.aird.compressor.bytes.ZSTD;
import net.csibio.aird.compressor.bytes.Zlib;
import net.csibio.aird.compressor.ints.BinPacking;
import net.csibio.aird.compressor.ints.IntComp;
import net.csibio.aird.compressor.ints.IntegratedBinaryPack;
import net.csibio.aird.compressor.ints.IntegratedVarByte;
import net.csibio.aird.compressor.ints.VarByte;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdScanUtil;
import net.csibio.aird.util.FileUtil;

/**
 * Base Parser
 */
@Data
public abstract class BaseParser {

  /**
   * the aird file
   */
  public File airdFile;

  /**
   * the aird index file. JSON format
   */
  public File indexFile;

  /**
   * the airdInfo from the index file.
   */
  public AirdInfo airdInfo;

  /**
   * the m/z compressor
   */
  public Compressor mzCompressor;

  /**
   * the intensity compressor
   */
  public Compressor intCompressor;

  /**
   * 用于PASEF的压缩内核
   */
  public Compressor mobiCompressor;

  /**
   * 使用的压缩内核
   */
  public IntComp mzIntComp;
  public ByteComp mzByteComp;

  public IntComp intIntComp;
  public ByteComp intByteComp;

  public IntComp mobiIntComp;
  public ByteComp mobiByteComp;

  /**
   * the m/z precision
   */
  public double mzPrecision;

  /**
   * the intensity precision, 1dp
   */
  public double intPrecision = 10d;

  /**
   * the mobility precision, 7dp
   */
  public double mobiPrecision = 10000000d;

  /**
   * Acquisition Method Type Supported by Aird
   *
   * @see net.csibio.aird.enums.AirdType
   */
  public String type;

  /**
   * Random Access File reader
   */
  public RandomAccessFile raf;

  /**
   * 构造函数
   */
  public BaseParser() {
  }

  /**
   * 构造函数
   *
   * @param indexPath 索引文件的位置
   * @throws ScanException 扫描时的异常
   */
  public BaseParser(String indexPath) throws Exception {
    this.indexFile = new File(indexPath);
    this.airdFile = new File(AirdScanUtil.getAirdPathByIndexPath(indexPath));
    try {
      raf = new RandomAccessFile(airdFile, "r");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
    }
    airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
    if (airdInfo == null) {
      throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
    }
    mzCompressor = getTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MZ);
    intCompressor = getTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_INTENSITY);
    mobiCompressor = getTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MOBILITY);
    initCompressor();
    mzPrecision = mzCompressor.getPrecision();
    type = airdInfo.getType();
  }

  /**
   * 构造函数
   *
   * @param indexPath 索引文件的位置
   * @throws ScanException 扫描时的异常
   */
  public BaseParser(String indexPath, AirdInfo airdInfo) throws Exception {
    if (airdInfo == null) {
      throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
    }
    this.indexFile = new File(indexPath);
    this.airdFile = new File(AirdScanUtil.getAirdPathByIndexPath(indexPath));
    try {
      raf = new RandomAccessFile(airdFile, "r");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
    }
    this.airdInfo = airdInfo;

    mzCompressor = getTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MZ);
    intCompressor = getTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_INTENSITY);
    mobiCompressor = getTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MOBILITY);
    initCompressor();
    mzPrecision = mzCompressor.getPrecision();
    type = airdInfo.getType();
  }

  /**
   * 使用直接的关键信息进行初始化
   *
   * @param airdPath       Aird文件路径
   * @param mzCompressor   mz压缩策略
   * @param intCompressor  intensity压缩策略
   * @param mobiCompressor mobility压缩策略
   * @param mzPrecision    mz数字精度
   * @param airdType       aird类型
   * @throws ScanException 扫描异常
   */
  public BaseParser(String airdPath, Compressor mzCompressor, Compressor intCompressor,
      Compressor mobiCompressor,
      int mzPrecision, String airdType) throws Exception {
    this.indexFile = new File(AirdScanUtil.getIndexPathByAirdPath(airdPath));
    this.airdFile = new File(airdPath);

    try {
      raf = new RandomAccessFile(airdFile, "r");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
    }
    this.mzCompressor = mzCompressor;
    this.intCompressor = intCompressor;
    this.mobiCompressor = mobiCompressor;
    this.mzPrecision = mzPrecision;
    initCompressor();
    this.type = airdType;
  }

  public static BaseParser buildParser(String indexPath) throws Exception {
    File indexFile = new File(indexPath);
    if (indexFile.exists() && indexFile.canRead()) {
      AirdInfo airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
      if (airdInfo == null) {
        throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
      }
      return switch (AirdType.getType(airdInfo.getType())) {
        case DDA -> new DDAParser(indexPath, airdInfo);
        case DIA -> new DIAParser(indexPath, airdInfo);
        case PRM -> new PRMParser(indexPath, airdInfo);
        case COMMON -> new CommonParser(indexPath, airdInfo);
        default -> throw new IllegalStateException(
            "Unexpected value: " + AirdType.getType(airdInfo.getType()));
      };
    }
    throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
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

  public static Compressor getTargetCompressor(List<Compressor> compressors, String target) {
    if (compressors == null) {
      return null;
    }
    for (Compressor compressor : compressors) {
      if (compressor.getTarget().equals(target)) {
        return compressor;
      }
    }
    return null;
  }

  public void initCompressor() throws Exception {
    List<String> mzMethods = mzCompressor.getMethods();
    if (mzMethods.size() == 2) {
      switch (CompressorType.getByName(mzMethods.get(0))) {
        case IBP -> mzIntComp = new IntegratedBinaryPack();
        case IVB -> mzIntComp = new IntegratedVarByte();
        case Unknown -> throw new Exception("Unknown mz integer compressor");
        default -> throw new Exception("Unknown mz integer compressor");
      }
      switch (CompressorType.getByName(mzMethods.get(1))) {
        case Zlib -> mzByteComp = new Zlib();
        case Brotli -> mzByteComp = new Brotli();
        case Snappy -> mzByteComp = new Snappier();
        case Zstd -> mzByteComp = new ZSTD();
        case Unknown -> throw new Exception("Unknown mz byte compressor");
        default -> throw new Exception("Unknown mz byte compressor");
      }
    }
    List<String> intMethods = intCompressor.getMethods();
    if (intMethods.size() == 2) {
      switch (CompressorType.getByName(intMethods.get(0))) {
        case VB -> intIntComp = new VarByte();
        case BP -> intIntComp = new BinPacking();
        case Unknown -> throw new Exception("Unknown intensity integer compressor");
        default -> throw new Exception("Unknown intensity integer compressor");
      }

      switch (CompressorType.getByName(intMethods.get(1))) {
        case Zlib -> intByteComp = new Zlib();
        case Brotli -> intByteComp = new Brotli();
        case Snappy -> intByteComp = new Snappier();
        case Zstd -> intByteComp = new ZSTD();
        case Unknown -> throw new Exception("Unknown intensity byte compressor");
        default -> throw new Exception("Unknown intensity byte compressor");
      }
    }
    if (mobiCompressor != null) {
      List<String> mobiMethods = mobiCompressor.getMethods();
      if (mobiMethods.size() == 2) {
        switch (CompressorType.getByName(mobiMethods.get(0))) {
          case VB -> mobiIntComp = new VarByte();
          case BP -> mobiIntComp = new BinPacking();
          case Unknown -> throw new Exception("Unknown mobi integer compressor");
          default -> throw new Exception("Unknown mobi integer compressor");
        }

        switch (CompressorType.getByName(mobiMethods.get(1))) {
          case Zlib -> mobiByteComp = new Zlib();
          case Brotli -> mobiByteComp = new Brotli();
          case Snappy -> mobiByteComp = new Snappier();
          case Zstd -> mobiByteComp = new ZSTD();
          case Unknown -> throw new Exception("Unknown mobi byte compressor");
          default -> throw new Exception("Unknown mobi byte compressor");
        }
      }
    }
  }

  /**
   * get AirdInfo
   *
   * @return AirdInfo
   */
  public AirdInfo getAirdInfo() {
    return airdInfo;
  }

  /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
   * <p>
   * the result key is rt,value is the spectrum(mz-intensity pairs) In particular, this function
   * will not close the RAF object directly after using it. Users need to close the object manually
   * after using the diaparser object
   *
   * @param start      起始指针位置 start point
   * @param end        结束指针位置 end point
   * @param rtList     rt列表,包含所有的光谱产出时刻 the retention time list
   * @param mzOffsets  mz块的大小列表 the mz block size list
   * @param intOffsets intensity块的大小列表 the intensity block size list
   * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
   */
  public TreeMap<Float, Spectrum<double[]>> getSpectra(long start, long end, List<Float> rtList,
      List<Integer> mzOffsets, List<Integer> intOffsets) {

    TreeMap<Float, Spectrum<double[]>> map = new TreeMap<>();
    try {
      raf.seek(start);
      long delta = end - start;
      byte[] result = new byte[(int) delta];
      raf.read(result);

      int iter = 0;
      for (int i = 0; i < rtList.size(); i++) {
        map.put(rtList.get(i), getSpectrum(result, iter, mzOffsets.get(i), intOffsets.get(i)));
        iter = iter + mzOffsets.get(i) + intOffsets.get(i);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.BLOCK_PARSE_ERROR);
    }
  }

  /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
   * <p>
   * the result key is rt,value is the spectrum(mz-intensity pairs) In particular, this function
   * will not close the RAF object directly after using it. Users need to close the object manually
   * after using the diaparser object
   *
   * @param start      起始指针位置 start point
   * @param end        结束指针位置 end point
   * @param rtList     rt列表,包含所有的光谱产出时刻 the retention time list
   * @param mzOffsets  mz块的大小列表 the mz block size list
   * @param intOffsets intensity块的大小列表 the intensity block size list
   * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
   */
  public TreeMap<Float, Spectrum<float[]>> getSpectraAsFloat(long start, long end,
      List<Float> rtList, List<Integer> mzOffsets, List<Integer> intOffsets) {

    TreeMap<Float, Spectrum<float[]>> map = new TreeMap<>();
    try {
      raf.seek(start);
      long delta = end - start;
      byte[] result = new byte[(int) delta];
      raf.read(result);

      int iter = 0;
      for (int i = 0; i < rtList.size(); i++) {
        map.put(rtList.get(i),
            getSpectrumAsFloat(result, iter, mzOffsets.get(i), intOffsets.get(i)));
        iter = iter + mzOffsets.get(i) + intOffsets.get(i);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.BLOCK_PARSE_ERROR);
    }
  }

  /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
   * <p>
   * the result key is rt,value is the spectrum(mz-intensity pairs) In particular, this function
   * will not close the RAF object directly after using it. Users need to close the object manually
   * after using the diaparser object
   *
   * @param start      起始指针位置 start point
   * @param end        结束指针位置 end point
   * @param rtList     rt列表,包含所有的光谱产出时刻 the retention time list
   * @param mzOffsets  mz块的大小列表 the mz block size list
   * @param intOffsets intensity块的大小列表 the intensity block size list
   * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
   */
  public TreeMap<Float, Spectrum<int[]>> getSpectraAsInteger(long start, long end,
      List<Float> rtList, List<Integer> mzOffsets, List<Integer> intOffsets) {

    TreeMap<Float, Spectrum<int[]>> map = new TreeMap<>();
    try {
      raf.seek(start);
      long delta = end - start;
      byte[] result = new byte[(int) delta];
      raf.read(result);

      int iter = 0;
      for (int i = 0; i < rtList.size(); i++) {
        map.put(rtList.get(i),
            getSpectrumAsInteger(result, iter, mzOffsets.get(i), intOffsets.get(i)));
        iter = iter + mzOffsets.get(i) + intOffsets.get(i);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.BLOCK_PARSE_ERROR);
    }
  }

  public Spectrum<double[]> getSpectrum(byte[] bytes, int offset, int mzOffset, int intOffset) {
    double[] mzArray = getMzs(bytes, offset, mzOffset);
    offset = offset + mzOffset;
    float[] intensityArray = getIntValues(bytes, offset, intOffset);
    return new Spectrum<double[]>(mzArray, intensityArray);
  }

  public Spectrum<float[]> getSpectrumAsFloat(byte[] bytes, int offset, int mzOffset,
      int intOffset) {

    float[] mzArray = getMzsAsFloat(bytes, offset, mzOffset);
    offset = offset + mzOffset;
    float[] intensityArray = getIntValues(bytes, offset, intOffset);
    return new Spectrum<float[]>(mzArray, intensityArray);
  }

  public Spectrum<int[]> getSpectrumAsInteger(byte[] bytes, int offset, int mzOffset,
      int intOffset) {

    int[] mzArray = getMzsAsInteger(bytes, offset, mzOffset);
    offset = offset + mzOffset;
    float[] intensityArray = getIntValues(bytes, offset, intOffset);
    return new Spectrum<int[]>(mzArray, intensityArray);
  }

  /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value 压缩后的数组
   * @return 解压缩后的数组
   */
  public double[] getMzs(byte[] value) {
    return getMzs(value, 0, value.length);
  }

  /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value  压缩后的数组
   * @param offset 起始位置
   * @param length 读取长度
   * @return 解压缩后的数组
   */
  public double[] getMzs(byte[] value, int offset, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(mzByteComp.decode(value, offset, length));
    byteBuffer.order(mzCompressor.fetchByteOrder());

    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }
    intValues = mzIntComp.decode(intValues);
    double[] doubleValues = new double[intValues.length];
    for (int index = 0; index < intValues.length; index++) {
      doubleValues[index] = intValues[index] / mzPrecision;
    }
    byteBuffer.clear();
    return doubleValues;
  }

  /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value 压缩后的数组
   * @return 解压缩后的数组
   */
  public float[] getMzsAsFloat(byte[] value) {
    return getMzsAsFloat(value, 0, value.length);
  }

  /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value  压缩后的数组
   * @param offset 起始位置
   * @param length 读取长度
   * @return 解压缩后的数组
   */
  public float[] getMzsAsFloat(byte[] value, int offset, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(mzByteComp.decode(value, offset, length));
    byteBuffer.order(mzCompressor.fetchByteOrder());

    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }
    intValues = mzIntComp.decode(intValues);
    float[] floats = new float[intValues.length];
    for (int index = 0; index < intValues.length; index++) {
      floats[index] = (float) (intValues[index] / mzPrecision);
    }
    byteBuffer.clear();
    return floats;
  }

  /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN
   *
   * @param value 加密的数组
   * @return 解压缩后的数组
   */
  public int[] getMzsAsInteger(byte[] value) {
    return getMzsAsInteger(value, 0, value.length);
  }

  /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value  压缩后的数组
   * @param offset 起始位置
   * @param length 读取长度
   * @return 解压缩后的数组
   */
  public int[] getMzsAsInteger(byte[] value, int offset, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(mzByteComp.decode(value, offset, length));
    byteBuffer.order(mzCompressor.fetchByteOrder());

    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }
    byteBuffer.clear();
    return intValues;
  }

  /**
   * get tag values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value 压缩后的数组
   * @return 解压缩后的数组
   */
  public int[] getTags(byte[] value) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new Zlib().decode(value));
    byteBuffer.order(mzCompressor.fetchByteOrder());

    byte[] byteValue = new byte[byteBuffer.capacity() * 8];
    for (int i = 0; i < byteBuffer.capacity(); i++) {
      for (int j = 0; j < 8; j++) {
        byteValue[8 * i + j] = (byte) (((byteBuffer.get(i) & 0xff) >> j) & 1);
      }
    }
    int digit = mzCompressor.getDigit();
    int[] tags = new int[byteValue.length / digit];
    for (int i = 0; i < tags.length; i++) {
      for (int j = 0; j < digit; j++) {
        tags[i] += byteValue[digit * i + j] << j;
      }
    }
    byteBuffer.clear();
    return tags;
  }

  /**
   * get tag values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value  压缩后的数组
   * @param start  起始位置
   * @param length 读取长度
   * @return 解压缩后的数组
   */
  public int[] getTags(byte[] value, int start, int length) {
    byte[] tagShift = new Zlib().decode(value, start, length);
//        byteBuffer.order(mzCompressor.getByteOrder());

    byte[] byteValue = new byte[tagShift.length * 8];
    for (int i = 0; i < tagShift.length; i++) {
      for (int j = 0; j < 8; j++) {
        byteValue[8 * i + j] = (byte) (((tagShift[i] & 0xff) >> j) & 1);
      }
    }

    int digit = mzCompressor.getDigit();
    int[] tags = new int[byteValue.length / digit];
    for (int i = 0; i < tags.length; i++) {
      for (int j = 0; j < digit; j++) {
        tags[i] += byteValue[digit * i + j] << j;
      }
    }
    return tags;
  }

  /**
   * get intensity values only for aird file
   *
   * @param value 压缩的数组
   * @return 解压缩后的数组
   */
  public float[] getIntValues(byte[] value) {
    return getIntValues(value, 0, value.length);
  }

  /**
   * get intensity values from the start point with a specified length
   *
   * @param value  the original array
   * @param start  the start point
   * @param length the specified length
   * @return the decompression intensity array
   */
  public float[] getIntValues(byte[] value, int start, int length) {

    ByteBuffer byteBuffer = ByteBuffer.wrap(intByteComp.decode(value, start, length));
    byteBuffer.order(intCompressor.fetchByteOrder());

    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }
    intValues = intIntComp.decode(intValues);

    float[] intensityValues = new float[intValues.length];
    for (int i = 0; i < intValues.length; i++) {
      int intensity = intValues[i];
      if (intensity < 0) {
        intensity = (int) Math.pow(2, intensity / 100000d);
      }
      intensityValues[i] = intensity / 10f;
    }

    byteBuffer.clear();
    return intensityValues;
  }

  /**
   * Close the raf object
   */
  public void close() {
    FileUtil.close(raf);
  }
}
