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

namespace AirdSDK.Compressor;

public class ByteCompressor
{
    private ByteCompType byteCompType;

    public ByteCompressor(ByteCompType type)
    {
        this.byteCompType = type;
    }

    public byte[] encode(byte[] bytes)
    {
        switch (byteCompType)
        {
            case ByteCompType.Zlib: return new ZlibWrapper().encode(bytes);
            case ByteCompType.Snappy: return new SnappyWrapper().encode(bytes);
            case ByteCompType.Brotli: return new BrotliWrapper().encode(bytes);
            case ByteCompType.Zstd: return new ZstdWrapper().encode(bytes);
            default: return null;
        }
    }

    public byte[] decode(byte[] bytes)
    {
        return decode(bytes, 0, bytes.Length);
    }

    /**
     * decompress the data with zlib at a specified start and length
     *
     * @param bytes  data to be decoded
     * @param start  the start position of the data array
     * @param length the length for compressor to decode
     * @return decompressed data
     */
    public byte[] decode(byte[] bytes, int start, int length)
    {
        switch (byteCompType)
        {
            case ByteCompType.Zlib: return new ZlibWrapper().decode(bytes, start, length);
            case ByteCompType.Snappy: return new SnappyWrapper().decode(bytes, start, length);
            case ByteCompType.Brotli: return new BrotliWrapper().decode(bytes, start, length);
            case ByteCompType.Zstd: return new ZstdWrapper().decode(bytes, start, length);
            default: return null;
        }
    }
}