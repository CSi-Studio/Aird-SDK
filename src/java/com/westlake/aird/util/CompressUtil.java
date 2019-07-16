package com.westlake.aird.util;

import com.westlake.aird.bean.Compressor;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.differential.IntegratedBinaryPacking;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import me.lemire.integercompression.differential.SkippableIntegratedComposition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Inflater;

public class CompressUtil {

    public static byte[] zlibDecoder(byte[] data) {
        byte[] output = null;

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[2048];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    public static int[] fastPForDecoder(int[] sortedInt) {
        SkippableIntegratedComposition codec = new SkippableIntegratedComposition(new IntegratedBinaryPacking(), new IntegratedVariableByte());
        int size = sortedInt[0];
        // output vector should be large enough...
        int[] recovered = new int[size];
        IntWrapper inPoso = new IntWrapper(1);
        IntWrapper outPoso = new IntWrapper(0);
        IntWrapper recoffset = new IntWrapper(0);
        codec.headlessUncompress(sortedInt, inPoso, sortedInt.length, recovered, recoffset, size, outPoso);

        return recovered;
    }

    public static Compressor getMzCompressor(List<Compressor> compressors){
        if(compressors == null){
            return null;
        }
        for(Compressor compressor : compressors){
            if (compressor.getTarget().equals(Compressor.TARGET_MZ)){
                return compressor;
            }
        }
        return null;
    }

    public static Compressor getIntCompressor(List<Compressor> compressors){
        if(compressors == null){
            return null;
        }
        for(Compressor compressor : compressors){
            if (compressor.getTarget().equals(Compressor.TARGET_INTENSITY)){
                return compressor;
            }
        }
        return null;
    }

}
