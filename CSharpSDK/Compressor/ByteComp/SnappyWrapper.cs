/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using AirdSDK.Enums;
using IronSnappy;
using System;

namespace AirdSDK.Compressor
{
    public class SnappyWrapper : ByteComp
    {
        //使用zstd将byte数组压缩
        public override string getName()
        {
            return ByteCompType.Snappy.ToString();
        }

        public override byte[] encode(byte[] data)
        {
            byte[] compressed = Snappy.Encode(data);
            return compressed;
        }

        public override byte[] decode(byte[] data)
        {
            byte[] uncompressed = Snappy.Decode(data);
            return uncompressed;
        }

        public override byte[] decode(byte[] input, int offset, int length)
        {
            byte[] result = new byte[length];
            Array.Copy(input, offset, result, 0, length);
            byte[] uncompressed = Snappy.Decode(result);
            return uncompressed;
        }
    }
}