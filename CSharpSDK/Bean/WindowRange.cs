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

namespace AirdSDK.Beans
{
    public class WindowRange
    {
        //前体的荷质比窗口开始位置,已经经过ExperimentDO.overlap参数调整,precursor mz
        public double start;

        //前体的荷质比窗口结束位置,已经经过ExperimentDO.overlap参数调整,precursor mz
        public double end;

        //前体的荷质比,precursor mz
        public double mz;

        //前体的带电量,大多情况为0,表示未知
        public int? charge;
        #nullable enable
        public string? features;

        public WindowRange()
        {
        }

        public WindowRange(double start, double end, double mz)
        {
            this.start = start;
            this.end = end;
            this.mz = mz;
        }

        public WindowRangeProto ToProto()
        {
            WindowRangeProto proto = new()
            {
                Start = this.start,
                End = this.end,
                Mz = this.mz,
            };
            if (this.features != null)
            {
                proto.Features = features;
            }

            if (this.charge != null)
            {
                proto.Charge = this.charge.Value;
            }
            return proto;
        }
        
        public static WindowRange FromProto(WindowRangeProto proto)
        {
            if (proto == null)
                throw new ArgumentNullException(nameof(proto));

            return new WindowRange
            {
                start = proto.Start,
                end = proto.End,
                mz = proto.Mz,
                charge = proto.Charge,
                features = proto.Features
            };
        }
    }
}