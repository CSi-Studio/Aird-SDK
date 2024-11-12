package net.csibio.aird.test.AirdV3Try.Compressor;

import java.util.ArrayList;
import java.util.List;

public class PredictiveIntegerCompressor {
    /**
     * 对数据进行预测编码
     * @param data 原始整形数据序列
     * @return 预测编码后的数据序列
     */
    public static List<Integer> encode(int[] data) {
        List<Integer> encoded = new ArrayList<>();
        int prev = 0;
        for (int num : data) {
            int predicted = prev;
            int error = num - predicted;
            encoded.add(error);
            prev = num;
        }
        return encoded;
    }

    /**
     * 对预测编码数据进行解码
     * @param encoded 预测编码后的数据序列
     * @return 解码后的原始整形数据序列
     */
    public static int[] decode(List<Integer> encoded) {
        int[] decoded = new int[encoded.size()];
        int prev = 0;
        for (int i = 0; i < encoded.size(); i++) {
            int error = encoded.get(i);
            decoded[i] = prev + error;
            prev = decoded[i];
        }
        return decoded;
    }
}
