/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor;

import net.csibio.aird.compressor.bytecomp.ByteComp;
import net.csibio.aird.compressor.intcomp.IntComp;
import net.csibio.aird.compressor.sortedintcomp.SortedIntComp;

/**
 * Combinable Compressors
 */
public class ComboComp {

    /**
     * encode method
     *
     * @param byteComp the byte compressor to encode
     * @param target   the target to encode
     * @return the compressed data
     */
    public static byte[] encode(ByteComp byteComp, int[] target) {
        return byteComp.encode(ByteTrans.intToByte(target));
    }

    /**
     * decode method
     *
     * @param byteComp the byte compressor
     * @param target   the target to decode
     * @return the decompressed data
     */
    public static int[] decode(ByteComp byteComp, byte[] target) {
        return ByteTrans.byteToInt(byteComp.decode(target));
    }

    /**
     * encode method
     *
     * @param intComp  the integer compressor
     * @param byteComp the byte compressor
     * @param target   the target to encode
     * @return the compressed data
     */
    public static byte[] encode(IntComp intComp, ByteComp byteComp, int[] target) {
        return byteComp.encode(ByteTrans.intToByte(intComp.encode(target)));
    }

    /**
     * decode method
     *
     * @param intComp  the integer compressor
     * @param byteComp the byte compressor
     * @param target   the target to encode
     * @return the compressed data
     */
    public static int[] decode(IntComp intComp, ByteComp byteComp, byte[] target) {
        return intComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
    }

    /**
     * encode method
     *
     * @param sortedIntComp the sorted integer compressor
     * @param byteComp      the byte compressor
     * @param target        the target to encode
     * @return the compressed data
     */
    public static byte[] encode(SortedIntComp sortedIntComp, ByteComp byteComp, int[] target) {
        return byteComp.encode(ByteTrans.intToByte(sortedIntComp.encode(target)));
    }

    /**
     * encode method
     *
     * @param sortedIntComp  the integer compressor
     * @param byteComp the byte compressor
     * @param target   the target to encode
     * @return the compressed data
     */
    public static byte[] encode(SortedIntComp sortedIntComp, ByteComp byteComp, double[] target, int precision) {
        int[] newTarget = new int[target.length];
        for (int i = 0; i < target.length; i++) {
            newTarget[i] = (int)Math.round(target[i]*precision);
        }

        return byteComp.encode(ByteTrans.intToByte(sortedIntComp.encode(newTarget)));
    }

    /**
     * decode method
     *
     * @param sortedIntComp the sorted integer compressor
     * @param byteComp      the byte compressor
     * @param target        the target to encode
     * @return the compressed data
     */
    public static int[] decode(SortedIntComp sortedIntComp, ByteComp byteComp, byte[] target) {
        return sortedIntComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
    }
}
