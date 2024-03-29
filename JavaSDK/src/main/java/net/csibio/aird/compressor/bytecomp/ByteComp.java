/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.bytecomp;

/**
 * Byte Compressor Interface
 */
public interface ByteComp {

    /**
     * Compresses a byte array
     *
     * @return the byte compressor name
     */
    String getName();

    /**
     * @param uncompressed uncompressed byte array
     * @return the compressed byte array
     */
    byte[] encode(byte[] uncompressed);

    /**
     * @param compressed compressed byte array
     * @return the compressed byte array
     */
    byte[] decode(byte[] compressed);

    /**
     * @param input  input byte array
     * @param offset offset byte
     * @param length length
     * @return the uncompressed byte array
     */
    byte[] decode(byte[] input, int offset, int length);
}
