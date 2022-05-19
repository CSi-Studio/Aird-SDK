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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.TreeMap;
import lombok.Data;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MobiInfo;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.BrotliWrapper;
import net.csibio.aird.compressor.bytecomp.ByteComp;
import net.csibio.aird.compressor.bytecomp.SnappyWrapper;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.BinPackingWrapper;
import net.csibio.aird.compressor.intcomp.Empty;
import net.csibio.aird.compressor.intcomp.IntComp;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.SortedIntComp;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.enums.ByteCompType;
import net.csibio.aird.enums.IntCompType;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.enums.SortedIntCompType;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdScanUtil;
import net.csibio.aird.util.FileUtil;

/**
 * Base Parser
 */
@Data
public abstract class BaseParser {

  public static final int MAX_READ_SIZE = Integer.MAX_VALUE / 100;
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

  public double mzPrecision;
  public double intPrecision;
  public double mobiPrecision;
  /**
   * 使用的压缩内核
   */
  public SortedIntComp mzIntComp;
  public ByteComp mzByteComp;

  public IntComp intIntComp;
  public ByteComp intByteComp;

  public IntComp mobiIntComp;
  public ByteComp mobiByteComp;

  /**
   * Random Access File reader
   */
  public RandomAccessFile raf;

  /**
   * Mobility 字典
   */
  public double[] mobiDict;

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
    airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
    if (airdInfo == null) {
      throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
    }

    this.airdFile = new File(AirdScanUtil.getAirdPathByIndexPath(indexPath));
    try {
      raf = new RandomAccessFile(airdFile, "r");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
    }

    parseCompsFromAirdInfo();
    parserComboComp();
    parseMobilityDict();
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
    this.airdInfo = airdInfo;

    this.airdFile = new File(AirdScanUtil.getAirdPathByIndexPath(indexPath));
    try {
      raf = new RandomAccessFile(airdFile, "r");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
    }

    parseCompsFromAirdInfo();
    parserComboComp();
    parseMobilityDict();
  }

  /**
   * 使用不读取Index文件,直接使用关键信息进行初始化的方法
   *
   * @param airdPath       Aird文件路径
   * @param mzCompressor   mz压缩策略
   * @param intCompressor  intensity压缩策略
   * @param mobiCompressor mobility压缩策略
   * @param airdType       aird类型
   * @throws ScanException 扫描异常
   */
  public BaseParser(String airdPath, Compressor mzCompressor, Compressor intCompressor,
      Compressor mobiCompressor, String airdType) throws Exception {
    this.indexFile = new File(AirdScanUtil.getIndexPathByAirdPath(airdPath));

    //不使用Index文件初始化的时候,会直接初始化一个空AirdInfo,用于存放传入的基础信息
    this.airdInfo = new AirdInfo();
    airdInfo.setType(airdType);
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
    parserComboComp();
    parseMobilityDict();
  }

  public static BaseParser buildParser(String indexPath) throws Exception {
    File indexFile = new File(indexPath);
    return buildParser(indexFile);
  }

  /**
   * 最基础的启动方法:使用Index文件扫描AirdInfo以后读取Aird文件,然后根据AirdInfo中的文件类型分别初始化不同的Parser
   *
   * @param indexFile
   * @return
   * @throws Exception
   */
  public static BaseParser buildParser(File indexFile) throws Exception {
    if (indexFile.exists() && indexFile.canRead()) {
      AirdInfo airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
      if (airdInfo == null) {
        throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
      }
      return switch (AirdType.getType(airdInfo.getType())) {
        case DDA_PASEF -> new DDAPasefParser(indexFile.getAbsolutePath(), airdInfo);
        case DIA_PASEF -> new DIAPasefParser(indexFile.getAbsolutePath(), airdInfo);
        case DDA -> new DDAParser(indexFile.getAbsolutePath(), airdInfo);
        case DIA -> new DIAParser(indexFile.getAbsolutePath(), airdInfo);
        case PRM -> new PRMParser(indexFile.getAbsolutePath(), airdInfo);
        case COMMON -> new CommonParser(indexFile.getAbsolutePath(), airdInfo);
        default -> throw new IllegalStateException(
            "Unexpected value: " + AirdType.getType(airdInfo.getType()));
      };
    }
    throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
  }

  public static Compressor fetchTargetCompressor(List<Compressor> compressors, String target) {
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

  /**
   * 必须读取索引文件以及Aird二进制文件才可以获取Dict字典
   *
   * @throws IOException
   */
  public void parseMobilityDict() throws IOException {
    MobiInfo mobiInfo = airdInfo.getMobiInfo();
    if ("TIMS".equals(mobiInfo.getType())) {
      raf.seek(mobiInfo.getDictStart());
      long delta = mobiInfo.getDictEnd() - mobiInfo.getDictStart();
      byte[] result = new byte[(int) delta];
      raf.read(result);
      int[] mobiArray = new IntegratedVarByteWrapper().decode(
          ByteTrans.byteToInt(new ZstdWrapper().decode(result)));
      double[] mobiDArray = new double[mobiArray.length];
      for (int i = 0; i < mobiArray.length; i++) {
        mobiDArray[i] = mobiArray[i] / getMobiPrecision();
      }
      this.mobiDict = mobiDArray;
    }
  }

  public void parseCompsFromAirdInfo() {
    mzCompressor = fetchTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MZ);
    intCompressor = fetchTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_INTENSITY);
    mobiCompressor = fetchTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MOBILITY);
    mzPrecision = mzCompressor.getPrecision();
    intPrecision = intCompressor.getPrecision();
    mobiPrecision = mobiCompressor.getPrecision();
  }

  public void parserComboComp() throws Exception {
    List<String> mzMethods = mzCompressor.getMethods();
    if (mzMethods.size() == 2) {
      switch (SortedIntCompType.getByName(mzMethods.get(0))) {
        case IBP -> mzIntComp = new IntegratedBinPackingWrapper();
        case IVB -> mzIntComp = new IntegratedVarByteWrapper();
        default -> throw new Exception("Unknown mz integer compressor");
      }
      switch (ByteCompType.getByName(mzMethods.get(1))) {
        case Zlib -> mzByteComp = new ZlibWrapper();
        case Brotli -> mzByteComp = new BrotliWrapper();
        case Snappy -> mzByteComp = new SnappyWrapper();
        case Zstd -> mzByteComp = new ZstdWrapper();
        default -> throw new Exception("Unknown mz byte compressor");
      }
    }
    List<String> intMethods = intCompressor.getMethods();
    if (intMethods.size() == 2) {
      switch (IntCompType.getByName(intMethods.get(0))) {
        case VB -> intIntComp = new VarByteWrapper();
        case BP -> intIntComp = new BinPackingWrapper();
        case Empty -> intIntComp = new Empty();
        default -> throw new Exception("Unknown intensity integer compressor");
      }

      switch (ByteCompType.getByName(intMethods.get(1))) {
        case Zlib -> intByteComp = new ZlibWrapper();
        case Brotli -> intByteComp = new BrotliWrapper();
        case Snappy -> intByteComp = new SnappyWrapper();
        case Zstd -> intByteComp = new ZstdWrapper();
        default -> throw new Exception("Unknown intensity byte compressor");
      }
    }
    if (mobiCompressor != null) {
      List<String> mobiMethods = mobiCompressor.getMethods();
      if (mobiMethods.size() == 2) {
        switch (IntCompType.getByName(mobiMethods.get(0))) {
          case VB -> mobiIntComp = new VarByteWrapper();
          case BP -> mobiIntComp = new BinPackingWrapper();
          case Empty -> mobiIntComp = new Empty();
          default -> throw new Exception("Unknown mobi integer compressor");
        }

        switch (ByteCompType.getByName(mobiMethods.get(1))) {
          case Zlib -> mobiByteComp = new ZlibWrapper();
          case Brotli -> mobiByteComp = new BrotliWrapper();
          case Snappy -> mobiByteComp = new SnappyWrapper();
          case Zstd -> mobiByteComp = new ZstdWrapper();
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
   * 根据位移偏差解析单张光谱图
   *
   * @param bytes
   * @param offset
   * @param mzOffset
   * @param intOffset
   * @return
   */
  public Spectrum getSpectrum(byte[] bytes, int offset, int mzOffset, int intOffset) {
    if (mzOffset == 0) {
      return new Spectrum(new double[0], new double[0]);
    }
    double[] mzArray = getMzs(bytes, offset, mzOffset);
    offset = offset + mzOffset;
    double[] intensityArray = getInts(bytes, offset, intOffset);
    return new Spectrum(mzArray, intensityArray);
  }

  /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息
   * 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
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
  public TreeMap<Double, Spectrum> getSpectra(long start, long end, List<Double> rtList,
      List<Integer> mzOffsets, List<Integer> intOffsets) {

    TreeMap<Double, Spectrum> map = new TreeMap<>();
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
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息
   * 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
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
  public TreeMap<Double, Spectrum> getSpectra(long start, long end, List<Double> rtList,
      List<Integer> mzOffsets, List<Integer> intOffsets, List<Integer> mobiOffsets) {

    TreeMap<Double, Spectrum> map = new TreeMap<>();
    try {
      //首先计算压缩块的总大小
      long delta = end - start;
      int rtIndex = 0;
      //如果块体积大于整数最大值,则进行分段解析
      while (delta > MAX_READ_SIZE) {
        raf.seek(start);//确认起始点
        byte[] result = new byte[MAX_READ_SIZE];//读取一个最大块,其中的有效数据应该是小于该块的大小的
        raf.read(result);
        int iter = 0;//迭代指针,在处理每一个分段的时候都会归零
        while (rtIndex < rtList.size()) {
          //判断本轮已经处理的数据是否会超出分段大小MAX_READ_SIZE的范围,如果超过则结束for循环,如果未超过则进行解码
          if ((iter + mzOffsets.get(rtIndex) + intOffsets.get(rtIndex) + mobiOffsets.get(rtIndex))
              > MAX_READ_SIZE) {
            //分段数据处理完毕, 移动指针至下一个分段的位置
            delta = delta - iter;
            start = start + iter;
            break;
          }

          map.put(rtList.get(rtIndex),
              getSpectrum(result, iter, mzOffsets.get(rtIndex), intOffsets.get(rtIndex),
                  mobiOffsets.get(rtIndex)));
          iter = iter + mzOffsets.get(rtIndex) + intOffsets.get(rtIndex) + mobiOffsets.get(rtIndex);

          rtIndex++;
        }
      }
      raf.seek(start);
      byte[] result = new byte[(int) delta];
      raf.read(result);
      int iter = 0;
      while (rtIndex < rtList.size()) {
        map.put(rtList.get(rtIndex),
            getSpectrum(result, iter, mzOffsets.get(rtIndex), intOffsets.get(rtIndex),
                mobiOffsets.get(rtIndex)));
        iter = iter + mzOffsets.get(rtIndex) + intOffsets.get(rtIndex) + mobiOffsets.get(rtIndex);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ScanException(ResultCodeEnum.BLOCK_PARSE_ERROR);
    }
  }

  public Spectrum getSpectrum(byte[] bytes, int offset, int mzOffset, int intOffset,
      int mobiOffset) {
    if (mzOffset == 0) {
      return new Spectrum(new double[0], new double[0], new double[0]);
    }
    double[] mzArray = getMzs(bytes, offset, mzOffset);
    offset = offset + mzOffset;
    double[] intensityArray = getInts(bytes, offset, intOffset);
    offset = offset + intOffset;
    double[] mobiArray = getMobilities(bytes, offset, mobiOffset);
    return new Spectrum(mzArray, intensityArray, mobiArray);
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
    try {
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
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new double[0];
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
   * get intensity values only for aird file
   *
   * @param value 压缩的数组
   * @return 解压缩后的数组
   */
  public double[] getInts(byte[] value) {
    return getInts(value, 0, value.length);
  }

  /**
   * get intensity values from the start point with a specified length
   *
   * @param value  the original array
   * @param start  the start point
   * @param length the specified length
   * @return the decompression intensity array
   */
  public double[] getInts(byte[] value, int start, int length) {

    ByteBuffer byteBuffer = ByteBuffer.wrap(intByteComp.decode(value, start, length));
    byteBuffer.order(intCompressor.fetchByteOrder());

    IntBuffer ints = byteBuffer.asIntBuffer();
    int[] intValues = new int[ints.capacity()];
    for (int i = 0; i < ints.capacity(); i++) {
      intValues[i] = ints.get(i);
    }
    intValues = intIntComp.decode(intValues);

    double[] intensityValues = new double[intValues.length];
    for (int i = 0; i < intValues.length; i++) {
      double intensity = intValues[i];
      if (intensity < 0) {
        intensity = Math.pow(2, -intensity / 100000d);
      }
      intensityValues[i] = intensity / intPrecision;
    }

    byteBuffer.clear();
    return intensityValues;
  }

  /**
   * get intensity values from the start point with a specified length
   *
   * @param value  the original array
   * @param start  the start point
   * @param length the specified length
   * @return the decompression intensity array
   */
  public double[] getMobilities(byte[] value, int start, int length) {

    try {
      ByteBuffer byteBuffer = ByteBuffer.wrap(mobiByteComp.decode(value, start, length));
      byteBuffer.order(mobiCompressor.fetchByteOrder());

      IntBuffer ints = byteBuffer.asIntBuffer();
      int[] intValues = new int[ints.capacity()];
      for (int i = 0; i < ints.capacity(); i++) {
        intValues[i] = ints.get(i);
      }
      intValues = mobiIntComp.decode(intValues);
      double[] mobilities = new double[intValues.length];
      for (int i = 0; i < intValues.length; i++) {
        mobilities[i] = mobiDict[intValues[i]];
      }
      return mobilities;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * get tag values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value 压缩后的数组
   * @return 解压缩后的数组
   */
  public int[] getTags(byte[] value) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new ZlibWrapper().decode(value));
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
    byte[] tagShift = new ZlibWrapper().decode(value, start, length);
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


  public String getType() {
    return airdInfo == null ? null : airdInfo.getType();
  }

  /**
   * Close the raf object
   */
  public void close() {
    FileUtil.close(raf);
  }
}
