package net.csibio.aird.compressor.bytes;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.Decoder;
import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import com.aayushatharva.brotli4j.decoder.DirectDecompress;
import com.aayushatharva.brotli4j.encoder.Encoder;
import java.util.Arrays;

public class Brotli {

  public static byte[] encode(byte[] input) {
    Brotli4jLoader.ensureAvailability();
    try {
      return Encoder.compress(input);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] decode(byte[] input) {
    Brotli4jLoader.ensureAvailability();
    try {
      DirectDecompress directDecompress = Decoder.decompress(input);
      if (directDecompress.getResultStatus() == DecoderJNI.Status.DONE) {
        return directDecompress.getDecompressedData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] decode(byte[] input, int offset, int length) {
    Brotli4jLoader.ensureAvailability();
    try {
      DirectDecompress directDecompress = Decoder.decompress(
          Arrays.copyOfRange(input, offset, offset + length));
      if (directDecompress.getResultStatus() == DecoderJNI.Status.DONE) {
        return directDecompress.getDecompressedData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
