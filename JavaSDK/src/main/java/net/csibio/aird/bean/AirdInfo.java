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
import net.csibio.aird.constant.Features;
import net.csibio.aird.enums.AirdEngine;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.enums.MsLevel;
import net.csibio.aird.bean.msi.MsiInfo;

import java.util.*;

/**
 * AirdInfo
 */
@Data
public class AirdInfo {

    /**
     * Format Version 应用版本号
     */
    String version = "2.4.0";

    /**
     * Version Code. Integer code from 1 to N 应用版本编码,从1开始计数的整型编码
     */
    Integer versionCode = 8;

    /**
     * 压缩内核
     * 分为行式压缩与列式压缩,具体见AirdEngine类
     */
    Integer engine = AirdEngine.RowCompression.getCode();

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
     * BlockIndex经过压缩以后的二进制数据开始位置
     * version code >=7 以后支持的字段，支持索引数据使用二进制的方式存储于Aird文件中
     */
    long indexStartPtr;

    /**
     * BlockIndex经过压缩以后的二进制数据结束位置
     * version code >=7 以后支持的字段，支持索引数据使用二进制的方式存储于Aird文件中
     */
    long indexEndPtr;

    /**
     * [Core Field]
     * [核心字段]
     * 用于MRM采集模式下色谱信息的存储
     */
    ChromatogramIndex chromatogramIndex;

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
     * Aird格式下统一为秒
     */
    String rtUnit;

    /**
     * Polarity
     *
     * @see net.csibio.aird.enums.PolarityType
     */
    String polarity;

    /**
     * filter String
     */
    String filterString;

    /**
     * 是否忽略intensity为0的点 Ignore the point which intensity is 0
     */
    Boolean ignoreZeroIntensityPoint = true;

    /**
     * ion mobility information
     */
    MobiInfo mobiInfo = new MobiInfo();

    /**
     * Use in MSI
     * 如果是空间代谢数据,本字段会启用
     */
    MsiInfo msiInfo;

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

    /**
     * 采集时间
     */
    String startTimeStamp;

    /**
     * @param target target compressor dimension
     * @return the target compressor
     */
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

    /**
     * @return the TIC map
     */
    public TreeMap<Double, Long> getTicMap() {
        TreeMap<Double, Long> map = new TreeMap<Double, Long>() {
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

    public static AirdInfo fromProto(net.csibio.aird.bean.proto.AirdInfo.AirdInfoProto proto) {
        AirdInfo airdInfo = new AirdInfo();

        // 设置基本字段
        airdInfo.version = proto.getVersion();
        airdInfo.versionCode = proto.getVersionCode();
        airdInfo.engine = proto.getEngine();
        airdInfo.type = proto.getType();
        airdInfo.fileSize = proto.getFileSize();
        airdInfo.totalCount = proto.getTotalCount();
        airdInfo.airdPath = proto.getAirdPath();
        airdInfo.activator = proto.getActivator();
        airdInfo.energy = proto.getEnergy();
        airdInfo.msType = proto.getMsType();
        airdInfo.rtUnit = proto.getRtUnit();
        airdInfo.polarity = proto.getPolarity();
        airdInfo.filterString = proto.getFilterString();
        airdInfo.ignoreZeroIntensityPoint = proto.getIgnoreZeroIntensityPoint();
        airdInfo.creator = proto.getCreator();
        airdInfo.features = proto.getFeatures();
        airdInfo.createDate = proto.getCreateDate();
        airdInfo.startTimeStamp = proto.getStartTimeStamp();
        airdInfo.indexStartPtr = proto.getIndexStartPtr();
        airdInfo.indexEndPtr = proto.getIndexEndPtr();

        List<net.csibio.aird.bean.proto.AirdInfo.CompressorProto> compressorProtos = proto.getCompressorsList();
        List<Compressor> compressors = new ArrayList<>();
        for (net.csibio.aird.bean.proto.AirdInfo.CompressorProto compressorProto : compressorProtos) {
            compressors.add(Compressor.fromProto(compressorProto));
        }
        airdInfo.compressors = compressors;

        // 处理instruments列表字段
        List<net.csibio.aird.bean.proto.AirdInfo.InstrumentProto> instrumentProtos = proto.getInstrumentsList();
        List<Instrument> instruments = new ArrayList<>();
        for (net.csibio.aird.bean.proto.AirdInfo.InstrumentProto instrumentProto : instrumentProtos) {
            instruments.add(Instrument.fromProto(instrumentProto));
        }
        airdInfo.instruments = instruments;

        // 处理dataProcessings列表字段
        List<net.csibio.aird.bean.proto.AirdInfo.DataProcessingProto> dataProcessingProtos = proto.getDataProcessingsList();
        List<DataProcessing> dataProcessings = new ArrayList<>();
        for (net.csibio.aird.bean.proto.AirdInfo.DataProcessingProto dataProcessingProto : dataProcessingProtos) {
            dataProcessings.add(DataProcessing.fromProto(dataProcessingProto));
        }
        airdInfo.dataProcessings = dataProcessings;

        // 处理softwares列表字段
        List<net.csibio.aird.bean.proto.AirdInfo.SoftwareProto> softwareProtos = proto.getSoftwaresList();
        List<Software> softwares = new ArrayList<>();
        for (net.csibio.aird.bean.proto.AirdInfo.SoftwareProto softwareProto : softwareProtos) {
            softwares.add(Software.fromProto(softwareProto));
        }
        airdInfo.softwares = softwares;

        // 处理parentFiles列表字段
        List<net.csibio.aird.bean.proto.AirdInfo.ParentFileProto> parentFileProtos = proto.getParentFilesList();
        List<ParentFile> parentFiles = new ArrayList<>();
        for (net.csibio.aird.bean.proto.AirdInfo.ParentFileProto parentFileProto : parentFileProtos) {
            parentFiles.add(ParentFile.fromProto(parentFileProto));
        }
        airdInfo.parentFiles = parentFiles;

        // 处理rangeList列表字段
        List<net.csibio.aird.bean.proto.WindowRange.WindowRangeProto> rangeProtos = proto.getRangeListList();
        List<WindowRange> rangeList = new ArrayList<>();
        for (net.csibio.aird.bean.proto.WindowRange.WindowRangeProto rangeProto : rangeProtos) {
            rangeList.add(WindowRange.fromProto(rangeProto));
        }
        airdInfo.rangeList = rangeList;

        // 处理indexList列表字段
        List<net.csibio.aird.bean.proto.AirdInfo.BlockIndexProto> indexProtos = proto.getIndexListList();
        List<BlockIndex> indexList = new ArrayList<>();
        for (net.csibio.aird.bean.proto.AirdInfo.BlockIndexProto indexProto : indexProtos) {
            indexList.add(BlockIndex.fromProto(indexProto));
        }
        airdInfo.indexList = indexList;

        airdInfo.chromatogramIndex = ChromatogramIndex.fromProto(proto.getChromatogramIndex());
        airdInfo.mobiInfo = MobiInfo.fromProto(proto.getMobiInfo());
        return airdInfo;
    }
}
