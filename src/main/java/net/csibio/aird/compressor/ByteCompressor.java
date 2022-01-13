package net.csibio.aird.compressor;

import net.csibio.aird.compressor.bytes.Gzip;
import net.csibio.aird.compressor.bytes.LZMA2;
import net.csibio.aird.compressor.bytes.Sna;
import net.csibio.aird.compressor.bytes.Zlib;

public class ByteCompressor {

  CompressorType compressorType;

  public ByteCompressor(CompressorType type) {
    this.compressorType = type;
  }

  public byte[] encode(byte[] bytes) {
    return switch (compressorType) {
      case Zlib -> Zlib.encode(bytes);
      case LZMA2 -> LZMA2.encode(bytes);
      case Gzip -> Gzip.encode(bytes);
      case Sna -> Sna.encode(bytes);
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
      case LZMA2 -> LZMA2.decode(bytes, start, length);
      case Gzip -> Gzip.decode(bytes, start, length);
      default -> null;
    };
  }
}
