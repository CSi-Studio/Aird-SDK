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
using AirdSDK.Enums;

namespace AirdSDK.Compressor
{
    public abstract class ByteComp : BaseComp<byte>
    {
        public static ByteComp build(ByteCompType type)
        {
            switch (type)
            {
                case ByteCompType.Brotli:
                    return new BrotliWrapper();
                case ByteCompType.Snappy:
                    return new SnappyWrapper();
                case ByteCompType.Zstd:
                    return new ZstdWrapper();
                case ByteCompType.Zlib:
                    return new ZlibWrapper();
                default: throw new System.Exception("No Implementation for " + type);
            }
        }

        public static ByteCompType getType(string type)
        {
            switch (type)
            {
                case "Brotli":
                    return ByteCompType.Brotli;
                case "Snappy":
                    return ByteCompType.Snappy;
                case "Zstd":
                    return ByteCompType.Zstd;
                case "Zlib":
                    return ByteCompType.Zlib;
                default: throw new System.Exception("No Implementation for " + type);
            }
        }

        public abstract byte[] decode(byte[] input, int offset, int length);
    }
}