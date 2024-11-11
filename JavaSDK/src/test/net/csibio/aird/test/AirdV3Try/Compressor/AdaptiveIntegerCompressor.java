package net.csibio.aird.test.AirdV3Try.Compressor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptiveIntegerCompressor {
    public static int[] encode(int[] data) {
        int[] freqMap = new int[data.length];
        // 统计差分值出现的频率
        for (int diff : data) {
            freqMap[Math.abs(diff)]++;
        }

        int[] encoded = new int[data.length * 2];
        int index = 0;
        for (int diff : data) {
            int length = String.valueOf(Math.abs(freqMap[Math.abs(diff)])).length();
            encoded[index++] = length;
            encoded[index++] = diff;
        }
        return encoded;
    }

    public static int[] decode(int[] encoded) {
        int[] decoded = new int[encoded.length / 2];
        for (int i = 0, j = 0; i < encoded.length; i += 2, j++) {
            int length = encoded[i];
            int diff = encoded[i + 1];
            decoded[j] = diff;
        }
        return decoded;
    }
}
