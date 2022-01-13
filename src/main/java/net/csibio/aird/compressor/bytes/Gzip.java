package net.csibio.aird.compressor.bytes;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.csibio.aird.util.FileUtil;

public class Gzip {

  static int BUFFER_SIZE = 8192;

  public static byte[] encode(byte[] data) {
    ByteArrayInputStream in = null;
    ByteArrayOutputStream out = null;
    GZIPOutputStream outputStream = null;
    try {
      in = new ByteArrayInputStream(data);
      out = new ByteArrayOutputStream();
      outputStream = new GZIPOutputStream(out);

      int size;
      final byte[] buf = new byte[BUFFER_SIZE];
      while ((size = in.read(buf)) != -1) {
        outputStream.write(buf, 0, size);
      }
      outputStream.finish();
      return out.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.close(outputStream);
      FileUtil.close(in);
      FileUtil.close(out);
    }
    return null;
  }

  public static byte[] decode(byte[] data) {
    return decode(data, 0, data.length);
  }

  public static byte[] decode(byte[] data, int start, int length) {
    ByteArrayInputStream in = new ByteArrayInputStream(data, start, length);
    BufferedInputStream inBuffer = new BufferedInputStream(in);
    ByteArrayOutputStream out = null;
    GZIPInputStream inputStream = null;
    try {
      out = new ByteArrayOutputStream();
      inputStream = new GZIPInputStream(inBuffer);
      int size;
      byte[] buf = new byte[BUFFER_SIZE];
      while ((size = inputStream.read(buf)) != -1) {
        out.write(buf, 0, size);
      }
      return out.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.close(inputStream);
      FileUtil.close(out);
    }
    return null;
  }
}
