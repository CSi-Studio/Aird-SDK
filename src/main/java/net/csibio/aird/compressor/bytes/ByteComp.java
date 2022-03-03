package net.csibio.aird.compressor.bytes;

public interface ByteComp {

  byte[] encode(byte[] uncompressed);

  byte[] decode(byte[] compressed);
  
  byte[] decode(byte[] input, int offset, int length);
}
