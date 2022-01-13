package net.csibio.aird.compressor.bytes;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.csibio.aird.util.FileUtil;

public class Zlib {

  public static byte[] encode(byte[] data) {
    Deflater compressor = new Deflater();
    compressor.reset();
    compressor.setInput(data);
    compressor.finish();
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
    try {
      byte[] buf = new byte[1024];
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

    return null;
  }

  public static byte[] decode(byte[] data) {
    return decode(data, 0, data.length);
  }

  public static byte[] decode(byte[] data, int start, int length) {
    Inflater inflater = new Inflater();
    inflater.setInput(data, start, length);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    try {
      byte[] buffer = new byte[10240];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        outputStream.write(buffer, 0, count);
      }
      return outputStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.close(outputStream);
    }
    return null;
  }
}
