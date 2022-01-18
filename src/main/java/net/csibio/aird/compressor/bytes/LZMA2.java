package net.csibio.aird.compressor.bytes;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.csibio.aird.util.FileUtil;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

public class LZMA2 {

  static int BUFFER_SIZE = 8192;

  public static byte[] encode(byte[] input) {
    ByteArrayInputStream in = null;
    ByteArrayOutputStream out = null;
    XZOutputStream outputStream = null;
    try {
      in = new ByteArrayInputStream(input);
      out = new ByteArrayOutputStream();
      outputStream = new XZOutputStream(out, new LZMA2Options(6));

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

  public static byte[] decode(byte[] input) {
    return decode(input, 0, input.length);
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    ByteArrayInputStream in = new ByteArrayInputStream(input, offset, length);
    BufferedInputStream inBuffer = new BufferedInputStream(in);
    ByteArrayOutputStream out = null;
    InputStream inputStream = null;
    try {
      out = new ByteArrayOutputStream();
      inputStream = new XZInputStream(inBuffer);
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
