/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
using AirdSDK.Bean.Msi;
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
        public string version = "2.4.0";

        /**
         * Aird Code
         * Aird的版本编码
         */
        public int versionCode = 8;

        /**
         * 压缩内核
         * 分为行式压缩与列式压缩,具体见AirdEngine类
         */
        public int engine = 0;
        
        /**
         * [Core Field]
         * The array compressor strategy
         * [核心字段]
         * 数组压缩策略
         */
        public List<Compressor> compressors = new();

        /**
         * Instrument information list
         */
        public List<Instrument> instruments = new();

        /**
         * dataProcessing information list
         */
        public List<DataProcessing> dataProcessings = new();

        /**
         * The processed software information
         * 处理的软件信息
         */
        public List<Software> softwares = new();

        /**
         * The file information before converting
         * 处理前的文件信息
         */
        public List<ParentFile> parentFiles = new();

        /**
         * [Core Field]
         * Store the window ranges
         * [核心字段]
         * 存储SWATH窗口信息
         */
        public List<WindowRange> rangeList = new();

        /**
         * [Core Field]
         * Used for block index(PRM/DIA/ScanningSWATH/DDA)
         * When using for storing SWATH window range information, the swath windows have already been adjusted by overlap between windows.
         * [核心字段]
         * 面向计算的场景所使用的索引对象
         * 用于存储Block的索引（适用于PRM/DIA/ScanningSwath/DDA）
         * 当存储SWATH窗口信息,窗口已经根据overlap进行过调整
         */
        public List<BlockIndex> indexList = new();

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
         * AcquisitionMethod, Support for DIA/SWATH, PRM, DDA, SRM/MRM, DDAPasef, DIAPasef, MSI_MALDI
         * [核心字段]
         * Aird支持的采集模式的类型,目前支持SRM/MRM, DIA, PRM, DDA, DDAPasef, DIAPasef, MSI_MALDI 7种
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
         * Use in MSI
         * 如果是空间代谢数据,本字段会启用
         */
        public MsiInfo msiInfo;

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
        
        public AirdInfoProto ToProto()
        {
            AirdInfoProto proto = new AirdInfoProto
            {
                Version = this.version,
                VersionCode = this.versionCode,
                Engine = this.engine,
                IndexStartPtr = this.indexStartPtr,
                IndexEndPtr = this.indexEndPtr,
                FileSize = this.fileSize,
                TotalCount = this.totalCount,
                Energy = this.energy,
                RtUnit = this.rtUnit,
                IgnoreZeroIntensityPoint = this.ignoreZeroIntensityPoint,
                MobiInfo = this.mobiInfo?.ToProto()
            };

            if (this.compressors != null && this.compressors.Count > 0)
            {
                List<CompressorProto> protos = new List<CompressorProto>();
                foreach (var compressor in this.compressors)
                {
                    protos.Add(compressor.ToProto());
                }

                proto.Compressors.AddRange(protos);
            }
            if (this.instruments != null && this.instruments.Count > 0)
            {
                List<InstrumentProto> protos = new List<InstrumentProto>();
                foreach (var instrument in this.instruments)
                {
                    protos.Add(instrument.ToProto());
                }

                proto.Instruments.AddRange(protos);
            }
            if (this.dataProcessings != null && this.dataProcessings.Count > 0)
            {
                List<DataProcessingProto> protos = new List<DataProcessingProto>();
                foreach (var dataProcessing in this.dataProcessings)
                {
                    protos.Add(dataProcessing.ToProto());
                }

                proto.DataProcessings.AddRange(protos);
            } 
            if (this.softwares != null && this.softwares.Count > 0)
            {
                List<SoftwareProto> protos = new List<SoftwareProto>();
                foreach (var software in this.softwares)
                {
                    protos.Add(software.ToProto());
                }

                proto.Softwares.AddRange(protos);
            } 
            if (this.parentFiles != null && this.parentFiles.Count > 0)
            {
                List<ParentFileProto> protos = new List<ParentFileProto>();
                foreach (var parent in this.parentFiles)
                {
                    protos.Add(parent.ToProto());
                }

                proto.ParentFiles.AddRange(protos);
            }
            if (this.rangeList != null && this.rangeList.Count > 0)
            {
                List<WindowRangeProto> protos = new List<WindowRangeProto>();
                foreach (var range in this.rangeList)
                {
                    protos.Add(range.ToProto());
                }

                proto.RangeList.AddRange(protos);
            }
            if (this.indexList != null && this.indexList.Count > 0)
            {
                List<BlockIndexProto> protos = new List<BlockIndexProto>();
                foreach (var blockIndex in this.indexList)
                {
                    protos.Add(blockIndex.ToProto());
                }

                proto.IndexList.AddRange(protos);
            }

            if (chromatogramIndex != null)
            {
                proto.ChromatogramIndex = this.chromatogramIndex.ToProto();
            }

            if (airdPath != null)
            {
                proto.AirdPath = this.airdPath;
            }
            
            if (type != null)
            {
                proto.Type = this.type;
            }
            if (activator != null)
            {
                proto.Activator = this.activator;
            }
            if (msType != null)
            {
                proto.MsType = this.msType;
            }
            if (polarity != null)
            {
                proto.Polarity = this.polarity;
            }
            if (filterString != null)
            {
                proto.FilterString = this.filterString;
            }
            if (creator != null)
            {
                proto.Creator = this.creator;
            }
            if (features != null)
            {
                proto.Features = this.features;
            }
            if (startTimeStamp != null)
            {
                proto.StartTimeStamp = this.startTimeStamp;
            }
            if (createDate != null)
            {
                proto.CreateDate = this.createDate;
            }
            
            return proto;
        }

        public static AirdInfo FromProto(AirdInfoProto proto)
        {
            if (proto == null)
                throw new ArgumentNullException(nameof(proto));

            var airdInfo = new AirdInfo
            {
                version = proto.Version,
                versionCode = proto.VersionCode,
                engine = proto.Engine,
                indexStartPtr = proto.IndexStartPtr,
                indexEndPtr = proto.IndexEndPtr,
                type = proto.Type,
                fileSize = proto.FileSize,
                totalCount = proto.TotalCount,
                airdPath = proto.AirdPath,
                activator = proto.Activator,
                energy = proto.Energy,
                msType = proto.MsType,
                rtUnit = proto.RtUnit,
                polarity = proto.Polarity,
                filterString = proto.FilterString,
                ignoreZeroIntensityPoint = proto.IgnoreZeroIntensityPoint,
                creator = proto.Creator,
                features = proto.Features,
                startTimeStamp = proto.StartTimeStamp,
                createDate = proto.CreateDate,
                mobiInfo = MobiInfo.FromProto(proto.MobiInfo),
                chromatogramIndex = ChromatogramIndex.FromProto(proto.ChromatogramIndex)
            };

            // Convert protobuf lists to AirdInfo lists
            foreach (var compressor in proto.Compressors)
            {
                airdInfo.compressors.Add(Compressor.FromProto(compressor));
            }
            foreach (var instrument in proto.Instruments)
            {
                airdInfo.instruments.Add(Instrument.FromProto(instrument));
            } 
            foreach (var dataProcessing in proto.DataProcessings)
            {
                airdInfo.dataProcessings.Add(DataProcessing.FromProto(dataProcessing));
            } 
            foreach (var software in proto.Softwares)
            {
                airdInfo.softwares.Add(Software.FromProto(software));
            } 
            foreach (var parentFile in proto.ParentFiles)
            {
                airdInfo.parentFiles.Add(ParentFile.FromProto(parentFile));
            }
            foreach (var windowRange in proto.RangeList)
            {
                airdInfo.rangeList.Add(WindowRange.FromProto(windowRange));
            } 
            foreach (var blockIndex in proto.IndexList)
            {
                airdInfo.indexList.Add(BlockIndex.FromProto(blockIndex));
            }

            return airdInfo;
        }
    }
}