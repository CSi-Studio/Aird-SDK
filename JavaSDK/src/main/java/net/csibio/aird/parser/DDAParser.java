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
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.DDAUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DDA模式的转换器 The parser for DDA acquisition mode. The index is group like MS1-MS2 Group DDA reader
 * using the strategy of loading all the information into the memory
 */
public class DDAParser extends BaseParser {

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public DDAParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    /**
     * DDAParser 构造函数
     *
     * @param indexFilePath index file path
     * @param airdInfo      airdInfo
     * @throws Exception exception
     */
    public DDAParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    /**
     * DDA只有一个MS1 BlockIndex,因此是归属于DDAParser的特殊算法
     *
     * @return the index of all the ms1
     */
    public BlockIndex getMs1Index() {
        if (airdInfo != null && airdInfo.getIndexList() != null && airdInfo.getIndexList().size() > 0) {
            return airdInfo.getIndexList().get(0);
        }
        return null;
    }

    /**
     * get the index of all the ms2 spectrum
     *
     * @return the index of all the ms2 indexes
     */
    public List<BlockIndex> getAllMs2Index() {
        if (airdInfo != null && airdInfo.getIndexList() != null && airdInfo.getIndexList().size() > 0) {
            return airdInfo.getIndexList().subList(1, airdInfo.getIndexList().size());
        }
        return null;
    }

    /**
     * key为parentNum
     *
     * @return the ms2 index,key is the num, value is the index instance
     */
    public Map<Integer, BlockIndex> getMs2IndexMap() {
        if (airdInfo != null && airdInfo.getIndexList() != null && airdInfo.getIndexList().size() > 0) {
            List<BlockIndex> ms2IndexList = airdInfo.getIndexList().subList(1, airdInfo.getIndexList().size());
            return ms2IndexList.stream().collect(Collectors.toMap(BlockIndex::getParentNum, Function.identity()));
        }
        return null;
    }

    /**
     * DDA文件采用一次性读入内存的策略 DDA reader using the strategy of loading all the information into the memory
     *
     * @return DDA文件中的所有信息, 以MsCycle的模型进行存储 the mz-intensity pairs read from the aird. And store as
     * MsCycle in the memory
     * @throws Exception exception when reading the file
     */
    public List<DDAMs> readAllToMemory() throws Exception {
        BlockIndex ms1Index = getMs1Index();//所有的ms1谱图都在第一个index中
        TreeMap<Double, Spectrum> ms1Map = getSpectra(ms1Index);
        List<Double> ms1RtList = new ArrayList<>(ms1Map.keySet());
        List<DDAMs> ms1List = buildDDAMsList(ms1RtList, 0, ms1RtList.size(), ms1Index, ms1Map, true);
        return ms1List;
    }

    /**
     * key为rt, value为对应谱图
     *
     * @return the map of the ms1, key is rt, value is spectrum
     */
    public TreeMap<Double, Spectrum> getMs1SpectraMap() {
        BlockIndex ms1Index = getMs1Index();
        return getSpectra(ms1Index);
    }

    /**
     * @param rtStart the start of the retention time
     * @param rtEnd   the end of the retention time
     * @return all the spectra in the target retention time range
     */
    public List<DDAMs> getSpectraByRtRange(double rtStart, double rtEnd, boolean includeMS2) {
        BlockIndex ms1Index = getMs1Index();
        Double[] rts = new Double[ms1Index.getRts().size()];
        ms1Index.getRts().toArray(rts);
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
        TreeMap<Double, Spectrum> ms1Map = new TreeMap<>();
        for (int i = start; i <= end; i++) {
            ms1Map.put(rts[i], getSpectrumByIndex(ms1Index, i));
        }

        List<DDAMs> ms1List = buildDDAMsList(ms1Index.getRts(), start, end+1, ms1Index, ms1Map, includeMS2);
        return ms1List;
    }

    /**
     * @param rtList     the target rt list
     * @param start      start
     * @param end        end
     * @param ms1Index   the ms1 index
     * @param ms1Map     the ms1 map
     * @param includeMS2 if including the ms2 spectra
     * @return the search DDAMs results
     */
    private List<DDAMs> buildDDAMsList(List<Double> rtList, int start, int end, BlockIndex ms1Index, TreeMap<Double, Spectrum> ms1Map, boolean includeMS2) {
        List<DDAMs> ms1List = new ArrayList<>();
        Map<Integer, BlockIndex> ms2IndexMap = null;
        if (includeMS2) {
            ms2IndexMap = getMs2IndexMap();
        }
        for (int i = start; i <= (end - start); i++) {
            DDAMs ms1 = new DDAMs(rtList.get(i), ms1Map.get(rtList.get(i)));
            DDAUtil.initFromIndex(airdInfo, ms1, ms1Index, i);
            if (includeMS2) {
                BlockIndex ms2Index = ms2IndexMap.get(ms1.getNum());
                if (ms2Index != null) {
                    TreeMap<Double, Spectrum> ms2Map = getSpectra(ms2Index.getStartPtr(), ms2Index.getEndPtr(),
                            ms2Index.getRts(), ms2Index.getMzs(), ms2Index.getInts());
                    List<Double> ms2RtList = new ArrayList<>(ms2Map.keySet());
                    List<DDAMs> ms2List = new ArrayList<>();
                    for (int j = 0; j < ms2RtList.size(); j++) {
                        DDAMs ms2 = new DDAMs(ms2RtList.get(j), ms2Map.get(ms2RtList.get(j)));
                        DDAUtil.initFromIndex(airdInfo, ms2, ms2Index, j);
                        ms2List.add(ms2);
                    }
                    ms1.setMs2List(ms2List);
                }
            }

            ms1List.add(ms1);
        }
        return ms1List;
    }
}
