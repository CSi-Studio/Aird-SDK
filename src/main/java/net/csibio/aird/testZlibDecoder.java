package net.csibio.aird;

import net.csibio.aird.util.CompressUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;

import static net.csibio.aird.util.CompressUtil.fastPforEncoder;

public class testZlibDecoder {
    public static void main(String[] args) throws IOException, DataFormatException {
        byte[] arr = new byte[2000 + (int) (Math.random() * 100)];
        arr[0] = 0;
        for (int j = 1; j < arr.length; j++) {
            arr[j] = (byte) (arr[j - 1] + (byte) (Math.random() * 10));
        }
        byte[] comArr = CompressUtil.zlibEncoder(arr);
        byte[] decArr = CompressUtil.zlibDecoderLongArray(comArr);
        boolean a =Arrays.equals(arr, decArr);

        System.out.println("对照压缩前后数组是否相同" + a);
    }
}
