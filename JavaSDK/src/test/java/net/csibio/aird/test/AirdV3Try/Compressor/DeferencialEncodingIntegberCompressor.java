package net.csibio.aird.test.AirdV3Try.Compressor;

import java.util.ArrayList;
import java.util.List;

public class DeferencialEncodingIntegberCompressor {
    public static int[] encode(int[] data) {
        int[] compressed = new int[data.length];
        int prev = 0;
        for (int i = 0; i < data.length; i++) {
            int diff = data[i] - prev;
            compressed[i] = diff;
            prev = data[i];
        }
        return compressed;
    }

    public static int[] decode(int[] compressed) {
        int[] decompressed = new int[compressed.length];
        int prev = 0;
        for (int i = 0; i < compressed.length; i++) {
            int diff = compressed[i];
            decompressed[i] = prev + diff;
            prev = decompressed[i];
        }
        return decompressed;
    }
}
