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
using CSharpFastPFOR.Differential;

namespace AirdSDK.Compressor;

public class IntegratedXVByte
{
    /**
   * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
   * sorted integers.
   *
   * @param sortedInts the sorted integers
   * @return the compressed data
   */
    public byte[] encode(int[] sortedInts)
    {
        return encode(sortedInts, ByteCompType.Zlib);
    }

    /**
     * ZDPD Encoder
     *
     * @param sortedInts the sorted integers
     * @return the compressed data
     */
    public byte[] encode(int[] sortedInts, ByteCompType byteCompType)
    {
        int[] compressedInts = new IntegratedIntCompressor(new IntegratedVariableByte()).compress(sortedInts);
        byte[] bytes = ByteTrans.intToByte(compressedInts);
        return new ByteCompressor(byteCompType).encode(bytes);
    }

    /**
     * @param sortedFloats
     * @param precision
     * @param compType
     * @return
     */
    public byte[] encode(double[] sortedFloats, double precision, ByteCompType compType)
    {
        int[] sortedInts = new int[sortedFloats.Length];
        for (int i = 0; i < sortedFloats.Length; i++)
        {
            sortedInts[i] = (int) (precision * sortedFloats[i]);
        }

        return encode(sortedInts, compType);
    }

    public int[] decode(byte[] bytes)
    {
        return decode(bytes, ByteCompType.Zlib);
    }

    public int[] decode(byte[] bytes, ByteCompType type)
    {
        byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
        int[] zipInts = ByteTrans.byteToInt(decodeBytes);
        int[] sortedInts = new IntegratedIntCompressor(new IntegratedVariableByte()).uncompress(zipInts);
        return sortedInts;
    }
}