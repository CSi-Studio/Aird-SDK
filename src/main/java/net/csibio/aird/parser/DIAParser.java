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

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.bean.SpectrumDetail;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.exception.ScanException;

/**
 * DIA/SWATH模式的转换器 DIA Parser will convert the original order which is ordered by retention time to
 * a precursor m/z grouped block. spectras will be grouped by Precursor m/z range
 */
public class DIAParser extends BaseParser {

  /**
   * 构造函数
   *
   * @param indexFilePath index file path
   * @throws ScanException scan exception
   */
  public DIAParser(String indexFilePath) throws ScanException {
    super(indexFilePath);
  }

  public DIAParser(String indexFilePath, AirdInfo airdInfo) throws ScanException {
    super(indexFilePath, airdInfo);
  }

  /**
   * 构造函数
   *
   * @param airdPath      aird file path
   * @param mzCompressor  mz compressor
   * @param intCompressor intensity compressor
   * @param mzPrecision   mz precision
   * @throws ScanException scan exception
   */
  public DIAParser(String airdPath, Compressor mzCompressor, Compressor intCompressor,
      int mzPrecision) throws ScanException {
    super(airdPath, mzCompressor, intCompressor, mzPrecision, AirdType.DIA_SWATH.getName());
  }

  /**
   * @param index block index
   * @return all the spectrums of one block
   */
  public TreeMap<Float, MzIntensityPairs> getSpectrums(BlockIndex index) {
    if (mzCompressor.getMethods().contains(Compressor.METHOD_STACK)) {
      return getSpectrums(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(),
          index.getTags(), index.getInts());
    } else {
      return getSpectrums(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(),
          index.getInts());
    }
  }

  /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
   * <p>
   * the result key is rt,value is the spectrum(mz-intensity pairs) In particular, this function
   * will not close the RAF object directly after using it. Users need to close the object manually
   * after using the diaparser object
   *
   * @param startPtr    起始指针位置 start point
   * @param endPtr      结束指针位置 end point
   * @param rtList      rt列表,包含所有的光谱产出时刻 the retention time list
   * @param mzSizeList  mz块的大小列表 the mz block size list
   * @param intSizeList intensity块的大小列表 the intensity block size list
   * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
   */
  public TreeMap<Float, MzIntensityPairs> getSpectrums(long startPtr, long endPtr,
      List<Float> rtList, List<Long> mzSizeList, List<Long> intSizeList) {

    try {
      TreeMap<Float, MzIntensityPairs> map = new TreeMap<>();

      raf.seek(startPtr);
      long delta = endPtr - startPtr;
      byte[] result = new byte[(int) delta];
      raf.read(result);
      assert rtList.size() == mzSizeList.size();
      assert mzSizeList.size() == intSizeList.size();

      int start = 0;
      for (int i = 0; i < rtList.size(); i++) {
        float[] intensityArray = null;
        double[] mzArray = getMzValues(result, start, mzSizeList.get(i).intValue());
        start = start + mzSizeList.get(i).intValue();
        if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
          intensityArray = getLogedIntValues(result, start, intSizeList.get(i).intValue());
        } else {
          intensityArray = getIntValues(result, start, intSizeList.get(i).intValue());
        }
        start = start + intSizeList.get(i).intValue();
        map.put(rtList.get(i), new MzIntensityPairs(mzArray, intensityArray));
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
   * <p>
   * the result key is rt,value is the spectrum(mz-intensity pairs) In particular, this function
   * will not close the RAF object directly after using it. Users need to close the object manually
   * after using the diaparser object
   *
   * @param startPtr    起始指针位置 start point
   * @param endPtr      结束指针位置 end point
   * @param rtList      rt列表,包含所有的光谱产出时刻 the retention time list
   * @param mzSizeList  mz块的大小列表 the mz block size list
   * @param tagSizeList tag块的大小列表 the tag block size list
   * @param intSizeList intensity块的大小列表 the intensity block size list
   * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
   */
  public TreeMap<Float, MzIntensityPairs> getSpectrums(long startPtr, long endPtr,
      List<Float> rtList, List<Long> mzSizeList, List<Long> tagSizeList, List<Long> intSizeList) {

    try {
      TreeMap<Float, MzIntensityPairs> map = new TreeMap<>();

      raf.seek(startPtr);
      long delta = endPtr - startPtr;
      byte[] result = new byte[(int) delta];
      raf.read(result);
      assert tagSizeList.size() == mzSizeList.size();
      assert mzSizeList.size() == intSizeList.size();

      int start = 0;
      int maxTag = (int) Math.pow(2, mzCompressor.getDigit());
      for (int i = 0; i < mzSizeList.size(); i++) {
        double[] mzArray = getMzValues(result, start, mzSizeList.get(i).intValue());
        start = start + mzSizeList.get(i).intValue();

        int[] tagArray = getTags(result, start, tagSizeList.get(i).intValue());
        start += tagSizeList.get(i).intValue();

        Map<Integer, Integer> tagMap = new HashMap<Integer, Integer>();
        for (int tag : tagArray) {
          tagMap.merge(tag, 1, Integer::sum);
        }

        List<double[]> mzGroup = new ArrayList<>();
        int layerNum = tagMap.keySet().size();
        for (int j = 0; j < layerNum; j++) {
          mzGroup.add(new double[tagMap.get(j)]);
        }
        int[] p = new int[layerNum];
        for (int j = 0; j < tagArray.length; j++) {
          mzGroup.get(tagArray[j])[p[tagArray[j]]++] = mzArray[j];
        }

        float[] intensityArray = null;
        if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
          intensityArray = getLogedIntValues(result, start, intSizeList.get(i).intValue());
        } else {
          intensityArray = getIntValues(result, start, intSizeList.get(i).intValue());
        }
        start = start + intSizeList.get(i).intValue();
        List<float[]> intensityGroup = new ArrayList<>();
        int initFlag = 0;
        for (int j = 0; j < layerNum; j++) {
          intensityGroup.add(
              Arrays.copyOfRange(intensityArray, initFlag, initFlag + tagMap.get(j)));
          initFlag += tagMap.get(j);
        }

        for (int j = 0; j < layerNum; j++) {
          map.put(rtList.get(i * maxTag + j),
              new MzIntensityPairs(mzGroup.get(j), intensityGroup.get(j)));
        }

      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 从aird文件中获取某一条记录 查询条件: 1.起始坐标 2.全rt列表 3.mz块体积列表 4.intensity块大小列表 5.rt
   * <p>
   * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.rt list
   * 3.mz block size list 4.intensity block size list 5.rt
   *
   * @param startPtr    起始位置 the start point of the target spectrum
   * @param rtList      全部时刻列表 all the retention time list
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param rt          获取某一个时刻原始谱图 the retention time of the target spectrum
   * @return 某个时刻的光谱信息 the spectrum of the target retention time
   */
  public MzIntensityPairs getSpectrumByRt(long startPtr, List<Float> rtList, List<Long> mzSizeList,
      List<Long> intSizeList, float rt) {
    int position = rtList.indexOf(rt);
    return getSpectrumByIndex(startPtr, mzSizeList, intSizeList, position);
  }

  /**
   * 从aird文件中获取某一条记录 查询条件: 1.起始坐标 2.全rt列表 3.mz块体积列表 4.tag块大小列表 5.intensity块大小列表 6.rt
   * <p>
   * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.rt list
   * 3.mz block size list 4.intensity block size list 5.rt
   *
   * @param startPtr    起始位置 the start point of the target spectrum
   * @param rtList      全部时刻列表 all the retention time list
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param tagSizeList tag数组长度列表 tag size block list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param rt          获取某一个时刻原始谱图 the retention time of the target spectrum
   * @return 某个时刻的光谱信息 the spectrum of the target retention time
   */
  public MzIntensityPairs getSpectrumByRt(long startPtr, List<Float> rtList, List<Long> mzSizeList,
      List<Long> tagSizeList, List<Long> intSizeList, float rt) {
    int position = rtList.indexOf(rt);
    return getSpectrumByIndex(startPtr, mzSizeList, tagSizeList, intSizeList, position);
  }


  /**
   * 从一个完整的Swath Block块中取出一条记录 查询条件: 1. block索引号 2. rt
   * <p>
   * Read a spectrum from aird with block index and target rt
   *
   * @param index block index
   * @param rt    retention time of the target spectrum
   * @return the target spectrum
   */
  public MzIntensityPairs getSpectrumByRt(BlockIndex index, float rt) {
    List<Float> rts = index.getRts();
    int position = rts.indexOf(rt);
    return getSpectrumByIndex(index, position);
  }

  /**
   * 从一个完整的Swath Block块中取出一条记录 查询条件: 1. block索引号 2. rt
   * <p>
   * Read a spectrum from aird with block index and target rt
   *
   * @param startPtr    block start position
   * @param rts         rt list
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param rtStart     start retention time of the target spectrum
   * @param rtEnd       ended retention time of the target spectrum
   * @return the target spectrum
   */
  public TreeMap<Float, MzIntensityPairs> getSpectrumsByRtRange(Long startPtr, List<Float> rts,
      List<Long> mzSizeList, List<Long> intSizeList, float rtStart, float rtEnd) {
    float[] rtArray = new float[rts.size()];
    for (int i = 0; i < rts.size(); i++) {
      rtArray[i] = rts.get(i);
    }
    int startIndex = Extractor.lowerBound(rtArray, rtStart);
    int endIndex = Extractor.lowerBound(rtArray, rtEnd);

    return getSpectrumByIndexRange(startPtr, rts, mzSizeList, intSizeList, startIndex, endIndex);
  }

  /**
   * 从Aird文件中获取一个范围内的光谱记录 Read spectrum map from aird with target index range
   *
   * @param startPtr    起始位置 the start point of the target spectrum
   * @param rts         所有的rt the rt list
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param indexStart  the start index(including itself)
   * @param indexEnd    the end index(including itself)
   * @return the target spectrum map
   */
  public TreeMap<Float, MzIntensityPairs> getSpectrumByIndexRange(long startPtr, List<Float> rts,
      List<Long> mzSizeList, List<Long> intSizeList, int indexStart, int indexEnd) {
    long start = startPtr;
    for (int i = 0; i < indexStart; i++) {
      start += mzSizeList.get(i);
      start += intSizeList.get(i);
    }

    long end = start;
    for (int j = indexStart; j < indexEnd; j++) {
      end += mzSizeList.get(j);
      end += intSizeList.get(j);
    }

    return getSpectrums(start, end, rts.subList(indexStart, indexEnd),
        mzSizeList.subList(indexStart, indexEnd), intSizeList.subList(indexStart, indexEnd));
  }

  /**
   * 从aird文件中获取某一条记录 查询条件: 1.起始坐标 2.mz块体积列表 3.intensity块大小列表 4.光谱在块中的索引位置
   * <p>
   * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.mz
   * block size list 3.intensity block size list  4.spectrum index in the block
   *
   * @param startPtr    起始位置 the start point of the target spectrum
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param index       光谱在block块中的索引位置 the spectrum index in the block
   * @return 某个时刻的光谱信息 the spectrum of the target retention time
   */
  public MzIntensityPairs getSpectrumByIndex(long startPtr, List<Long> mzSizeList,
      List<Long> intSizeList, int index) {
    try {
      raf = new RandomAccessFile(airdFile, "r");
      long start = startPtr;

      for (int i = 0; i < index; i++) {
        start += mzSizeList.get(i);
        start += intSizeList.get(i);
      }

      raf.seek(start);
      byte[] reader = new byte[mzSizeList.get(index).intValue()];
      raf.read(reader);
      double[] mzArray = getMzValues(reader);
      start += mzSizeList.get(index).intValue();
      raf.seek(start);
      reader = new byte[intSizeList.get(index).intValue()];
      raf.read(reader);

      float[] intensityArray = null;
      if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
        intensityArray = getLogedIntValues(reader);
      } else {
        intensityArray = getIntValues(reader);
      }

      return new MzIntensityPairs(mzArray, intensityArray);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * 从aird文件中获取某一条记录,同时返回它的原始二进制数组 查询条件: 1.起始坐标 2.mz块体积列表 3.intensity块大小列表 4.光谱在块中的索引位置
   * <p>
   * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.mz
   * block size list 3.intensity block size list  4.spectrum index in the block
   *
   * @param startPtr    起始位置 the start point of the target spectrum
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param index       光谱在block块中的索引位置 the spectrum index in the block
   * @return 某个时刻的光谱信息 the spectrum of the target retention time, The spectrum contains the original
   * bytes array of mz and intensity
   */
  public SpectrumDetail getSpectrumDetailByIndex(long startPtr, List<Long> mzSizeList,
      List<Long> intSizeList, int index) {
    SpectrumDetail detail = new SpectrumDetail();
    try {
      raf = new RandomAccessFile(airdFile, "r");
      long start = startPtr;

      for (int i = 0; i < index; i++) {
        start += mzSizeList.get(i);
        start += intSizeList.get(i);
      }

      raf.seek(start);
      byte[] reader = new byte[mzSizeList.get(index).intValue()];
      raf.read(reader);
      detail.setMzBytes(reader.clone());
      double[] mzArray = getMzValues(reader);
      start += mzSizeList.get(index).intValue();
      raf.seek(start);
      reader = new byte[intSizeList.get(index).intValue()];
      raf.read(reader);
      detail.setIntensityBytes(reader.clone());
      float[] intensityArray = null;
      if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
        intensityArray = getLogedIntValues(reader);
      } else {
        intensityArray = getIntValues(reader);
      }

      detail.setMzs(mzArray);
      detail.setIntensities(intensityArray);
      return detail;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * 从aird文件中获取某一条记录 查询条件: 1.起始坐标 2.mz块体积列表 3.tag块大小列表 4.intensity块大小列表 5.光谱在块中的索引位置
   * <p>
   * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.mz
   * block size list 3.intensity block size list  4.spectrum index in the block
   *
   * @param startPtr    起始位置 the start point of the target spectrum
   * @param mzSizeList  mz数组长度列表 mz size block list
   * @param tagSizeList tag块的大小列表 the tag block size list
   * @param intSizeList int数组长度列表 intensity size block list
   * @param index       光谱在block块中的索引位置 the spectrum index in the block
   * @return 某个时刻的光谱信息 the spectrum of the target retention time
   */
  public MzIntensityPairs getSpectrumByIndex(long startPtr, List<Long> mzSizeList,
      List<Long> tagSizeList, List<Long> intSizeList, int index) {
    try {
      raf = new RandomAccessFile(airdFile, "r");
      long start = startPtr;

      int maxTag = (int) Math.pow(2, mzCompressor.getDigit());
      int mzIndex = index / maxTag;

      for (int i = 0; i < mzIndex; i++) {
        start += mzSizeList.get(i);
        start += tagSizeList.get(i);
        start += intSizeList.get(i);
      }

      raf.seek(start);
      byte[] reader = new byte[mzSizeList.get(mzIndex).intValue()];
      raf.read(reader);
      double[] mzArray = getMzValues(reader);
      start += mzSizeList.get(mzIndex).intValue();

      raf.seek(start);
      reader = new byte[tagSizeList.get(mzIndex).intValue()];
      raf.read(reader);
      int[] tagArray = getTags(reader);
      start += tagSizeList.get(mzIndex).intValue();

      raf.seek(start);
      reader = new byte[intSizeList.get(mzIndex).intValue()];
      raf.read(reader);
      float[] intensityArray = null;
      if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
        intensityArray = getLogedIntValues(reader);
      } else {
        intensityArray = getIntValues(reader);
      }

      Map<Integer, Integer> tagMap = new HashMap<Integer, Integer>();
      for (int tag : tagArray) {
        tagMap.merge(tag, 1, Integer::sum);
      }

      List<double[]> mzGroup = new ArrayList<>();
      int layerNum = tagMap.keySet().size();
      for (int j = 0; j < layerNum; j++) {
        mzGroup.add(new double[tagMap.get(j)]);
      }
      int[] p = new int[layerNum];
      for (int j = 0; j < tagArray.length; j++) {
        mzGroup.get(tagArray[j])[p[tagArray[j]]++] = mzArray[j];
      }

      List<float[]> intensityGroup = new ArrayList<>();
      int initFlag = 0;
      for (int j = 0; j < layerNum; j++) {
        intensityGroup.add(Arrays.copyOfRange(intensityArray, initFlag, initFlag + tagMap.get(j)));
        initFlag += tagMap.get(j);
      }

      int remainder = index - mzIndex * maxTag;
      return new MzIntensityPairs(mzGroup.get(remainder), intensityGroup.get(remainder));

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * @param blockIndex 块索引
   * @param index      块内索引值
   * @return 对应光谱数据
   */
  public MzIntensityPairs getSpectrumByIndex(BlockIndex blockIndex, int index) {
    if (mzCompressor.getMethods().contains(Compressor.METHOD_STACK)) {
      return getSpectrumByIndex(blockIndex.getStartPtr(), blockIndex.getMzs(), blockIndex.getTags(),
          blockIndex.getInts(), index);
    } else {
      return getSpectrumByIndex(blockIndex.getStartPtr(), blockIndex.getMzs(), blockIndex.getInts(),
          index);
    }
  }

  /**
   * 根据序列号查询光谱
   *
   * @param index 索引序列号
   * @return 该索引号对应的光谱信息
   */
  public MzIntensityPairs getSpectrum(int index) {
    List<BlockIndex> indexList = getAirdInfo().getIndexList();
    for (int i = 0; i < indexList.size(); i++) {
      BlockIndex blockIndex = indexList.get(i);
      if (blockIndex.getNums().contains(index)) {
        int targetIndex = blockIndex.getNums().indexOf(index);
        return getSpectrumByIndex(blockIndex, targetIndex);
      }
    }
    return null;
  }

  /**
   * Specific API 从Aird文件中读取,但是不要将m/z数组的从Integer改为Float
   * <p>
   * Read from aird, but not convert the m/z array from integer to float
   *
   * @param index      索引序列号 block index
   * @param rt         光谱产出时刻 retention time for the spectrum
   * @param compressor 压缩方式 compression method 0: ZDPD 1: S-ZDPD, default compressor = 0
   * @return 该时刻产生的光谱信息, 其中mz数据以int类型返回 the target rt's spectrum with integer mz array
   */
  public MzIntensityPairs getSpectrumAsInteger(BlockIndex index, float rt, int compressor) {
    if (compressor == 0) {
      return getSpectrumAsInteger(index, rt);
    } else if (compressor == 1) {

      List<Float> rts = index.getRts();
      int position = rts.indexOf(rt);
      MzIntensityPairs mzIntensityPairs = getSpectrumByIndex(index, position);
      int[] mzArray = new int[mzIntensityPairs.getMzArray().length];
      for (int i = 0; i < mzArray.length; i++) {
        mzArray[i] = (int) (mzIntensityPairs.getMzArray()[i] * mzPrecision);
      }
      return new MzIntensityPairs(mzArray, mzIntensityPairs.getIntensityArray());

    } else {
      System.out.println("No such compressor.");
    }

    return null;
  }

  /**
   * get the spectrum which contains the mz data as integers
   *
   * @param index block index
   * @param rt    retention time
   * @return the target spectrum
   */
  public MzIntensityPairs getSpectrumAsInteger(BlockIndex index, float rt) {
    try {
      raf = new RandomAccessFile(airdFile, "r");
      List<Float> rts = index.getRts();
      int position = rts.indexOf(rt);

      long start = index.getStartPtr();

      for (int i = 0; i < position; i++) {
        start += index.getMzs().get(i);
        start += index.getInts().get(i);
      }

      raf.seek(start);
      byte[] reader = new byte[index.getMzs().get(position).intValue()];
      raf.read(reader);
      int[] mzArray = getMzValuesAsInteger(reader);
      start += index.getMzs().get(position).intValue();
      raf.seek(start);
      reader = new byte[index.getInts().get(position).intValue()];
      raf.read(reader);

      float[] intensityArray = null;
      if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
        intensityArray = getLogedIntValues(reader);
      } else {
        intensityArray = getIntValues(reader);
      }

      return new MzIntensityPairs(mzArray, intensityArray);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
