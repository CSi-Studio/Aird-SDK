/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

namespace AirdSDK.Beans
{
    public class Layers
    {
        /**
         * 使用fastPfor算法压缩以后的mz数组
         * compressed mz array with fastPfor
        */
        public byte[] mzArray;

        /**
         * mz对应的层索引
         * layer index of mz
         */
        public byte[] tagArray;

        /**
         * 存储单个索引所需的位数
         * storage digits occupied by an index
         */
        public int digit;

        public Layers()
        {
        }

        public Layers(byte[] mzArray, byte[] tagArray, int digit)
        {
            this.mzArray = mzArray;
            this.tagArray = tagArray;
            this.digit = digit;
        }
    }
}