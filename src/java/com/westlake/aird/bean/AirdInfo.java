/*
 * Copyright (c) 2020 Propro Studio
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AirdInfo {

    /**
     * [核心字段]
     * 数组压缩策略
     * [Core Field]
     * Data Compression Strategies
     *
     * @see Compressor
     */
    List<Compressor> compressors;

    /**
     * Version
     * 应用版本号
     */
    String version = "1.0.0";

    /**
     * Version Code. Integer code from 1 to N
     * 应用版本编码,从1开始计数的整型编码
     */
    Integer versionCode = 1;

    /**
     * [核心字段]
     * m/z窗口信息,窗口已经根据overlap进行过调整
     * [Core Field]
     * Store the precursor m/z window ranges which have been adjusted with experiment overlap
     *
     * @see WindowRange
     */
    List<WindowRange> rangeList = new ArrayList<WindowRange>();

    /**
     * [核心字段]
     * 用于存储Block的索引
     * [Core Field]
     * Store the Block Index
     */
    List<BlockIndex> indexList;

    /**
     * 仪器设备信息
     * General information about the MS instruments
     */
    List<Instrument> instruments;

    /**
     * DataProcessing information list
     */
    List<DataProcessing> dataProcessings;

    /**
     * 处理的软件信息
     * Software identifier
     */
    List<Software> softwares;

    /**
     * 处理前的文件信息
     * Path to all the ancestor files (up to the native acquisition file).
     */
    List<ParentFile> parentFiles;

    /**
     * [核心字段]
     * Aird文件类型
     * [Core Field]
     * Aird File Type
     * @see com.westlake.aird.enums.AirdType
     */
    String type;

    /**
     * 原始文件的总大小,包括json格式的索引文件以及aird格式的质谱数据文件
     * Total File size,including index file with .json format and ms data file with .aird format
     */
    Long fileSize;

    /**
     * 总计光谱数
     * Total Spectrums Count
     */
    Long totalScanCount;

    /**
     * 转换压缩后的aird二进制文件路径,默认读取同目录下的同名文件,如果不存在才去去读本字段对应的路径
     * The .aird file path
     */
    String airdPath;

    /**
     * 文件描述
     * The description for the file
     */
    String description;

    /**
     * 实验的创建者
     * The aird file creator
     */
    String creator;

    /**
     * 实验的创建日期
     * The create date for the aird file
     */
    String createDate;

    /**
     * 是否忽略intensity为0的点
     * Ignore the point which intensity is 0
     */
    Boolean ignoreZeroIntensityPoint = true;

    /**
     * 特征键值对,详情见Features
     * Features
     * @see com.westlake.aird.constant.Features
     */
    String features;
}
