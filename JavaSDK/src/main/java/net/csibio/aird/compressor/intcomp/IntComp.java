/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.intcomp;

/**
 * Integer compressor interface
 */
public interface IntComp {

    /**
     * Compresses the given byte array.
     *
     * @return the Integer compressor name
     */
    String getName();

    /**
     * Decompresses the given byte array.
     *
     * @param uncompressed the sorted integers
     * @return the compressed data
     */
    int[] encode(int[] uncompressed);

    /**
     * Decompresses the given byte array.
     *
     * @param compressed the compressed sorted integers
     * @return the decompressed data
     */
    int[] decode(int[] compressed);
}
