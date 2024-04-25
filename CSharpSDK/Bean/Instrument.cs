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
using System.Linq;

namespace AirdSDK.Beans
{
    /**
     * General information about the MS instrument.
     */
    public class Instrument
    {
        //设备仪器厂商
        public string manufacturer;

        //电离方式 Ionisation Method
        public string ionisation;

        //仪器分辨率 The Instrument Resolution
        public string resolution;

        //设备类型
        public string model;

        //来源 source
        public List<string> source = new();

        //分析方式
        public List<string> analyzer = new();

        //探测器
        public List<string> detector = new();

        public InstrumentProto ToProto()
        {
            InstrumentProto proto = new InstrumentProto();
            if (source != null && source.Count > 0)
            {
                proto.Source.AddRange(source);
            }

            if (analyzer != null && analyzer.Count > 0)
            {
                proto.Analyzer.AddRange(analyzer);
            }

            if (detector != null && detector.Count > 0)
            {
                proto.Detector.AddRange(detector);
            }
            if (this.manufacturer != null)
            {
                proto.Manufacturer = manufacturer;
            }
            if (this.ionisation != null)
            {
                proto.Ionisation = ionisation;
            } 
            if (this.resolution != null)
            {
                proto.Resolution = resolution;
            }
            if (this.model != null)
            {
                proto.Model = model;
            }
            return proto;
        }
        
        public static Instrument FromProto(InstrumentProto proto)
        {
            if (proto == null)
                throw new ArgumentNullException(nameof(proto));

            Instrument instrument = new Instrument()
            {
                manufacturer = proto.Manufacturer,
                ionisation = proto.Ionisation,
                resolution = proto.Resolution,
                model = proto.Model,
                source = proto.Source.ToList(),
                analyzer = proto.Analyzer.ToList(),
                detector = proto.Detector.ToList(),
            };

            return instrument;
        }
    }
}