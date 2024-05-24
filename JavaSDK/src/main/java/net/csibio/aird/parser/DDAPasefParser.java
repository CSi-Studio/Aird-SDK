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
import net.csibio.aird.bean.DDAPasefMs;
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
public class DDAPasefParser extends BaseParser {

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public DDAPasefParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @param airdInfo      airdinfo instance
     * @throws Exception scan exception
     */
    public DDAPasefParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    /**
     * get ms1 index from the index file
     *
     * @return the index info of all the ms1 spectrum
     */
    public BlockIndex getMs1Index() {
        if (airdInfo != null && airdInfo.getIndexList() != null && !airdInfo.getIndexList().isEmpty()) {
            return airdInfo.getIndexList().get(0);
        }
        return null;
    }

    /**
     * get ms2 index from the index file
     *
     * @return the index info of all the ms2 spectrum
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
    public List<DDAPasefMs> readAllToMemory(boolean includeBinaryData) throws Exception {
        List<DDAPasefMs> ms1List = new ArrayList<>();
        BlockIndex ms1Index = getMs1Index();//所有的ms1谱图都在第一个index中
        List<BlockIndex> ms2IndexList = getAllMs2Index();
        TreeMap<Double, Spectrum> ms1Map = new TreeMap<>();
        if (includeBinaryData){
            ms1Map = getSpectra(ms1Index.getStartPtr(), ms1Index.getEndPtr(), ms1Index.getRts(), ms1Index.getMzs(), ms1Index.getInts(), ms1Index.getMobilities());
        }

        List<Double> ms1RtList = new ArrayList<>(ms1Index.getRts());

        for (int i = 0; i < ms1RtList.size(); i++) {
            DDAPasefMs ms1 = new DDAPasefMs(ms1RtList.get(i), ms1Map.get(ms1RtList.get(i)));
            DDAUtil.initFromIndex(airdInfo, ms1, ms1Index, i);
            Optional<BlockIndex> ms2IndexRes = ms2IndexList.stream().filter(index -> index.getParentNum().equals(ms1.getNum())).findFirst();
            if (ms2IndexRes.isPresent()) {
                BlockIndex ms2Index = ms2IndexRes.get();
                try {
                    TreeMap<Double, Spectrum> ms2Map = new TreeMap<>();
                    if(includeBinaryData){
                        ms2Map = getSpectra(ms2Index.getStartPtr(), ms2Index.getEndPtr(), ms2Index.getRts(), ms2Index.getMzs(), ms2Index.getInts(), ms2Index.getMobilities());
                    }

                    List<Double> ms2RtList = new ArrayList<>(ms2Index.getRts());
                    List<DDAPasefMs> ms2List = new ArrayList<>();
                    for (int j = 0; j < ms2RtList.size(); j++) {
                        DDAPasefMs ms2 = new DDAPasefMs(ms2RtList.get(j), ms2Map.get(ms2RtList.get(j)));
                        DDAUtil.initFromIndex(airdInfo, ms2, ms2Index, j);
                        ms2List.add(ms2);
                    }
                    ms1.setMs2List(ms2List);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ms1List.add(ms1);
        }
        return ms1List;
    }

    /**
     * @param rtStart the start of the retention time
     * @param rtEnd   the end of the retention time
     * @return all the spectra in the target retention time range
     */
    public List<DDAPasefMs> getSpectraByRtRange(double rtStart, double rtEnd, boolean includeMS2) {
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
        List<DDAPasefMs> ms1List = buildDDAPasefMsList(ms1Index.getRts(), start, end + 1, ms1Index, ms1Map, includeMS2);
        return ms1List;
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

    /**
     * @param rtList     the target rt list
     * @param start      start
     * @param end        end
     * @param ms1Index   the ms1 index
     * @param ms1Map     the ms1 map
     * @param includeMS2 if including the ms2 spectra
     * @return the search DDAMs results
     */
    private List<DDAPasefMs> buildDDAPasefMsList(List<Double> rtList, int start, int end, BlockIndex ms1Index, TreeMap<Double, Spectrum> ms1Map, boolean includeMS2) {
        List<DDAPasefMs> ms1List = new ArrayList<>();
        Map<Integer, BlockIndex> ms2IndexMap = null;
        if (includeMS2) {
            ms2IndexMap = getMs2IndexMap();
        }
        for (int i = start; i < end; i++) {
            DDAPasefMs ms1 = new DDAPasefMs(rtList.get(i), ms1Map.get(rtList.get(i)));
            DDAUtil.initFromIndex(airdInfo, ms1, ms1Index, i);
            if (includeMS2) {
                BlockIndex ms2Index = ms2IndexMap.get(ms1.getNum());
                if (ms2Index != null) {
                    TreeMap<Double, Spectrum> ms2Map = getSpectra(ms2Index.getStartPtr(), ms2Index.getEndPtr(),
                        ms2Index.getRts(), ms2Index.getMzs(), ms2Index.getInts());
                    List<Double> ms2RtList = new ArrayList<>(ms2Map.keySet());
                    List<DDAPasefMs> ms2List = new ArrayList<>();
                    for (int j = 0; j < ms2RtList.size(); j++) {
                        DDAPasefMs ms2 = new DDAPasefMs(ms2RtList.get(j), ms2Map.get(ms2RtList.get(j)));
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
