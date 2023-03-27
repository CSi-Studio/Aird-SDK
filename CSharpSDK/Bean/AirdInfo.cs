/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System;
using System.Collections.Generic;

namespace AirdSDK.Beans
{
    public class AirdInfo
    {
        /**
        * Aird version
        * Aird的版本号
        */
        public string version = "2.3.0";

        /**
         * Aird Code
         * Aird的版本编码
         */
        public int versionCode = 7;

        /**
       * [Core Field]
       * The array compressor strategy
       * [核心字段]
       * 数组压缩策略
       */
        public List<Beans.Compressor> compressors;

        /**
         * Instrument information list
         */
        public List<Instrument> instruments;

        /**
         * dataProcessing information list
         */
        public List<DataProcessing> dataProcessings;

        /**
         * The processed software information
         * 处理的软件信息
         */
        public List<Software> softwares;

        /**
         * The file information before converting
         * 处理前的文件信息
         */
        public List<ParentFile> parentFiles;

        /**
         * [Core Field]
         * Store the window ranges
         * [核心字段]
         * 存储SWATH窗口信息
         */
        public List<WindowRange> rangeList;

        /**
         * [Core Field]
         * Used for block index(PRM/DIA/ScanningSWATH/DDA)
         * When using for storing SWATH window range information, the swath windows have already been adjusted by overlap between windows.
         * [核心字段]
         * 用于存储Block的索引（适用于PRM/DIA/ScanningSwath/DDA）
         * 当存储SWATH窗口信息,窗口已经根据overlap进行过调整
         */
        public List<BlockIndex> indexList;

        /**
         * BlockIndex经过压缩以后的二进制数据开始位置
         * version code >=7 以后支持的字段，支持索引数据使用二进制的方式存储于Aird文件中
         */
        public long indexStartPtr;

        /**
         * BlockIndex经过压缩以后的二进制数据结束位置
         * version code >=7 以后支持的字段，支持索引数据使用二进制的方式存储于Aird文件中
         */
        public long indexEndPtr;

        /**
         * [Core Field]
         * [核心字段]
         * 用于SRM采集模式下色谱信息的存储
         */
        public ChromatogramIndex chromatogramIndex;

        /**
         * [Core Field]
         * AcquisitionMethod, Support for DIA/SWATH, PRM, DDA, SRM/MRM, DDAPasef, DIAPasef
         * [核心字段]
         * Aird支持的采集模式的类型,目前支持SRM/MRM, DIA, PRM, DDA, DDAPasef, DIAPasef 6种
         */
        public string type;

        /**
         * the vendor file size
         * 原始文件的文件大小,单位byte
         */
        public long fileSize;

        /**
         * the total spectra count
         * 总计拥有的光谱数
         */
        public long totalCount;

        /**
        * the aird file path.
        * 转换压缩后的aird二进制文件路径,默认读取同目录下的同名文件,如果不存在才去读本字段对应的路径
        */
        public string airdPath;

        /**
        * See ActivationMethod: HCD,CID....
        * 碎裂方法,详情见ActivationMethod类
        */
        public string activator;

        /**
        * Activation Method Energy
        * 轰击能量
        */
        public float energy;

        /**
        * Profile or Centroid
        */
        public string msType;

        /**
         * rt unit
         * rt的时间单位, Aird格式下统一为秒
         */
        public string rtUnit = "second";

        /**
         * polarity
         */
        public string polarity;

        /**
         * filter String
         */
        public string filterString;

        /**
         * If ignore the point which intensity = 0
         * 是否忽略intensity为0的点
         */
        public bool ignoreZeroIntensityPoint = true;

        /**
         * Use in ion mobility acquisition method
         * 如果是Mobility采集模式,本字段会启用
         */
        public MobiInfo mobiInfo = new MobiInfo();

        /**
        * the aird file creator
        * 实验的创建者
        */
        public string creator;

        /**
        * the features. See Features.cs
        * 特征键值对,详情见Features.cs
        */
        public string features;

        /**
         * 采集日期
         */
        public string startTimeStamp;

        /**
        * the create data
        * 文件的创建日期
        */
        public string createDate;
    }
}