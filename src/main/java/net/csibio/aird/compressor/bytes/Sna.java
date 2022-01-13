package net.csibio.aird.compressor.bytes;

import java.io.IOException;
import org.xerial.snappy.Snappy;

public class Sna {

  public static byte[] encode(byte[] data) {
    try {
      return Snappy.compress(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] decode(byte[] data) {
    try {
      return Snappy.uncompress(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
