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
import java.util.List;

@Data
public class ChromatogramIndex {

    /**
     * the total chromatograms count, exclude the TIC and BPC chromatograms
     */
    Long totalCount;

    /**
     * acquisitionMethod
     */
    String type;

    /**
     * 1:MS1;2:MS2
     */
    List<String> ids;

    /**
     * the block start position in the file
     * 在文件中的开始位置
     */
    Long startPtr;

    /**
     * the block end position in the file
     * 在文件中的结束位置
     */
    Long endPtr;

    /**
     * Every Chromatogram's activator in the block
     * 所有该块中的activator列表
     */
    List<String> activators;

    /**
     * Every Chromatogram's energy in the block
     * 所有该块中的energy列表
     */
    List<Float> energies;

    /**
     * Every Chromatogram's polarity in the block
     * 所有该块中的polarity列表
     */
    List<String> polarities;

    /**
     * The precursor ion list
     */
    List<WindowRange> precursors;

    /**
     * The product ion list
     */
    List<WindowRange> products;

    /**
     * spectrum num list
     * 谱图序列号
     */
    List<Integer> nums;

    /**
     * rts for chromatogram
     */
    List<Integer> rts;

    /**
     * 一个块中所有子谱图的intensity的压缩后的大小列表
     */
    List<Integer> ints;

    /**
     * PSI CV
     * PSI可控词汇表
     */
    List<List<CV>> cvs;

    /**
     * Features of every index
     * 用于存储KV键值对
     */
    String features;
}
