/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;
import net.csibio.aird.constant.Features;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.enums.MsLevel;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Data
public class AirdInfo {

    /**
     * Format Version 应用版本号
     */
    String version = "2.0.0";

    /**
     * Version Code. Integer code from 1 to N 应用版本编码,从1开始计数的整型编码
     */
    Integer versionCode = 5;

    /**
     * [核心字段] 数组压缩策略 [Core Field] Data Compression Strategies
     *
     * @see Compressor
     */
    List<Compressor> compressors;

    /**
     * 仪器设备信息 General information about the MS instruments
     */
    List<Instrument> instruments;

    /**
     * 数据处理过程的记录字段 DataProcessing information list
     */
    List<DataProcessing> dataProcessings;

    /**
     * 处理的软件信息 Software identifier
     */
    List<Software> softwares;

    /**
     * 处理前的文件信息 Path to all the ancestor files (up to the native acquisition file).
     */
    List<ParentFile> parentFiles;

    /**
     * [核心字段] m/z窗口信息,窗口已经根据overlap进行过调整 [Core Field] Store the precursor m/z window ranges which have
     * been adjusted with experiment overlap
     *
     * @see WindowRange
     */
    List<WindowRange> rangeList;

    /**
     * [核心字段] 用于存储Block的索引 [Core Field] Store the Block Index
     */
    List<BlockIndex> indexList;

    /**
     * [核心字段] Aird文件类型 [Core Field] Aird File Type
     *
     * @see AirdType
     */
    String type;

    /**
     * 原始文件的总大小,包括json格式的索引文件以及aird格式的质谱数据文件 Total File size,including index file with .json format
     * and ms data file with .aird format
     */
    Long fileSize;

    /**
     * 总计光谱数 Total Spectra Count
     */
    Long totalCount;

    /**
     * 转换压缩后的aird二进制文件路径,默认读取同目录下的同名文件,如果不存在才去去读本字段对应的路径 The .aird file path
     */
    String airdPath;

    /**
     * Activator Method
     */
    String activator;

    /**
     * Collision Energy
     */
    Float energy;

    /**
     * MassSpectrumType
     *
     * @see net.csibio.aird.enums.MsType
     */
    String msType;

    /**
     * rt unit
     */
    String rtUnit;

    /**
     * Polarity
     *
     * @see net.csibio.aird.enums.PolarityType
     */
    String polarity;

    /**
     * 是否忽略intensity为0的点 Ignore the point which intensity is 0
     */
    Boolean ignoreZeroIntensityPoint = true;

    /**
     * ion mobility information
     */
    MobiInfo mobiInfo;

    /**
     * 实验的创建者 The aird file creator
     */
    String creator;

    /**
     * 特征键值对,详情见Features Pairs with key/value for extension features
     *
     * @see Features
     */
    String features;

    /**
     * 实验的创建日期 The create date for the aird file
     */
    String createDate;

    public Compressor fetchCompressor(String target) {
        if (compressors == null) {
            return null;
        }
        for (int i = 0; i < compressors.size(); i++) {
            if (compressors.get(i).getTarget().equals(target)) {
                return compressors.get(i);
            }
        }
        return null;
    }

//    public List<BlockIndex> getIndexList() {
//        if (blockIndexList != null && blockIndexList.size() != 0) {
//            return blockIndexList;
//        } else {
//            return indexList;
//        }
//    }

    public TreeMap<Float, Long> getTicMap() {
        TreeMap<Float, Long> map = new TreeMap<Float, Long>() {
        };
        indexList.forEach(blockIndex -> {
            if (Objects.equals(blockIndex.level, MsLevel.MS1.getCode())) {
                for (int i = 0; i < blockIndex.rts.size(); i++) {
                    map.put(blockIndex.rts.get(i), blockIndex.tics.get(i));
                }
            }
        });
        return map;
    }
}
