﻿/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System.Collections.Generic;

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
        public List<string> activators = new List<string>();

        /**
         * Every Chromatogram's energy in the block
         * 所有该块中的energy列表
         */
        public List<float> energies = new List<float>();

        /**
         * Every Chromatogram's polarity in the block
         * 所有该块中的polarity列表
         */
        public List<string> polarities = new List<string>();

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
        public List<int> nums = new List<int>();

        /**
         * 一个块中所有子图的rt的压缩后的大小列表
         */
        public List<int> rts = new List<int>();

        /**
         * 一个块中所有子图的intensity的压缩后的大小列表
         */
        public List<int> ints = new List<int>();

        /**
         * PSI CV
         * PSI可控词汇表
         */
        public List<List<CV>> cvs = new List<List<CV>>();

        /**
         * Features of every index
         * 用于存储KV键值对
         */
        public string features;


        public ChromatogramIndex()
        {
        }
    }
}