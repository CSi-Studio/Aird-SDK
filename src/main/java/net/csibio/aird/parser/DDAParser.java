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

    public DDAParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    /**
     * DDA只有一个MS1 BlockIndex,因此是归属于DDAParser的特殊算法
     *
     * @return
     */
    public BlockIndex getMs1Index() {
        if (airdInfo != null && airdInfo.getIndexList() != null && airdInfo.getIndexList().size() > 0) {
            return airdInfo.getIndexList().get(0);
        }
        return null;
    }

    public List<BlockIndex> getAllMs2Index() {
        if (airdInfo != null && airdInfo.getIndexList() != null && airdInfo.getIndexList().size() > 0) {
            return airdInfo.getIndexList().subList(1, airdInfo.getIndexList().size());
        }
        return null;
    }

    /**
     * key为parentNum
     *
     * @return
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
        List<DDAMs> ms1List = buildDDAMsList(ms1RtList, ms1Index, ms1Map, true);
        return ms1List;
    }

    /**
     * key为rt, value为对应谱图
     *
     * @return
     */
    public TreeMap<Double, Spectrum> getMs1SpectraMap() {
        BlockIndex ms1Index = getMs1Index();
        return getSpectra(ms1Index);
    }

    /**
     * @param num 所需要搜索的scan number
     * @return
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
     * @param rtStart
     * @param rtEnd
     * @return
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

        List<DDAMs> ms1List = buildDDAMsList(ms1Index.getRts().subList(start, end + 1), ms1Index, ms1Map, includeMS2);
        return ms1List;
    }

    private List<DDAMs> buildDDAMsList(List<Double> rtList, BlockIndex ms1Index, TreeMap<Double, Spectrum> ms1Map, boolean includeMS2) {
        List<DDAMs> ms1List = new ArrayList<>();
        Map<Integer, BlockIndex> ms2IndexMap = null;
        if (includeMS2) {
            ms2IndexMap = getMs2IndexMap();
        }
        for (int i = 0; i < rtList.size(); i++) {
            DDAMs ms1 = new DDAMs(rtList.get(i), ms1Map.get(rtList.get(i)));
            DDAUtil.initFromIndex(ms1, ms1Index, i);
            if (includeMS2) {
                BlockIndex ms2Index = ms2IndexMap.get(ms1.getNum());
                if (ms2Index != null) {
                    TreeMap<Double, Spectrum> ms2Map = getSpectra(ms2Index.getStartPtr(), ms2Index.getEndPtr(),
                            ms2Index.getRts(), ms2Index.getMzs(), ms2Index.getInts());
                    List<Double> ms2RtList = new ArrayList<>(ms2Map.keySet());
                    List<DDAMs> ms2List = new ArrayList<>();
                    for (int j = 0; j < ms2RtList.size(); j++) {
                        DDAMs ms2 = new DDAMs(ms2RtList.get(j), ms2Map.get(ms2RtList.get(j)));
                        DDAUtil.initFromIndex(ms2, ms2Index, j);
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
