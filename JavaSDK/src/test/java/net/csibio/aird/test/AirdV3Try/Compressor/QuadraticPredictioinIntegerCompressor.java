package net.csibio.aird.test.AirdV3Try.Compressor;

import java.util.ArrayList;
import java.util.List;

public class QuadraticPredictioinIntegerCompressor {
    public static int[] encode(int[] data) {
        int[] encoded = new int[data.length];
        int prev1 = 0, prev2 = 0;
        for (int i = 0; i < data.length; i++) {
            int predicted = getQuadraticPrediction(prev1, prev2);
            int error = data[i] - predicted;
            encoded[i] = error;
            prev2 = prev1;
            prev1 = data[i];
        }
        return encoded;
    }

    public static int[] decode(int[] encoded) {
        int[] decoded = new int[encoded.length];
        int prev1 = 0, prev2 = 0;
        for (int i = 0; i < encoded.length; i++) {
            int predicted = getQuadraticPrediction(prev1, prev2);
            int error = encoded[i];
            decoded[i] = predicted + error;
            prev2 = prev1;
            prev1 = decoded[i];
        }
        return decoded;
    }

    private static int getQuadraticPrediction(int prev1, int prev2) {
        return prev1 * 2 - prev2;
    }
}
