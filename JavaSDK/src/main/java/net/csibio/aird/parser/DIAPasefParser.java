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

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.exception.ScanException;

import java.util.List;
import java.util.TreeMap;

/**
 * DIA Parser
 */
public class DIAPasefParser extends BaseParser {

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public DIAPasefParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @param airdInfo      airdInfo
     * @throws Exception exception
     */
    public DIAPasefParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    /**
     * the main interface for getting all the spectrums of a block.
     *
     * @param index block index
     * @return all the spectrums
     */
    public TreeMap<Double, Spectrum> getSpectra(BlockIndex index) {
        return getSpectra(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(), index.getInts(), index.getMobilities());
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
     * @param blockIndex 块索引
     * @param index      块内索引值
     * @return 对应光谱数据
     */
    public Spectrum getSpectrumByIndex(BlockIndex blockIndex, int index) {
        return getSpectrumByIndex(blockIndex.getStartPtr(), blockIndex.getMzs(), blockIndex.getInts(), blockIndex.getMobilities(), index);
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
    public Spectrum getSpectrumByIndex(long startPtr, List<Integer> mzOffsets, List<Integer> intOffsets, List<Integer> mobiOffsets, int index) {
        long start = startPtr;

        for (int i = 0; i < index; i++) {
            start += mzOffsets.get(i);
            start += intOffsets.get(i);
            start += mobiOffsets.get(i);
        }

        try {
            raf.seek(start);
            byte[] reader = new byte[mzOffsets.get(index) + intOffsets.get(index) + mobiOffsets.get(index)];
            raf.read(reader);
            return getSpectrum(reader, 0, mzOffsets.get(index), intOffsets.get(index), mobiOffsets.get(index));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
