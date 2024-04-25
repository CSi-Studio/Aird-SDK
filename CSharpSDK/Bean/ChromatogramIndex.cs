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
    public class ChromatogramIndex
    {
        /**
         * the total chromatograms count, exclude the TIC and BPC chromatograms
         */
        public long totalCount = 0;

        /**
         * acquisitionMethod
         */
        public string type;

        /**
         * 1:MS1;2:MS2
         */
        public List<string> ids = new();

        /**
         * 化合物名称列表
         */
        public List<string> compounds = new();
        
        /**
         * the block start position in the file
         * 在文件中的开始位置
         */
        public long startPtr;

        /**
         * the block end position in the file
         * 在文件中的结束位置
         */
        public long endPtr;

        /**
         * Every Chromatogram's activator in the block
         * 所有该块中的activator列表
         */
        public List<string> activators = new();

        /**
         * Every Chromatogram's energy in the block
         * 所有该块中的energy列表
         */
        public List<float> energies = new();

        /**
         * Every Chromatogram's polarity in the block
         * 所有该块中的polarity列表
         */
        public List<string> polarities = new();

        /**
         * The precursor ion list
         */
        public List<WindowRange> precursors = new();

        /**
         * The product ion list
         */
        public List<WindowRange> products = new();

        /**
         * chromatogram num list
         * 谱图序列号
         */
        public List<int> nums = new();

        /**
         * 一个块中所有子图的rt的压缩后的大小列表
         */
        public List<int> rts = new();

        /**
         * 一个块中所有子图的intensity的压缩后的大小列表
         */
        public List<int> ints = new();

        /**
         * Features of every index
         * 用于存储KV键值对
         */
        public string features;


        public ChromatogramIndex()
        {
        }
        
        public ChromatogramIndexProto ToProto()
        {
            ChromatogramIndexProto proto = new ChromatogramIndexProto
            {
                TotalCount = this.totalCount,
                StartPtr = this.startPtr,
                EndPtr = this.endPtr,
                Ids = { this.ids },
                Nums = { this.nums },
                Rts = { this.rts },
                Ints = { this.ints },
                Compounds = { this.compounds },
                Activators = { this.activators },
                Energies = { this.energies },
                Polarities = { this.polarities }
            };
            if (type != null)
            {
                proto.Type = type;
            }  
            if (features != null)
            {
                proto.Features = features;
            }
            if (this.precursors != null && this.precursors.Count > 0)
            {
                List<WindowRangeProto> protos = new List<WindowRangeProto>();
                foreach (var precursor in this.precursors)
                {
                    protos.Add(precursor.ToProto());
                }

                proto.Precursors.AddRange(protos);
            } 
            if (this.products != null && this.products.Count > 0)
            {
                List<WindowRangeProto> protos = new List<WindowRangeProto>();
                foreach (var product in this.products)
                {
                    protos.Add(product.ToProto());
                }

                proto.Products.AddRange(protos);
            }
            return proto;
        }
        
        public static ChromatogramIndex FromProto(ChromatogramIndexProto proto)
        {
            if (proto == null)
                throw new ArgumentNullException(nameof(proto));

            var chromatogramIndex = new ChromatogramIndex
            {
                totalCount = proto.TotalCount,
                type = proto.Type,
                startPtr = proto.StartPtr,
                endPtr = proto.EndPtr,
                features = proto.Features,
                ids = proto.Ids.ToList(),
                compounds = proto.Compounds.ToList(),
                activators = proto.Activators.ToList(),
                energies = proto.Energies.ToList(),
                polarities = proto.Polarities.ToList(),
                nums = proto.Nums.ToList(),
                rts = proto.Rts.ToList(),
                ints = proto.Ints.ToList()
            };

            // 将protobuf中的WindowRange列表转换为WindowRange列表
            chromatogramIndex.precursors = new List<WindowRange>();
            if (proto.Precursors != null && proto.Precursors.Count > 0)
            {
                foreach (var precursorProto in proto.Precursors)
                {
                    chromatogramIndex.precursors.Add(WindowRange.FromProto(precursorProto));
                }
            }

            chromatogramIndex.products = new List<WindowRange>();
            if (proto.Products != null && proto.Products.Count > 0)
            {
                foreach (var productProto in proto.Products)
                {
                    chromatogramIndex.products.Add(WindowRange.FromProto(productProto));
                }
            }
            
            return chromatogramIndex;
        }
    }
}