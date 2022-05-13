package net.csibio.aird.compressor;

import net.csibio.aird.compressor.bytecomp.BrotliWrapper;
import net.csibio.aird.compressor.bytecomp.SnappyWrapper;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.enums.ByteCompType;

public class ByteCompressor {

    ByteCompType byteCompType;

    public ByteCompressor(ByteCompType type) {
        this.byteCompType = type;
    }

    public byte[] encode(byte[] bytes) {
        return switch (byteCompType) {
            case Zlib -> new ZlibWrapper().encode(bytes);
            case Snappy -> new SnappyWrapper().encode(bytes);
            case Brotli -> new BrotliWrapper().encode(bytes);
            case Zstd -> new ZstdWrapper().encode(bytes);
            default -> null;
        };
    }

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
