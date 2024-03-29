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
     * 每一个mz对应的原有的层数, 使用zlib压缩,每一个存储的是压缩后的块大小 the original layers of every mz point, with zlib
     * compress.
     */
    List<Integer> tags;

    /**
     * 一个块中所有子谱图的intenisty的压缩后的大小列表 every compressed intensity array's binary size in the block index
     */
    List<Integer> ints;

    /**
     * 一个块中所有子谱图的mobility的压缩后的大小列表 every compressed mobility array's binary size in the block index
     */
    List<Integer> mobilities;

    /**
     * PSI Controlled Vocabulary PSI可控词汇表
     */
    List<List<CV>> cvList;

    /**
     * 用于存储KV键值对 Pairs with key/value for extension features
     */
    String features;

    /**
     * @return window range
     */
    public WindowRange getWindowRange() {
        if (rangeList == null || rangeList.size() == 0) {
            return null;
        } else {
            return rangeList.get(0);
        }
    }

    /**
     * @param windowRange set the windowRange
     */
    public void setWindowRange(WindowRange windowRange) {
        if (rangeList == null) {
            rangeList = new ArrayList<>();
        }
        rangeList.add(windowRange);
    }

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
}
