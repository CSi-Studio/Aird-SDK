/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System;
using System.Collections.Generic;
using System.IO;
using AirdSDK.Beans;
using AirdSDK.Beans.Common;
using AirdSDK.Compressor;
using AirdSDK.Enums;
using AirdSDK.Exception;

namespace AirdSDK.Parser;

public abstract class BaseParser
{
    public static int MAX_READ_SIZE = int.MaxValue / 100;

    /**
     * the aird file
     */
    public FileInfo airdFile;

    /**
     * the airdInfo from the index file.
     */
    public AirdInfo airdInfo;

    public FileStream fs;

    /**
     * the aird index file. JSON format
     */
    public FileInfo indexFile;


    /**
     * the intensity compressor
     */
    public Beans.Compressor intCompressor;

    public IntComp intIntComp;
    public ByteComp intByteComp;
    public double intPrecision;

    /**
     * 用于PASEF的压缩内核
     */
    public Beans.Compressor mobiCompressor;

    /**
     * Mobility 字典
     */
    public double[] mobiDict;

    public IntComp mobiIntComp;
    public ByteComp mobiByteComp;
    public double mobiPrecision;

    /**
     * the m/z compressor
     */
    public Beans.Compressor mzCompressor;

    /**
     * 使用的压缩内核
     */
    public SortedIntComp mzIntComp;

    public ByteComp mzByteComp;
    public double mzPrecision;

    public BaseParser()
    {
    }

    public BaseParser(string indexPath)
    {
        indexFile = new FileInfo(indexPath);
        airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
        if (airdInfo == null) throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);

        airdFile = new FileInfo(AirdScanUtil.getAirdPathByIndexPath(indexPath));
        fs = File.OpenRead(airdFile.FullName);

        parseCompsFromAirdInfo();
        parseComboComp();
        parseMobilityDict();
    }

    /**
    * 构造函数
    *
    * @param indexPath 索引文件的位置
    * @throws ScanException 扫描时的异常
    */
    public BaseParser(string indexPath, AirdInfo airdInfo)
    {
        if (airdInfo == null) throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);

        this.airdInfo = airdInfo;
        airdFile = new FileInfo(AirdScanUtil.getAirdPathByIndexPath(indexPath));

        fs = File.OpenRead(airdFile.FullName);

        parseCompsFromAirdInfo();
        parseComboComp();
        parseMobilityDict();
    }

    public BaseParser(string airdPath, Beans.Compressor mzCompressor, Beans.Compressor intCompressor,
        Beans.Compressor mobiCompressor, string airdType)
    {
        indexFile = new FileInfo(AirdScanUtil.getIndexPathByAirdPath(airdPath));

        //不使用Index文件初始化的时候,会直接初始化一个空AirdInfo,用于存放传入的基础信息
        airdInfo = new AirdInfo();
        airdInfo.type = airdType;
        airdFile = new FileInfo(airdPath);
        fs = File.OpenRead(airdFile.FullName);

        if (mzCompressor != null)
        {
            this.mzCompressor = mzCompressor;
            mzPrecision = mzCompressor.precision;
        }

        if (intCompressor != null)
        {
            this.intCompressor = intCompressor;
            intPrecision = intCompressor.precision;
        }

        if (mobiCompressor != null)
        {
            this.mobiCompressor = mobiCompressor;
            mobiPrecision = mobiCompressor.precision;
        }

        parseComboComp();
        parseMobilityDict();
    }

    public static BaseParser buildParser(string indexPath)
    {
        var indexFile = new FileInfo(indexPath);
        return buildParser(indexFile);
    }

    public static BaseParser buildParser(FileInfo indexFile)
    {
        if (indexFile.Exists)
        {
            var airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
            if (airdInfo == null) throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);

            BaseParser baseParser = null;

            switch (airdInfo.type)
            {
                case "DDA_PASEF":
                    baseParser = new DDAPasefParser(indexFile.FullName, airdInfo);
                    break;
                case "DIA_PASEF":
                    baseParser = new DIAPasefParser(indexFile.FullName, airdInfo);
                    break;
                case "DDA":
                    baseParser = new DDAParser(indexFile.FullName, airdInfo);
                    break;
                case "DIA":
                    baseParser = new DIAParser(indexFile.FullName, airdInfo);
                    break;
                case "PRM":
                    baseParser = new PRMParser(indexFile.FullName, airdInfo);
                    break;
                default: throw new System.Exception("Unexpected value: " + airdInfo.type);
            }

            return baseParser;
        }

        throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
    }

    public static Beans.Compressor fetchTargetCompressor(List<Beans.Compressor> compressors, string target)
    {
        if (compressors == null) return null;
        foreach (var compressor in compressors)
            if (compressor.target.Equals(target))
                return compressor;

        return null;
    }

    public void parseCompsFromAirdInfo()
    {
        mzCompressor = fetchTargetCompressor(airdInfo.compressors, Beans.Compressor.TARGET_MZ);
        intCompressor = fetchTargetCompressor(airdInfo.compressors, Beans.Compressor.TARGET_INTENSITY);
        mobiCompressor = fetchTargetCompressor(airdInfo.compressors, Beans.Compressor.TARGET_MOBILITY);
        mzPrecision = mzCompressor.precision;
        intPrecision = intCompressor.precision;
        mobiPrecision = mobiCompressor.precision;
    }

    /**
    * 必须读取索引文件以及Aird二进制文件才可以获取Dict字典
    */
    public void parseMobilityDict()
    {
        var mobiInfo = airdInfo.mobiInfo;
        if ("TIMS".Equals(mobiInfo.type))
        {
            fs.Seek(mobiInfo.dictStart, SeekOrigin.Begin);
            var delta = (int) (mobiInfo.dictEnd - mobiInfo.dictStart);
            var result = new byte[delta];
            fs.Read(result, 0, delta);
            var mobiArray =
                new DeltaWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(result)));
            var mobiDArray = new double[mobiArray.Length];
            for (var i = 0; i < mobiArray.Length; i++) mobiDArray[i] = mobiArray[i] / mobiPrecision;

            mobiDict = mobiDArray;
        }
    }

    public void parseComboComp()
    {
        var mzMethods = mzCompressor.methods;
        if (mzMethods.Count == 2)
        {
            switch (mzMethods[0])
            {
                case "IBP":
                    mzIntComp = new IntegratedBinPackingWrapper(); //IBP
                    break;
                case "IVB":
                    mzIntComp = new IntegratedVarByteWrapper(); //IVB
                    break;
                case "Delta":
                    mzIntComp = new DeltaWrapper(); //Delta
                    break;
            }

            switch (mzMethods[1])
            {
                case "Zlib":
                    mzByteComp = new ZlibWrapper();
                    break;
                case "Brotli":
                    mzByteComp = new BrotliWrapper();
                    break;
                case "Snappy":
                    mzByteComp = new SnappyWrapper();
                    break;
                case "Zstd":
                    mzByteComp = new ZstdWrapper();
                    break;
            }
        }

        var intMethods = intCompressor.methods;
        if (intMethods.Count == 2)
        {
            switch (intMethods[0])
            {
                case "VB":
                    intIntComp = new VarByteWrapper();
                    break;
                case "BP":
                    intIntComp = new BinPackingWrapper();
                    break;
                case "Empty":
                    intIntComp = new Empty();
                    break;
            }

            switch (intMethods[1])
            {
                case "Zlib":
                    intByteComp = new ZlibWrapper();
                    break;
                case "Brotli":
                    intByteComp = new BrotliWrapper();
                    break;
                case "Snappy":
                    intByteComp = new SnappyWrapper();
                    break;
                case "Zstd":
                    intByteComp = new ZstdWrapper();
                    break;
            }
        }

        if (mobiCompressor != null)
        {
            var mobiMethods = mobiCompressor.methods;
            if (mobiMethods.Count == 2)
            {
                switch (mobiMethods[0])
                {
                    case "VB":
                        mobiIntComp = new VarByteWrapper();
                        break;
                    case "BP":
                        mobiIntComp = new BinPackingWrapper();
                        break;
                    case "Empty":
                        mobiIntComp = new Empty();
                        break;
                }

                switch (mobiMethods[1])
                {
                    case "Zlib":
                        mobiByteComp = new ZlibWrapper();
                        break;
                    case "Brotli":
                        mobiByteComp = new BrotliWrapper();
                        break;
                    case "Snappy":
                        mobiByteComp = new SnappyWrapper();
                        break;
                    case "Zstd":
                        mobiByteComp = new ZstdWrapper();
                        break;
                }
            }
        }
    }


    /**
     * 根据位移偏差解析单张光谱图
     */
    public Spectrum getSpectrum(byte[] bytes, int offset, int mzOffset, int intOffset)
    {
        if (mzOffset == 0) return new Spectrum(new double[0], new double[0]);

        double[] mzArray = getMzs(bytes, offset, mzOffset);
        offset = offset + mzOffset;
        double[] intensityArray = getInts(bytes, offset, intOffset);
        return new Spectrum(mzArray, intensityArray);
    }


    public Dictionary<double, Spectrum> getSpectraByRtRange(long startPtr, long endPtr, List<double> rtList,
        List<int> mzOffsets, List<int> intOffsets, double rtStart, double rtEnd)
    {
        //如果范围不在已有的rt数组范围内,则直接返回empty map
        if (rtStart > rtList[rtList.Count - 1] || rtEnd < rtList[0])
        {
            return null;
        }


        int start = rtList.BinarySearch(rtStart);
        if (start < 0)
        {
            start = -start - 1;
        }

        int end = rtList.BinarySearch(rtEnd);
        if (end < 0)
        {
            end = -end - 2;
        }

        return getSpectra(startPtr, endPtr, rtList.GetRange(start, end + 1), mzOffsets, intOffsets);
    }

    public Dictionary<double, Spectrum> getSpectraByRtRange(BlockIndex index, double rtStart, double rtEnd)
    {
        return getSpectraByRtRange(index.startPtr, index.endPtr, index.rts, index.mzs, index.ints, rtStart, rtEnd);
    }

    public Dictionary<double, Spectrum> getSpectra(BlockIndex index)
    {
        return getSpectra(index.startPtr, index.endPtr, index.rts, index.mzs, index.ints);
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
    public Dictionary<double, Spectrum> getSpectra(long start, long end, List<double> rtList, List<int> mzOffsets,
        List<int> intOffsets)
    {
        Dictionary<double, Spectrum> map = new Dictionary<double, Spectrum>();
        fs.Seek(start, SeekOrigin.Begin);
        long delta = end - start;
        byte[] result = new byte[(int) delta];
        fs.Read(result, 0, result.Length);
        int iter = 0;
        for (int i = 0; i < rtList.Count; i++)
        {
            map.Add(rtList[i], getSpectrum(result, iter, mzOffsets[i], intOffsets[i]));
            iter = iter + mzOffsets[i] + intOffsets[i];
        }

        return map;
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
    public Dictionary<double, Spectrum> getSpectra(long start, long end, List<double> rtList, List<int> mzOffsets,
        List<int> intOffsets, List<int> mobiOffsets)
    {
        Dictionary<double, Spectrum> map = new Dictionary<double, Spectrum>();

        //首先计算压缩块的总大小
        long delta = end - start;
        int rtIndex = 0;
        //如果块体积大于整数最大值,则进行分段解析
        while (delta > MAX_READ_SIZE)
        {
            fs.Seek(start, SeekOrigin.Begin); //确认起始点
            byte[] res = new byte[MAX_READ_SIZE]; //读取一个最大块,其中的有效数据应该是小于该块的大小的
            fs.Read(res, 0, res.Length);
            int iterA = 0; //迭代指针,在处理每一个分段的时候都会归零
            while (rtIndex < rtList.Count)
            {
                //判断本轮已经处理的数据是否会超出分段大小MAX_READ_SIZE的范围,如果超过则结束for循环,如果未超过则进行解码
                if ((iterA + mzOffsets[rtIndex] + intOffsets[rtIndex] + mobiOffsets[rtIndex]) > MAX_READ_SIZE)
                {
                    //分段数据处理完毕, 移动指针至下一个分段的位置
                    delta = delta - iterA;
                    start = start + iterA;
                    break;
                }

                map.Add(rtList[rtIndex],
                    getSpectrum(res, iterA, mzOffsets[rtIndex], intOffsets[rtIndex], mobiOffsets[rtIndex]));
                iterA = iterA + mzOffsets[rtIndex] + intOffsets[rtIndex] + mobiOffsets[rtIndex];

                rtIndex++;
            }
        }

        fs.Seek(start, SeekOrigin.Begin);
        byte[] result = new byte[(int) delta];
        fs.Read(result, 0, result.Length);
        int iter = 0;
        while (rtIndex < rtList.Count)
        {
            map.Add(rtList[rtIndex],
                getSpectrum(result, iter, mzOffsets[rtIndex], intOffsets[rtIndex], mobiOffsets[rtIndex]));
            iter = iter + mzOffsets[rtIndex] + intOffsets[rtIndex] + mobiOffsets[rtIndex];
        }

        return map;
    }

    public Spectrum getSpectrum(byte[] bytes, int offset, int mzOffset, int intOffset, int mobiOffset)
    {
        if (mzOffset == 0)
        {
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
  * @param num 所需要搜索的scan number
  * @return
  */
    public Spectrum getSpectrumByNum(int num)
    {
        List<BlockIndex> indexList = airdInfo.indexList;
        foreach (BlockIndex blockIndex in indexList)
        {
            int index = blockIndex.nums.IndexOf(num);
            if (index >= 0)
            {
                return getSpectrumByIndex(blockIndex, index);
            }
        }

        return null;
    }

    /**
     * @param nums 所需要搜索的scan numbers
     * @return
     */
    public Spectrum[] getSpectraByNums(int[] nums)
    {
        List<BlockIndex> indexList = airdInfo.indexList;
        Spectrum[] spectra = new Spectrum[nums.Length];
        for (int i = 0; i < nums.Length; i++)
        {
            if (nums[i] == null)
            {
                spectra[i] = null;
                continue;
            }

            Spectrum spectrum = null;
            foreach (BlockIndex blockIndex in indexList)
            {
                int index = blockIndex.nums.IndexOf(nums[i]);
                if (index >= 0)
                {
                    spectrum = getSpectrumByIndex(blockIndex, index);
                    break;
                }
            }

            spectra[i] = spectrum;
        }

        return spectra;
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
    public Spectrum getSpectrumByRt(BlockIndex index, double rt)
    {
        List<double> rts = index.rts;
        int position = rts.IndexOf(rt);
        return getSpectrumByIndex(index, position);
    }

    /**
    * 从aird文件中获取某一条记录 查询条件: 1.起始坐标 2.全rt列表 3.mz块体积列表 4.intensity块大小列表 5.rt
    * <p>
    * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.rt list
    * 3.mz block size list 4.intensity block size list 5.rt
    *
    * @param startPtr   起始位置 the start point of the target spectrum
    * @param rtList     全部时刻列表 all the retention time list
    * @param mzOffsets  mz数组长度列表 mz size block list
    * @param intOffsets int数组长度列表 intensity size block list
    * @param rt         获取某一个时刻原始谱图 the retention time of the target spectrum
    * @return 某个时刻的光谱信息 the spectrum of the target retention time
    */
    public Spectrum getSpectrumByRt(long startPtr, List<double> rtList, List<int> mzOffsets, List<int> intOffsets,
        double rt)
    {
        int position = rtList.IndexOf(rt);
        return getSpectrumByIndex(startPtr, mzOffsets, intOffsets, position);
    }

    /**
   * 根据序列号查询光谱
   *
   * @param index 索引序列号
   * @return 该索引号对应的光谱信息
   */
    public Spectrum getSpectrum(int index)
    {
        List<BlockIndex> indexList = airdInfo.indexList;
        for (int i = 0; i < indexList.Count; i++)
        {
            BlockIndex blockIndex = indexList[i];
            if (blockIndex.nums.Contains(index))
            {
                int targetIndex = blockIndex.nums.IndexOf(index);
                return getSpectrumByIndex(blockIndex, targetIndex);
            }
        }

        return null;
    }

    /**
     * @param blockIndex 块索引
     * @param index 块内索引值
     * @return 对应光谱数据
     */
    public Spectrum getSpectrumByIndex(BlockIndex blockIndex, int index)
    {
        return getSpectrumByIndex(blockIndex.startPtr, blockIndex.mzs, blockIndex.ints, index);
    }

    /**
     * 从aird文件中获取某一条记录 查询条件: 1.起始坐标 2.mz块体积列表 3.intensity块大小列表 4.光谱在块中的索引位置
     * <p>
     * Read a spectrum from aird with multiple query criteria. Query Criteria: 1.Start Point 2.mz
     * block size list 3.intensity block size list  4.spectrum index in the block
     *
     * @param startPtr   起始位置 the start point of the target spectrum
     * @param mzOffsets  mz数组长度列表 mz size block list
     * @param intOffsets int数组长度列表 intensity size block list
     * @param index      光谱在block块中的索引位置 the spectrum index in the block
     * @return 某个时刻的光谱信息 the spectrum of the target retention time
     */
    public Spectrum getSpectrumByIndex(long startPtr, List<int> mzOffsets, List<int> intOffsets, int index)
    {
        long start = startPtr;

        for (int i = 0; i < index; i++)
        {
            start += mzOffsets[i];
            start += intOffsets[i];
        }

        fs.Seek(start, SeekOrigin.Begin);
        byte[] reader = new byte[mzOffsets[index] + intOffsets[index]];
        fs.Read(reader, 0, reader.Length);
        return getSpectrum(reader, 0, mzOffsets[index], intOffsets[index]);
    }

    /**
    * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
    *
    * @param value 压缩后的数组
    * @return 解压缩后的数组
    */
    public double[] getMzs(byte[] value)
    {
        return getMzs(value, 0, value.Length);
    }

    /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value  压缩后的数组
   * @param offset 起始位置
   * @param length 读取长度
   * @return 解压缩后的数组
   */
    public double[] getMzs(byte[] value, int offset, int length)
    {
        byte[] decodedData = mzByteComp.decode(value, offset, length);
        int[] intValues = ByteTrans.byteToInt(decodedData);
        intValues = mzIntComp.decode(intValues);
        double[] doubleValues = new double[intValues.Length];
        for (int index = 0; index < intValues.Length; index++)
        {
            doubleValues[index] = intValues[index] / mzPrecision;
        }

        return doubleValues;
    }

    /**
    * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN
    *
    * @param value 加密的数组
    * @return 解压缩后的数组
    */
    public int[] getMzsAsInteger(byte[] value)
    {
        return getMzsAsInteger(value, 0, value.Length);
    }

    /**
   * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value  压缩后的数组
   * @param offset 起始位置
   * @param length 读取长度
   * @return 解压缩后的数组
   */
    public int[] getMzsAsInteger(byte[] value, int offset, int length)
    {
        byte[] decodedData = mzByteComp.decode(value, offset, length);
        int[] intValues = ByteTrans.byteToInt(decodedData);
        intValues = mzIntComp.decode(intValues);
        return intValues;
    }

    /**
    * get intensity values only for aird file
    *
    * @param value 压缩的数组
    * @return 解压缩后的数组
    */
    public double[] getInts(byte[] value)
    {
        return getInts(value, 0, value.Length);
    }

    /**
    * get intensity values from the start point with a specified length
    *
    * @param value  the original array
    * @param start  the start point
    * @param length the specified length
    * @return the decompression intensity array
    */
    public double[] getInts(byte[] value, int start, int length)
    {
        byte[] decodedData = intByteComp.decode(value, start, length);
        int[] intValues = ByteTrans.byteToInt(decodedData);
        intValues = intIntComp.decode(intValues);

        double[] intensityValues = new double[intValues.Length];
        for (int i = 0; i < intValues.Length; i++)
        {
            double intensity = intValues[i];
            if (intensity < 0)
            {
                intensity = Math.Pow(2, -intensity / 100000d);
            }

            intensityValues[i] = intensity / intPrecision;
        }

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
    public double[] getMobilities(byte[] value, int start, int length)
    {
        byte[] decodedData = mobiByteComp.decode(value, start, length);
        int[] intValues = ByteTrans.byteToInt(decodedData);
        intValues = mobiIntComp.decode(intValues);
        double[] mobilities = new double[intValues.Length];
        for (int i = 0; i < intValues.Length; i++)
        {
            mobilities[i] = mobiDict[intValues[i]];
        }

        return mobilities;
    }

    /**
   * get tag values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
   *
   * @param value 压缩后的数组
   * @return 解压缩后的数组
   */
    public int[] getTags(byte[] value)
    {
        byte[] decodedData = new ZlibWrapper().decode(value);
        byte[] byteValue = new byte[decodedData.Length * 8];
        for (int i = 0; i < decodedData.Length; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                byteValue[8 * i + j] = (byte) (((decodedData[i] & 0xff) >> j) & 1);
            }
        }

        int digit = mzCompressor.digit;
        int[] tags = new int[byteValue.Length / digit];
        for (int i = 0; i < tags.Length; i++)
        {
            for (int j = 0; j < digit; j++)
            {
                tags[i] += byteValue[digit * i + j] << j;
            }
        }

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
    public int[] getTags(byte[] value, int start, int length)
    {
        byte[] tagShift = new ZlibWrapper().decode(value, start, length);
        //        byteBuffer.order(mzCompressor.getByteOrder());

        byte[] byteValue = new byte[tagShift.Length * 8];
        for (int i = 0; i < tagShift.Length; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                byteValue[8 * i + j] = (byte) (((tagShift[i] & 0xff) >> j) & 1);
            }
        }

        int digit = mzCompressor.digit;
        int[] tags = new int[byteValue.Length / digit];
        for (int i = 0; i < tags.Length; i++)
        {
            for (int j = 0; j < digit; j++)
            {
                tags[i] += byteValue[digit * i + j] << j;
            }
        }

        return tags;
    }

    public String getType()
    {
        return airdInfo == null ? null : airdInfo.type;
    }
}