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

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.FileUtil;

import java.io.RandomAccessFile;
import java.util.*;

/**
 * DIA/SWATH模式的转换器
 * DIA Parser will convert the original order which is ordered by retention time to a precursor m/z grouped block.
 * spectras will be grouped by Precursor m/z range
 */
public class DIAParser extends BaseParser {
    public DIAParser(String indexFilePath) throws ScanException {
        super(indexFilePath);
    }

    public DIAParser(String airdPath, Compressor mzCompressor, Compressor intCompressor, int mzPrecision) throws ScanException {
        super(airdPath, mzCompressor, intCompressor, mzPrecision, AirdType.DIA_SWATH.getName());
    }

    public TreeMap<Float, MzIntensityPairs> getSpectrums(BlockIndex index) {
        return getSpectrums(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(), index.getInts());
    }

    /**
     * the result key is rt,value is the spectrum
     * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息,原始谱图信息包含mz数组和intensity两个相同长度的数组
     * 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
     *
     * @param startPtr    起始指针位置
     * @param endPtr      结束指针位置
     * @param rtList      rt列表,包含所有的光谱产出时刻
     * @param mzSizeList  mz块的大小列表
     * @param intSizeList intensity块的大小列表
     * @return 每一个时刻对应的光谱信息
     */
    public TreeMap<Float, MzIntensityPairs> getSpectrums(long startPtr, long endPtr, List<Float> rtList, List<Long> mzSizeList, List<Long> intSizeList) {

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
                float[] mzArray = getMzValues(result, start, mzSizeList.get(i).intValue());
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
     * 从aird文件中获取某一条记录
     * 从一个完整的Swath Block块中取出一条记录
     *
     * @param startPtr    起始位置
     * @param rtList      全部时刻列表
     * @param mzSizeList  mz数组长度列表
     * @param intSizeList int数组长度列表
     * @param rt          获取某一个时刻原始谱图
     * @return 某个时刻的光谱信息
     */

    public MzIntensityPairs getSpectrumByRt(long startPtr, List<Float> rtList, List<Long> mzSizeList, List<Long> intSizeList, float rt) {
        int position = rtList.indexOf(rt);
        return getSpectrumByIndex(startPtr, mzSizeList, intSizeList, position);
    }

    public MzIntensityPairs getSpectrumByRt(BlockIndex index, float rt) {
        List<Float> rts = index.getRts();
        int position = rts.indexOf(rt);
        return getSpectrumByIndex(index, position);
    }

    public MzIntensityPairs getSpectrumByIndex(long startPtr, List<Long> mzSizeList, List<Long> intSizeList, int index) {
        RandomAccessFile raf = null;
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
            float[] mzArray = getMzValues(reader);
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

    public MzIntensityPairs getSpectrumByIndex(BlockIndex blockIndex, int index) {
        return getSpectrumByIndex(blockIndex.getStartPtr(), blockIndex.getMzs(), blockIndex.getInts(), index);
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
     * 从Aird文件中读取,但是不要将m/z数组的从Integer改为Float
     *
     * @param index 索引序列号
     * @param rt    光谱产出时刻
     * @return 该时刻产生的光谱信息, 其中mz数据以int类型返回
     */
    public MzIntensityPairs getSpectrumAsInteger(BlockIndex index, float rt) {
        RandomAccessFile raf = null;
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

    public void close() {
        FileUtil.close(raf);
    }
}
