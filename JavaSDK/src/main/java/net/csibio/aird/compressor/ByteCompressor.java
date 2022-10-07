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

import net.csibio.aird.compressor.bytecomp.BrotliWrapper;
import net.csibio.aird.compressor.bytecomp.SnappyWrapper;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.enums.ByteCompType;

/**
 * Byte Compressor implementation
 */
public class ByteCompressor {

    ByteCompType byteCompType;

    /**
     * @param type the type of the compressor
     */
    public ByteCompressor(ByteCompType type) {
        this.byteCompType = type;
    }

    /**
     * Compresses a byte array.
     *
     * @param bytes the bytes to compress
     * @return the compressed data
     */
    public byte[] encode(byte[] bytes) {
        return switch (byteCompType) {
            case Zlib -> new ZlibWrapper().encode(bytes);
            case Snappy -> new SnappyWrapper().encode(bytes);
            case Brotli -> new BrotliWrapper().encode(bytes);
            case Zstd -> new ZstdWrapper().encode(bytes);
            default -> null;
        };
    }

    /**
     * Decompresses a byte array.
     *
     * @param bytes the bytes
     * @return the compressed data
     */
    public byte[] decode(byte[] bytes) {
        return decode(bytes, 0, bytes.length);
    }

    /**
     * decompress the data with zlib at a specified start and length
     *
     * @param bytes  data to be decoded
     * @param start  the start position of the data array
     * @param length the length for compressor to decode
     * @return decompressed data
     */
    public byte[] decode(byte[] bytes, int start, int length) {
        return switch (byteCompType) {
            case Zlib -> new ZlibWrapper().decode(bytes, start, length);
            case Snappy -> new SnappyWrapper().decode(bytes, start, length);
            case Brotli -> new BrotliWrapper().decode(bytes, start, length);
            case Zstd -> new ZstdWrapper().decode(bytes, start, length);
            default -> null;
        };
    }
}
