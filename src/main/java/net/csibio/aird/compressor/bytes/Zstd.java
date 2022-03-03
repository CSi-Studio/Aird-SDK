package net.csibio.aird.compressor.bytes;

import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdDictDecompress;
import java.util.Arrays;

public class Zstd implements ByteComp {

  public byte[] encode(byte[] input) {
    return com.github.luben.zstd.Zstd.compress(input);
  }

  public byte[] encode(byte[] input, byte[] dict) {
    return com.github.luben.zstd.Zstd.compress(input, new ZstdDictCompress(dict, 3));
  }

  public byte[] decode(byte[] input) {
    int size = (int) com.github.luben.zstd.Zstd.decompressedSize(input);
    byte[] array = new byte[size];
    com.github.luben.zstd.Zstd.decompress(array, input);
    return array;
  }

  public byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }

  public byte[] decode(byte[] input, byte[] dict) {
    int size = (int) com.github.luben.zstd.Zstd.decompressedSize(input);
    return com.github.luben.zstd.Zstd.decompress(input, new ZstdDictDecompress(dict), size);
  }

  public byte[] decode(byte[] input, int offset, int length, byte[] dict) {
    return decode(Arrays.copyOfRange(input, offset, offset + length), dict);
  }
}
