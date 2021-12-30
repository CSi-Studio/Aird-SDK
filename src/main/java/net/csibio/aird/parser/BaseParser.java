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

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdScanUtil;
import net.csibio.aird.util.CompressUtil;
import net.csibio.aird.util.FileUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.TreeMap;

/**
 * Base Parser
 */
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
     * the m/z precision
     */
    public int mzPrecision;

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
    public BaseParser(String indexPath) throws ScanException {
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
        mzCompressor = CompressUtil.getMzCompressor(airdInfo.getCompressors());
        intCompressor = CompressUtil.getIntCompressor(airdInfo.getCompressors());
        mzPrecision = mzCompressor.getPrecision();
        type = airdInfo.getType();
    }

    /**
     * 构造函数
     *
     * @param indexPath 索引文件的位置
     * @throws ScanException 扫描时的异常
     */
    public BaseParser(String indexPath, AirdInfo airdInfo) throws ScanException {
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
        mzCompressor = CompressUtil.getMzCompressor(airdInfo.getCompressors());
        intCompressor = CompressUtil.getIntCompressor(airdInfo.getCompressors());
        mzPrecision = mzCompressor.getPrecision();
        type = airdInfo.getType();
    }

    public static BaseParser buildParser(String indexPath) throws ScanException {
        File indexFile = new File(indexPath);
        if (indexFile.exists() && indexFile.canRead()) {
            AirdInfo airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
            if (airdInfo == null) {
                throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
            }
            return switch (AirdType.getType(airdInfo.getType())) {
                case DDA -> new DDAParser(indexPath, airdInfo);
                case DIA_SWATH -> new DIAParser(indexPath, airdInfo);
                case PRM -> new PRMParser(indexPath, airdInfo);
                case COMMON -> new CommonParser(indexPath, airdInfo);
                default -> throw new IllegalStateException("Unexpected value: " + AirdType.getType(airdInfo.getType()));
            };
        }
        throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
    }

    /**
     * 使用直接的关键信息进行初始化
     *
     * @param airdPath      Aird文件路径
     * @param mzCompressor  mz压缩策略
     * @param intCompressor intensity压缩策略
     * @param mzPrecision   mz数字精度
     * @param airdType      aird类型
     * @throws ScanException 扫描异常
     */
    public BaseParser(String airdPath, Compressor mzCompressor, Compressor intCompressor, int mzPrecision, String airdType) throws ScanException {
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
        this.mzPrecision = mzPrecision;
        this.type = airdType;
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
     * 根据特定BlockIndex取出对应TreeMap
     *
     * @param raf        the random access file reader
     * @param blockIndex the block index read from the index file
     * @return 解码内容, key为rt, value为光谱中的键值对
     * @throws Exception 读取文件异常
     */
    public TreeMap<Double, MzIntensityPairs> parseBlockValue(RandomAccessFile raf, BlockIndex blockIndex) throws Exception {
        TreeMap<Double, MzIntensityPairs> map = new TreeMap<>();
        List<Float> rts = blockIndex.getRts();

        raf.seek(blockIndex.getStartPtr());
        long delta = blockIndex.getEndPtr() - blockIndex.getStartPtr();
        byte[] result = new byte[(int) delta];

        raf.read(result);
        List<Long> mzSizes = blockIndex.getMzs();
        List<Long> intensitySizes = blockIndex.getInts();

        int start = 0;
        for (int i = 0; i < mzSizes.size(); i++) {
            byte[] mz = ArrayUtils.subarray(result, start, start + mzSizes.get(i).intValue());
            start = start + mzSizes.get(i).intValue();
            byte[] intensity = ArrayUtils.subarray(result, start, start + intensitySizes.get(i).intValue());
            start = start + intensitySizes.get(i).intValue();
            try {
                MzIntensityPairs pairs = new MzIntensityPairs(getMzValues(mz), getIntValues(intensity));
                map.put(rts.get(i) / 60d, pairs);
            } catch (Exception e) {
                throw e;
            }
        }
        return map;
    }

    /**
     * 根据特定BlockIndex取出对应TreeMap
     *
     * @param raf        the random access file reader
     * @param blockIndex the block index read from the index file
     * @return 解码内容, key为rt, value为光谱中的键值对
     * @throws Exception 读取文件异常
     */
    public TreeMap<Float, Spectrum> parseBlock(RandomAccessFile raf, BlockIndex blockIndex) throws Exception {
        TreeMap<Float, Spectrum> map = new TreeMap<>();
        List<Float> rts = blockIndex.getRts();

        raf.seek(blockIndex.getStartPtr());
        long delta = blockIndex.getEndPtr() - blockIndex.getStartPtr();
        byte[] result = new byte[(int) delta];

        raf.read(result);
        List<Long> mzSizes = blockIndex.getMzs();
        List<Long> intensitySizes = blockIndex.getInts();

        int start = 0;
        for (int i = 0; i < mzSizes.size(); i++) {
            byte[] mz = ArrayUtils.subarray(result, start, start + mzSizes.get(i).intValue());
            start = start + mzSizes.get(i).intValue();
            byte[] intensity = ArrayUtils.subarray(result, start, start + intensitySizes.get(i).intValue());
            start = start + intensitySizes.get(i).intValue();
            try {
                Spectrum pairs = new Spectrum(getMzValues(mz), getIntValues(intensity));
                map.put(rts.get(i) / 60f, pairs);
            } catch (Exception e) {
                throw e;
            }
        }
        return map;
    }

    /**
     * get mz values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
     *
     * @param value 压缩后的数组
     * @return 解压缩后的数组
     */
    public float[] getMzValues(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(mzCompressor.fetchByteOrder());

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }
        intValues = CompressUtil.fastPforDecoder(intValues);
        float[] floatValues = new float[intValues.length];
        for (int index = 0; index < intValues.length; index++) {
            floatValues[index] = (float) intValues[index] / mzPrecision;
        }
        byteBuffer.clear();
        return floatValues;
    }

    /**
     * get mz values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
     *
     * @param value  压缩后的数组
     * @param start  起始位置
     * @param length 读取长度
     * @return 解压缩后的数组
     */
    public float[] getMzValues(byte[] value, int start, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value, start, length));
        byteBuffer.order(mzCompressor.fetchByteOrder());

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }
        intValues = CompressUtil.fastPforDecoder(intValues);
        float[] floatValues = new float[intValues.length];
        for (int index = 0; index < intValues.length; index++) {
            floatValues[index] = (float) intValues[index] / mzPrecision;
        }
        byteBuffer.clear();
        return floatValues;
    }

    /**
     * get mz values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN
     *
     * @param value 加密的数组
     * @return 解压缩后的数组
     */
    public int[] getMzValuesAsInteger(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(mzCompressor.fetchByteOrder());

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }
        intValues = CompressUtil.fastPforDecoder(intValues);
        byteBuffer.clear();
        return intValues;
    }


    /**
     * get tag values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
     *
     * @param value 压缩后的数组
     * @return 解压缩后的数组
     */
    public int[] getTags(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
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
     * get tag values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
     *
     * @param value  压缩后的数组
     * @param start  起始位置
     * @param length 读取长度
     * @return 解压缩后的数组
     */
    public int[] getTags(byte[] value, int start, int length) {
        byte[] tagShift = CompressUtil.zlibDecoder(value, start, length);
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

        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(intCompressor.fetchByteOrder());

        FloatBuffer intensities = byteBuffer.asFloatBuffer();
        float[] intensityValues = new float[intensities.capacity()];
        for (int i = 0; i < intensities.capacity(); i++) {
            intensityValues[i] = intensities.get(i);
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
    public float[] getIntValues(byte[] value, int start, int length) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value, start, length));
        byteBuffer.order(intCompressor.fetchByteOrder());

        FloatBuffer intensities = byteBuffer.asFloatBuffer();
        float[] intensityValues = new float[intensities.capacity()];
        for (int i = 0; i < intensities.capacity(); i++) {
            intensityValues[i] = intensities.get(i);
        }

        byteBuffer.clear();
        return intensityValues;
    }

    /**
     * get mz values only for aird file
     *
     * @param value 压缩的数组
     * @return 解压缩后的数组
     */
    public float[] getLogedIntValues(byte[] value) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(intCompressor.fetchByteOrder());

        FloatBuffer intensities = byteBuffer.asFloatBuffer();
        float[] intValues = new float[intensities.capacity()];
        for (int i = 0; i < intensities.capacity(); i++) {
            intValues[i] = (float) Math.pow(10, intensities.get(i));
        }

        byteBuffer.clear();
        return intValues;
    }

    /**
     * get mz values only for aird file
     *
     * @param value  压缩的数组
     * @param start  起始位置
     * @param length 长度
     * @return 解压缩后的数组
     */
    public float[] getLogedIntValues(byte[] value, int start, int length) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value, start, length));
        byteBuffer.order(intCompressor.fetchByteOrder());

        FloatBuffer intensities = byteBuffer.asFloatBuffer();
        float[] intValues = new float[intensities.capacity()];
        for (int i = 0; i < intensities.capacity(); i++) {
            intValues[i] = (float) Math.pow(10, intensities.get(i));
        }

        byteBuffer.clear();
        return intValues;
    }

    /**
     * Close the raf object
     */
    public void close() {
        FileUtil.close(raf);
    }
}
