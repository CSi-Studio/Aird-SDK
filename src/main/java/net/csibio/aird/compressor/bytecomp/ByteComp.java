package net.csibio.aird.compressor.bytecomp;

public interface ByteComp {

    String getName();

    byte[] encode(byte[] uncompressed);

    byte[] decode(byte[] compressed);

    byte[] decode(byte[] input, int offset, int length);
}
