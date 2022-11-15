/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using CSharpFastPFOR;

namespace AirdSDK.Compressor
{
    public class BinPackingWrapper : IntComp
    {
        //使用BinaryPacking算法将未排序的int数组进行压缩
        public override string getName()
        {
            return IntCompType.BP.ToString();
        }

        public override int[] encode(int[] uncompressed)
        {
            int[] compressedInts = new IntCompressor().compress(uncompressed);
            return compressedInts;
        }

        //使用BinaryPacking算法对已经压缩的int数组进行解压缩
        public override int[] decode(int[] compressed)
        {
            int[] sortedInts = new IntCompressor().uncompress(compressed);
            return sortedInts;
        }
    }
}