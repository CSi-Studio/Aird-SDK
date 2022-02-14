package net.csibio.aird.compressor.bytes;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.csibio.aird.util.FileUtil;

public class Zlib {

  static int BUFFER_SIZE = 2048;

  public static byte[] encode(byte[] input) {
    Deflater compressor = new Deflater();
    compressor.reset();
    compressor.setInput(input);
    compressor.finish();
    ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
    try {
      byte[] buf = new byte[BUFFER_SIZE];
      while (!compressor.finished()) {
        int i = compressor.deflate(buf);
        bos.write(buf, 0, i);
      }
      compressor.finish();
      return bos.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.close(bos);
      compressor.end();
    }

    return new byte[0];
  }

  public static byte[] decode(byte[] input) {
    return decode(input, 0, input.length);
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    Inflater inflater = new Inflater();
    inflater.setInput(input, offset, length);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
    try {
      byte[] buffer = new byte[10240];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        outputStream.write(buffer, 0, count);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.close(outputStream);
    }
    inflater.end();
    return outputStream.toByteArray();

  }
}
