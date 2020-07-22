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
     * Data Compression Strategy
     *
     * @see Compressor
     */
    List<Compressor> compressors;

    /**
     * 应用版本号
     */
    String version = "V1";

    /**
     * 应用版本编码,从1开始自增
     */
    Integer versionCode = 1;

    /**
     * [核心字段]
     * 存储SWATH窗口信息,窗口已经根据overlap进行过调整
     * [Core Field]
     * Store the swath window rangs which have been adjusted with experiment overlap
     *
     * @see WindowRange
     */
    List<WindowRange> rangeList = new ArrayList<WindowRange>();

    /**
     * [核心字段]
     * 用于存储Block的索引
     */
    List<BlockIndex> indexList;

    /**
     * 兼容旧版(2.2.2以前)的Aird版本文件
     */
    @Deprecated
    List<BlockIndex> blockIndexList;

    /**
     * 仪器设备信息
     * General information about the MS instrument
     */
    Instrument instrument;

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
     * 实验类型,目前支持DIA_SWATH和PRM两种
     */
    String type;

    /**
     * 原始文件的总大小
     */
    Long fileSize;

    /**
     * 转换压缩后的aird二进制文件路径,默认读取同目录下的同名文件,如果不存在才去去读本字段对应的路径
     */
    String airdPath;

    /**
     * 实验的描述
     */
    String description;

    /**
     * 实验的创建者,本字段在被导入Propro Server时会被操作人覆盖
     */
    String creator;

    /**
     * 实验的创建日期
     */
    String createDate;

    /**
     * 特征键值对,详情见Features.cs
     */
    String features;

    public List<BlockIndex> getIndexList() {
        if (blockIndexList != null && blockIndexList.size() != 0) {
            return blockIndexList;
        } else {
            return indexList;
        }
    }
}
