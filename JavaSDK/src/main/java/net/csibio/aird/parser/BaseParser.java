/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MobiInfo;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.*;
import net.csibio.aird.compressor.intcomp.BinPackingWrapper;
import net.csibio.aird.compressor.intcomp.Empty;
import net.csibio.aird.compressor.intcomp.IntComp;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.DeltaWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.SortedIntComp;
import net.csibio.aird.enums.*;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdScanUtil;
import net.csibio.aird.util.ArrayUtil;
import net.csibio.aird.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Base Parser
 */
@Data
public abstract class BaseParser {

    /**
     * Max Read Pool Size
     */
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

    /**
     * mz precision
     */
    public double mzPrecision;

    /**
     * intensity precision
     */
    public double intPrecision;

    /**
     * mobility precision
     */
    public double mobiPrecision;

    /**
     * integer compressor for mz
     */
    public SortedIntComp mzIntComp;

    /**
     * byte compressor for mz
     */
    public ByteComp mzByteComp;

    /**
     * integer compressor for intensity
     */
    public IntComp intIntComp;

    /**
     * byte compressor for intensity
     */
    public ByteComp intByteComp;

    /**
     * integer compressor for mobility
     */
    public IntComp mobiIntComp;

    /**
     * byte compressor for mobility
     */
    public ByteComp mobiByteComp;

    /**
     * Random Access File reader
     */
    public RandomAccessFile raf;

    /**
     * Mobility 字典
     */
    public double[] mobiDict;

    public SortedIntComp rtIntComp4Chroma = new IntegratedVarByteWrapper();
    public ByteComp rtByteComp4Chroma = new ZstdWrapper();
    public IntComp intIntComp4Chroma = new VarByteWrapper();
    public ByteComp intByteComp4Chroma = new ZstdWrapper();

    /**
     * 构造函数
     */
    public BaseParser() {
    }

    /**
     * 构造函数
     *
     * @param indexPath 索引文件的位置
     * @throws Exception 扫描时的异常
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
        parseIndexList();
        parseCompsFromAirdInfo();
        parseComboComp();
        parseMobilityDict();
    }

    /**
     * 构造函数
     *
     * @param indexPath 索引文件的位置
     * @throws Exception 扫描时的异常
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
        parseIndexList();
        parseCompsFromAirdInfo();
        parseComboComp();
        parseMobilityDict();
    }

    public void parseIndexList() throws IOException {
        if (airdInfo != null && airdInfo.getIndexList() == null) {
            var delta = (int)(airdInfo.getIndexEndPtr() - airdInfo.getIndexStartPtr());
            if (delta > 0)
            {
                raf.seek(airdInfo.getIndexStartPtr());
                byte[] result = new byte[delta];
                raf.read(result);
                byte[] indexListBytes = new ZstdWrapper().decode(result);
                String indexListStr = new String(indexListBytes);
                List<BlockIndex> indexList = JSONArray.parseArray(indexListStr, BlockIndex.class);
                airdInfo.setIndexList(indexList);
            }
        }
    }

    /**
     * 使用不读取Index文件,直接使用关键信息进行初始化的方法
     *
     * @param airdPath       Aird文件路径
     * @param mzCompressor   mz压缩策略
     * @param intCompressor  intensity压缩策略
     * @param mobiCompressor mobility压缩策略
     * @param airdType       aird类型
     * @throws Exception 扫描异常
     */
    public BaseParser(String airdPath, Compressor mzCompressor, Compressor intCompressor, Compressor mobiCompressor, String airdType) throws Exception {
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

        if (mzCompressor != null) {
            this.mzCompressor = mzCompressor;
            this.mzPrecision = mzCompressor.getPrecision();
        }

        if (intCompressor != null) {
            this.intCompressor = intCompressor;
            this.intPrecision = intCompressor.getPrecision();
        }

        if (mobiCompressor != null) {
            this.mobiCompressor = mobiCompressor;
            this.mobiPrecision = mobiCompressor.getPrecision();
        }
        parseIndexList();
        parseComboComp();
        parseMobilityDict();
    }

    /**
     * build parser function
     *
     * @param indexPath index file path
     * @return the base parser
     * @throws Exception exception
     */
    public static BaseParser buildParser(String indexPath) throws Exception {
        File indexFile = new File(indexPath);
        return buildParser(indexFile);
    }

    /**
     * 最基础的启动方法:使用Index文件扫描AirdInfo以后读取Aird文件,然后根据AirdInfo中的文件类型分别初始化不同的Parser
     *
     * @param indexFile index file
     * @return Base parser instance
     * @throws Exception exception
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
                default -> throw new IllegalStateException("Unexpected value: " + AirdType.getType(airdInfo.getType()));
            };
        }
        throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
    }

    /**
     * get the target compressor
     *
     * @param compressors all the compressors
     * @param target      the target dimension
     * @return the target compressor by target dimension name
     */
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
     * @throws IOException parse exception
     */
    public void parseMobilityDict() throws IOException {
        MobiInfo mobiInfo = airdInfo.getMobiInfo();
        if ("TIMS".equals(mobiInfo.getType())) {
            raf.seek(mobiInfo.getDictStart());
            long delta = mobiInfo.getDictEnd() - mobiInfo.getDictStart();
            byte[] result = new byte[(int) delta];
            raf.read(result);
            int[] mobiArray = new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(result)));
            double[] mobiDArray = new double[mobiArray.length];
            for (int i = 0; i < mobiArray.length; i++) {
                mobiDArray[i] = mobiArray[i] / getMobiPrecision();
            }
            this.mobiDict = mobiDArray;
        }
    }

    /**
     * parse Compressor information from the airdInfo
     */
    public void parseCompsFromAirdInfo() {
        mzCompressor = fetchTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MZ);
        intCompressor = fetchTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_INTENSITY);
        mobiCompressor = fetchTargetCompressor(airdInfo.getCompressors(), Compressor.TARGET_MOBILITY);
        mzPrecision = mzCompressor.getPrecision();
        intPrecision = intCompressor.getPrecision();
        mobiPrecision = mobiCompressor.getPrecision();
    }

    /**
     * parse Compressor information from the airdInfo
     *
     * @throws Exception exceptions
     */
    public void parseComboComp() throws Exception {
        List<String> mzMethods = mzCompressor.getMethods();
        if (mzMethods.size() == 2) {
            switch (SortedIntCompType.getByName(mzMethods.get(0))) {
                case IBP -> mzIntComp = new IntegratedBinPackingWrapper();
                case IVB -> mzIntComp = new IntegratedVarByteWrapper();
                case Delta -> mzIntComp = new DeltaWrapper();
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
     * @param bytes     the bytes to be searched
     * @param offset    the offset to be searched
     * @param mzOffset  the offset to be searched for mz
     * @param intOffset the offset to be searched for int
     * @return the searched spectrum
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
     * @param num 所需要搜索的scan number
     * @return the target spectrum
     */
    public Spectrum getSpectrumByNum(Integer num) {
        List<BlockIndex> indexList = airdInfo.getIndexList();
        for (BlockIndex blockIndex : indexList) {
            int index = blockIndex.getNums().indexOf(num);
            if (index >= 0) {
                return getSpectrumByIndex(blockIndex, index);
            }
        }
        return null;
    }

    /**
     * @param nums 所需要搜索的scan numbers
     * @return the target spectra
     */
    public Spectrum[] getSpectraByNums(Integer... nums) {
        List<BlockIndex> indexList = airdInfo.getIndexList();
        Spectrum[] spectra = new Spectrum[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == null) {
                spectra[i] = null;
                continue;
            }
            Spectrum spectrum = null;
            for (BlockIndex blockIndex : indexList) {
                int index = blockIndex.getNums().indexOf(nums[i]);
                if (index >= 0) {
                    spectrum = getSpectrumByIndex(blockIndex, index);
                    break;
                }
            }
            spectra[i] = spectrum;
        }
        return spectra;
    }

    /**
     * 根据RT范围解码光谱图
     *
     * @param startPtr   起始位置 the start point of the target spectrum
     * @param endPtr     结束位置 The end point of the target spectrum
     * @param rtList     the list of search retention time
     * @param mzOffsets  mz数组长度列表 mz size block list
     * @param intOffsets int数组长度列表 intensity size block list
     * @param rtStart    the start of retention time
     * @param rtEnd      the end of retention time
     * @return spectrum map for the search result
     */
    public TreeMap<Double, Spectrum> getSpectraByRtRange(long startPtr, long endPtr, List<Double> rtList, List<Integer> mzOffsets, List<Integer> intOffsets, double rtStart, double rtEnd) {
        double[] rts = ArrayUtil.toDoublePrimitive(rtList);
        //如果范围不在已有的rt数组范围内,则直接返回empty map
        if (rtStart > rts[rts.length - 1] || rtEnd < rts[0]) {
            return null;
        }

        int start = Arrays.binarySearch(rts, rtStart);
        if (start < 0) {
            start = -start - 1;
        }
        int end = Arrays.binarySearch(rts, rtEnd);
        if (end < 0) {
            end = -end - 2;
        }
        return getSpectra(startPtr, endPtr, rtList.subList(start, end + 1), mzOffsets, intOffsets);
    }

    /**
     * 根据RT范围解码光谱图
     *
     * @param index   the index of the target block
     * @param rtStart the start of the retention time
     * @param rtEnd   tje end of the retention time
     * @return spectrum map for the search result
     */
    public TreeMap<Double, Spectrum> getSpectraByRtRange(BlockIndex index, double rtStart, double rtEnd) {
        return getSpectraByRtRange(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(), index.getInts(), rtStart, rtEnd);
    }

    /**
     * 根据索引解码整个索引块内所有的光谱图
     *
     * @param index the index of the target block
     * @return spectrum map for the search result
     */
    public TreeMap<Double, Spectrum> getSpectra(BlockIndex index) {
        return getSpectra(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(), index.getInts());
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
    public TreeMap<Double, Spectrum> getSpectra(long start, long end, List<Double> rtList, List<Integer> mzOffsets, List<Integer> intOffsets) {

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
     * @param mobiOffsets mobiOffsets块的大小列表 the mobility block size list
     * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
     */
    public TreeMap<Double, Spectrum> getSpectra(long start, long end, List<Double> rtList, List<Integer> mzOffsets, List<Integer> intOffsets, List<Integer> mobiOffsets) {

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
                    if ((iter + mzOffsets.get(rtIndex) + intOffsets.get(rtIndex) + mobiOffsets.get(rtIndex)) > MAX_READ_SIZE) {
                        //分段数据处理完毕, 移动指针至下一个分段的位置
                        delta = delta - iter;
                        start = start + iter;
                        break;
                    }

                    map.put(rtList.get(rtIndex), getSpectrum(result, iter, mzOffsets.get(rtIndex), intOffsets.get(rtIndex), mobiOffsets.get(rtIndex)));
                    iter = iter + mzOffsets.get(rtIndex) + intOffsets.get(rtIndex) + mobiOffsets.get(rtIndex);

                    rtIndex++;
                }
            }
            raf.seek(start);
            byte[] result = new byte[(int) delta];
            raf.read(result);
            int iter = 0;
            while (rtIndex < rtList.size()) {
                map.put(rtList.get(rtIndex), getSpectrum(result, iter, mzOffsets.get(rtIndex), intOffsets.get(rtIndex), mobiOffsets.get(rtIndex)));
                iter = iter + mzOffsets.get(rtIndex) + intOffsets.get(rtIndex) + mobiOffsets.get(rtIndex);
                rtIndex++;
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ScanException(ResultCodeEnum.BLOCK_PARSE_ERROR);
        }
    }

    /**
     * 根据目标字节块与偏移参数获取单张光谱
     *
     * @param bytes      the bytes of the target block bytes
     * @param offset     the offset for the reading block
     * @param mzOffset   the offset for mz bytes
     * @param intOffset  the offset for int bytes
     * @param mobiOffset the offset for mobility
     * @return the search spectrum
     */
    public Spectrum getSpectrum(byte[] bytes, int offset, int mzOffset, int intOffset, int mobiOffset) {
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
     * 从一个完整的Swath Block块中取出一条记录 查询条件: 1. block索引号 2. rt
     * <p>
     * Read a spectrum from aird with block index and target rt
     *
     * @param index block index
     * @param rt    retention time of the target spectrum
     * @return the target spectrum
     */
    public Spectrum getSpectrumByRt(BlockIndex index, double rt) {
        List<Double> rts = index.getRts();
        int position = rts.indexOf(rt);
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
    public Spectrum getSpectrumByRt(long startPtr, List<Double> rtList, List<Integer> mzOffsets, List<Integer> intOffsets, double rt) {
        int position = rtList.indexOf(rt);
        return getSpectrumByIndex(startPtr, mzOffsets, intOffsets, position);
    }

    /**
     * 根据序列号查询光谱
     *
     * @param index 索引序列号
     * @return 该索引号对应的光谱信息
     */
    public Spectrum getSpectrum(int index) {
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
     * @param blockIndex 块索引
     * @param index      块内索引值
     * @return 对应光谱数据
     */
    public Spectrum getSpectrumByIndex(BlockIndex blockIndex, int index) {
        return getSpectrumByIndex(blockIndex.getStartPtr(), blockIndex.getMzs(), blockIndex.getInts(), index);
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
    public Spectrum getSpectrumByIndex(long startPtr, List<Integer> mzOffsets, List<Integer> intOffsets, int index) {
        long start = startPtr;

        for (int i = 0; i < index; i++) {
            start += mzOffsets.get(i);
            start += intOffsets.get(i);
        }

        try {
            raf.seek(start);
            byte[] reader = new byte[mzOffsets.get(index) + intOffsets.get(index)];
            raf.read(reader);
            return getSpectrum(reader, 0, mzOffsets.get(index), intOffsets.get(index));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    /**
     * @return the airdinfo
     */
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
