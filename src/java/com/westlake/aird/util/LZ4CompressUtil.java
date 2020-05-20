package com.westlake.aird.util;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LZ4CompressUtil {

    private static final LZ4Factory factory = LZ4Factory.fastestInstance();


    public static byte[] transToByte(float[] target){
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        // 第二个参数为xz压缩等级
        byte[] compressed = LZ4Compress(bbTarget.array());
        return compressed;
    }

    public static float[] transToFloat(byte[] target){
        byte[] decompressed = LZ4Decompress(target);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decompressed);
        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }

        byteBuffer.clear();
        return floatValues;
    }

    public static byte[] LZ4Compress(byte[] target){
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLen = compressor.maxCompressedLength(target.length);
        byte[] compressed = compressor.compress(target);
        return compressed;
    }

    public static byte[] LZ4Decompress(byte[] target){
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] buf = new byte[target.length * 100];
        int len = decompressor.decompress( target, 0, target.length, buf, 0);

        return Arrays.copyOfRange(buf, 0, len);

    }


    /*
    public static void main(String[] args) {
        File indexFile = new File("D:\\Propro\\projet\\data\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");

        System.out.println(indexFile.getAbsolutePath());
        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

        SwathIndex index = swathIndexList.get(10);
        Float rt = index.getRts().get(1000);
        //intensity -> zlib     mz -> fastpfor
        MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
        float[] intensity = pairs.getIntensityArray();
        System.out.println("intensity length: " + intensity.length * 4);
        byte[] compressed = transToByte(intensity);
        System.out.println("compressed length: " + compressed.length);
        float[] decompressed = transToFloat(compressed);
        float mse = 0;
        for (int i = 0; i < intensity.length; i++) {
            mse += Math.pow((intensity[i] - decompressed[i]), 2);
        }
        System.out.println(String.format("mse = %f", mse));

    }
     */
}
