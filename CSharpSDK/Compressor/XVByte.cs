﻿/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using AirdSDK.Enums;
using CSharpFastPFOR;

namespace AirdSDK.Compressor;

public class XVByte
{
    /**
   * VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * integers.
   *
   * @param ints the integers array
   * @return the compressed data
   */
    public static byte[] encode(int[] ints)
    {
        return encode(ints, ByteCompType.Zlib);
    }

    /**
     * XDPD Encoder
     *
     * @param ints the integers array
     * @return the compressed data
     */
    public static byte[] encode(int[] ints, ByteCompType byteCompType)
    {
        int[] compressedInts = new IntCompressor(new VariableByte()).compress(ints);
        byte[] bytes = ByteTrans.intToByte(compressedInts);
        return new ByteCompressor(byteCompType).encode(bytes);
    }

    public static byte[] encode(double[] floats, double precision, ByteCompType compType)
    {
        int[] ints = new int[floats.Length];
        for (int i = 0; i < floats.Length; i++)
        {
            ints[i] = (int) (precision * floats[i]);
        }

        return encode(ints, compType);
    }

    public static int[] decode(byte[] bytes)
    {
        return decode(bytes, ByteCompType.Zlib);
    }

    public static int[] decode(byte[] bytes, ByteCompType type)
    {
        byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
        int[] zipInts = ByteTrans.byteToInt(decodeBytes);
        int[] ints = new IntCompressor(new VariableByte()).uncompress(zipInts);
        return ints;
    }
}