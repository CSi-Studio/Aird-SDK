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
import net.csibio.aird.bean.DDAPasefMs;
import net.csibio.aird.bean.common.Spectrum4D;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.DDAUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

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

    public DDAPasefParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

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
     * DDA文件采用一次性读入内存的策略 DDA reader using the strategy of loading all the information into the memory
     *
     * @return DDA文件中的所有信息, 以MsCycle的模型进行存储 the mz-intensity pairs read from the aird. And store as
     * MsCycle in the memory
     * @throws Exception exception when reading the file
     */
    public List<DDAPasefMs> readAllToMemory() throws Exception {
        List<DDAPasefMs> ms1List = new ArrayList<>();
        BlockIndex ms1Index = getMs1Index();//所有的ms1谱图都在第一个index中
        List<BlockIndex> ms2IndexList = getAllMs2Index();
        TreeMap<Float, Spectrum4D<float[]>> ms1Map = getSpectra4DAsFloat(ms1Index.getStartPtr(),
                ms1Index.getEndPtr(),
                ms1Index.getRts(), ms1Index.getMzs(), ms1Index.getInts(), ms1Index.getMobilities());
        List<Float> ms1RtList = new ArrayList<>(ms1Map.keySet());

        for (int i = 0; i < ms1RtList.size(); i++) {
            DDAPasefMs ms1 = new DDAPasefMs(ms1RtList.get(i), ms1Map.get(ms1RtList.get(i)));
            DDAUtil.initFromIndex(ms1, ms1Index, i);
            Optional<BlockIndex> ms2IndexRes = ms2IndexList.stream()
                    .filter(index -> index.getParentNum().equals(ms1.getNum())).findFirst();
            if (ms2IndexRes.isPresent()) {
                BlockIndex ms2Index = ms2IndexRes.get();
                try {
                    TreeMap<Float, Spectrum4D<float[]>> ms2Map = getSpectra4DAsFloat(ms2Index.getStartPtr(),
                            ms2Index.getEndPtr(), ms2Index.getRts(), ms2Index.getMzs(), ms2Index.getInts(), ms2Index.getMobilities());
                    List<Float> ms2RtList = new ArrayList<>(ms2Map.keySet());
                    List<DDAPasefMs> ms2List = new ArrayList<>();
                    for (int j = 0; j < ms2RtList.size(); j++) {
                        DDAPasefMs ms2 = new DDAPasefMs(ms2RtList.get(j), ms2Map.get(ms2RtList.get(j)));
                        DDAUtil.initFromIndex(ms2, ms2Index, j);
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
}
