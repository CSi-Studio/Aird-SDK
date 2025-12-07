/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;
import net.csibio.aird.bean.proto.AirdInfo;
import net.csibio.aird.enums.MsLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * The index of the block
 */
@Data
public class BlockIndex {

    private static final long serialVersionUID = -3258834219160663625L;

    /**
     * MS1->1, MS2->2
     *
     * @see MsLevel
     */
    Integer level;

    /**
     * 该数据块在文件中的开始位置 The start pointer in the aird file
     */
    Long startPtr;

    /**
     * 在文件中的结束位置 The end pointer in the aird file
     */
    Long endPtr;

    /**
     * 每一个MS2对应的MS1序号;MS1中为空 The MS1 Number of Current Block Index(Current Block Index should be MS2
     * Index). It is null when Current Block is an MS1 Block Index
     */
    Integer num;

    /**
     * 每一个MS2对应的前体窗口;MS1中为空 The Precursor Range Windows for MS2,It is null when Current Block is an
     * MS1 Block Index
     */
    List<WindowRange> rangeList;

    /**
     * 当msLevel=1时,本字段为每一个MS1谱图的序号,,当msLevel=2时本字段为每一个MS2谱图序列号 When msLevel=1, this field is every
     * MS1's num. When msLevel=2, this field is every MS2's num
     */
    List<Integer> nums;

    /**
     * 所有该块中的rt时间列表 all the retention time in the aird
     */
    List<Double> rts;

    /**
     * Every Spectrum's total intensity in the block 所有该块中的tic列表
     */
    List<Long> tics;

    /**
     * Every Spectrum's injection time列表
     */
    List<Float> injectionTimes;

    /**
     * Every Spectrum's total base peak intensity in the block 所有该块中的base peak intensity列表
     */
    List<Double> basePeakIntensities;

    /**
     * Every Spectrum's total base peak mz in the block 所有该块中的base peak mz列表
     */
    List<Double> basePeakMzs;

    /**
     * Every Spectrum's filterString in the block
     * 所有该块中的filterString列表
     */
    List<String> filterStrings;

    /**
     * Every Spectrum's activator in the block
     * 所有该块中的activator列表
     */
    List<String> activators;

    /**
     * Every Spectrum's energy in the block
     * 所有该块中的energy列表
     */
    List<Float> energies;

    /**
     * Every Spectrum's polarity in the block
     * 所有该块中的polarity列表
     */
    List<String> polarities;

    /**
     * Every Spectrum's msType in the block
     * 所有该块中的msType列表
     */
    List<String> msTypes;

    /**
     * 一个块中所有子谱图的mz的压缩后的大小列表 every compressed mz array's binary size in the block index
     */
    List<Integer> mzs;

    /**
     * 一个块中所有子谱图的intenisty的压缩后的大小列表 every compressed intensity array's binary size in the block index
     */
    List<Integer> ints;

    /**
     * 一个块中所有子谱图的mobility的压缩后的大小列表 every compressed mobility array's binary size in the block index
     */
    List<Integer> mobilities;

    /**
     * 用于存储KV键值对 Pairs with key/value for extension features
     */
    String features;

    /**
     * @return window range
     */
    public WindowRange getWindowRange() {
        if (rangeList == null || rangeList.isEmpty()) {
            return null;
        } else {
            return rangeList.get(0);
        }
    }

//    /**
//     * @param windowRange set the windowRange
//     */
//    public void setWindowRange(WindowRange windowRange) {
//        if (rangeList == null) {
//            rangeList = new ArrayList<>();
//        }
//        rangeList.add(windowRange);
//    }

    /**
     * @return get the parent num
     */
    public Integer getParentNum() {
        if (level.equals(2)) {
            return num;
        } else {
            return null;
        }
    }

    public static BlockIndex fromProto(AirdInfo.BlockIndexProto indexProto){
        BlockIndex index = new BlockIndex();
        index.setLevel(indexProto.getLevel());
        index.setStartPtr(indexProto.getStartPtr());
        index.setEndPtr(indexProto.getEndPtr());
        index.setNum(indexProto.getNum());

        if (indexProto.getRangeListCount() > 0) {
            List<WindowRange> rangeList = new ArrayList<>();
            for (net.csibio.aird.bean.proto.WindowRange.WindowRangeProto windowRangeProto : indexProto.getRangeListList()) {
                rangeList.add(WindowRange.fromProto(windowRangeProto));
            }
            index.setRangeList(rangeList);
        }

        if (indexProto.getNumsCount() > 0) {
            index.setNums(new ArrayList<>(indexProto.getNumsList()));
        }

        if (indexProto.getRtsCount() > 0) {
            index.setRts(new ArrayList<>(indexProto.getRtsList()));
        }

        if (indexProto.getTicsCount() > 0) {
            index.setTics(new ArrayList<>(indexProto.getTicsList()));
        }

        if (indexProto.getInjectionTimesCount() > 0) {
            index.setInjectionTimes(new ArrayList<>(indexProto.getInjectionTimesList()));
        }

        if (indexProto.getBasePeakIntensitiesCount() > 0) {
            index.setBasePeakIntensities(new ArrayList<>(indexProto.getBasePeakIntensitiesList()));
        }

        if (indexProto.getBasePeakMzsCount() > 0) {
            index.setBasePeakMzs(new ArrayList<>(indexProto.getBasePeakMzsList()));
        }

        if (indexProto.getFilterStringsCount() > 0) {
            index.setFilterStrings(new ArrayList<>(indexProto.getFilterStringsList()));
        }

        if (indexProto.getActivatorsCount() > 0) {
            index.setActivators(new ArrayList<>(indexProto.getActivatorsList()));
        }

        if (indexProto.getEnergiesCount() > 0) {
            index.setEnergies(new ArrayList<>(indexProto.getEnergiesList()));
        }

        if (indexProto.getPolaritiesCount() > 0) {
            index.setPolarities(new ArrayList<>(indexProto.getPolaritiesList()));
        }

        if (indexProto.getMsTypesCount() > 0) {
            index.setMsTypes(new ArrayList<>(indexProto.getMsTypesList()));
        }

        if (indexProto.getMzsCount() > 0) {
            index.setMzs(new ArrayList<>(indexProto.getMzsList()));
        }

        if (indexProto.getIntsCount() > 0) {
            index.setInts(new ArrayList<>(indexProto.getIntsList()));
        }

        if (indexProto.getMobilitiesCount() > 0) {
            index.setMobilities(new ArrayList<>(indexProto.getMobilitiesList()));
        }

        index.setFeatures(indexProto.getFeatures());
        return index;
    }
}
