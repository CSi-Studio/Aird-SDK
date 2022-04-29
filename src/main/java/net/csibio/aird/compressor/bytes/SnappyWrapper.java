package net.csibio.aird.compressor.bytes;

import java.io.IOException;
import java.util.Arrays;

import net.csibio.aird.enums.ByteCompType;
import org.xerial.snappy.Snappy;

public class SnappyWrapper implements ByteComp {

  @Override
  public String getName() {
    return ByteCompType.Snappy.getName();
  }

  @Override
  public byte[] encode(byte[] input) {
    try {
      return Snappy.compress(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public byte[] decode(byte[] input) {
    try {
      return Snappy.uncompress(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public byte[] decode(byte[] input, int offset, int length) {
    return decode(Arrays.copyOfRange(input, offset, offset + length));
  }
}
