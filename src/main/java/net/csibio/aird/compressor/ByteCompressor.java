package net.csibio.aird.compressor;

import net.csibio.aird.compressor.bytes.Brotli;
import net.csibio.aird.compressor.bytes.Snappier;
import net.csibio.aird.compressor.bytes.ZSTD;
import net.csibio.aird.compressor.bytes.Zlib;

public class ByteCompressor {

  CompressorType compressorType;

  public ByteCompressor(CompressorType type) {
    this.compressorType = type;
  }

  public byte[] encode(byte[] bytes) {
    return switch (compressorType) {
      case Zlib -> Zlib.encode(bytes);
      case Snappy -> Snappier.encode(bytes);
      case Brotli -> Brotli.encode(bytes);
      case ZSTD -> ZSTD.encode(bytes);
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
    return switch (compressorType) {
      case Zlib -> Zlib.decode(bytes, start, length);
      case Snappy -> Snappier.decode(bytes, start, length);
      case Brotli -> Brotli.decode(bytes, start, length);
      case ZSTD -> ZSTD.decode(bytes, start, length);
      default -> null;
    };
  }
}
