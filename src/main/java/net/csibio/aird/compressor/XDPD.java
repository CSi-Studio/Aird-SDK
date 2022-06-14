/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor;

import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.enums.ByteCompType;

public class XDPD {

    /**
     * XDPD Encoder, default byte compressor is Zlib
     *
     * @param sortedInts the sorted integers
     * @return the compressed data
     */
    public static byte[] encode(int[] sortedInts) {
        return encode(sortedInts, ByteCompType.Zlib);
    }

    /**
     * ZDPD Encoder
     *
     * @param sortedInts the sorted integers
     * @return the compressed data
     */
    public static byte[] encode(int[] sortedInts, ByteCompType byteCompType) {
        int[] compressedInts = new IntegratedBinPackingWrapper().encode(sortedInts);
        byte[] bytes = ByteTrans.intToByte(compressedInts);
        return new ByteCompressor(byteCompType).encode(bytes);
    }

    public static byte[] encode(double[] sortedFloats, double precision, ByteCompType byteCompType) {
        int[] sortedInts = new int[sortedFloats.length];
        for (int i = 0; i < sortedFloats.length; i++) {
            sortedInts[i] = (int) (precision * sortedFloats[i]);
        }
        return encode(sortedInts, byteCompType);
    }

    public static byte[] encode(float[] sortedFloats, double precision, ByteCompType byteCompType) {
        int[] sortedInts = new int[sortedFloats.length];
        for (int i = 0; i < sortedFloats.length; i++) {
            sortedInts[i] = (int) (precision * sortedFloats[i]);
        }
        return encode(sortedInts, byteCompType);
    }

    public static int[] decode(byte[] bytes) {
        return decode(bytes, ByteCompType.Zlib);
    }

//  public static int[] decode(byte[] bytes, int precision) {
//    return decode(bytes, precision, CompressorType.Zlib);
//  }

    public static int[] decode(byte[] bytes, ByteCompType type) {
        byte[] decodeBytes = new ByteCompressor(type).decode(bytes);
        int[] zipInts = ByteTrans.byteToInt(decodeBytes);
        int[] sortedInts = new IntegratedBinPackingWrapper().decode(zipInts);
//    double[] sortedDouble = new double[sortedInts.length];
//    for (int i = 0; i < sortedInts.length; i++) {
//      sortedDouble[i] = sortedInts[i] / precision;
//    }
        return sortedInts;
    }
}
