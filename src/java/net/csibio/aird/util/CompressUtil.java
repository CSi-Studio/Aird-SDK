/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import net.csibio.aird.bean.Compressor;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.differential.IntegratedBinaryPacking;
import me.lemire.integercompression.differential.IntegratedVariableByte;
import me.lemire.integercompression.differential.SkippableIntegratedComposition;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressUtil {

    /**
     * compress the data with zlib algorithm
     *
     * @param data
     * @return compressed data
     */
    public static byte[] zlibEncoder(byte[] data) {
        byte[] output;

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    /**
     * decompress the data with zlib
     * @param data
     * @return decompressed data
     */
    public static byte[] zlibDecoder(byte[] data) {
        Inflater decompresser = new Inflater();
        decompresser.setInput(data);
        byte[] decompressedData = new byte[data.length * 100];
        int i;

        try {
            i = decompresser.inflate(decompressedData);
            decompressedData = ArrayUtils.subarray(decompressedData, 0, i);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return decompressedData;
    }

    /**
     * decompress the data with zlib at a specified start and length
     * @param data
     * @param start
     * @param length
     * @return decompressed data
     */
    public static byte[] zlibDecoder(byte[] data, int start, int length) {
        Inflater decompresser = new Inflater();
        decompresser.setInput(data, start, length);
        byte[] decompressedData = new byte[length * 10];
        int i;

        try {
            i = decompresser.inflate(decompressedData);
            decompressedData = ArrayUtils.subarray(decompressedData, 0, i);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return decompressedData;
    }

    /**
     * decompress the data with fastpfor algorithm
     * @param compressedInts
     * @return decompressed data
     */
    public static int[] fastPforDecoder(int[] compressedInts) {
        SkippableIntegratedComposition codec = new SkippableIntegratedComposition(new IntegratedBinaryPacking(), new IntegratedVariableByte());
        int size = compressedInts[0];
        // output vector should be large enough...
        int[] recovered = new int[size];
        IntWrapper inPoso = new IntWrapper(1);
        IntWrapper outPoso = new IntWrapper(0);
        IntWrapper recoffset = new IntWrapper(0);
        codec.headlessUncompress(compressedInts, inPoso, compressedInts.length, recovered, recoffset, size, outPoso);

        return recovered;
    }

    /**
     * compress the data with fastpfor algorithm
     * @param sortedInt
     * @return compressed data
     */
    public static int[] fastPforEncoder(int[] sortedInt) {
        SkippableIntegratedComposition codec = new SkippableIntegratedComposition(new IntegratedBinaryPacking(), new IntegratedVariableByte());
        int[] compressed = new int[sortedInt.length + 1024];
        IntWrapper inputoffset = new IntWrapper(0);
        IntWrapper outputoffset = new IntWrapper(1);
        codec.headlessCompress(sortedInt, inputoffset, sortedInt.length, compressed, outputoffset, new IntWrapper(0));
        compressed[0] = sortedInt.length;
        compressed = Arrays.copyOf(compressed, outputoffset.intValue());

        return compressed;
    }

    /**
     * get the compressor for m/z
     * @param compressors
     * @return the m/z compressor
     */
    public static Compressor getMzCompressor(List<Compressor> compressors) {
        if (compressors == null) {
            return null;
        }
        for (Compressor compressor : compressors) {
            if (compressor.getTarget().equals(Compressor.TARGET_MZ)) {
                return compressor;
            }
        }
        return null;
    }

    /**
     * get the intensity compressor for intensity
     * @param compressors
     * @return the intensity compressor
     */
    public static Compressor getIntCompressor(List<Compressor> compressors) {
        if (compressors == null) {
            return null;
        }
        for (Compressor compressor : compressors) {
            if (compressor.getTarget().equals(Compressor.TARGET_INTENSITY)) {
                return compressor;
            }
        }
        return null;
    }

    /**
     * compress a float array with zlib and convert the binary data into string with Base64 algorithm
     * @param target
     * @return base64 string
     */
    public static String transToString(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        byte[] targetArray = bbTarget.array();
        byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
        String targetStr = new String(new Base64().encode(compressedArray));
        return targetStr;
    }

    /**
     * compress an integer array with zlib and convert the binary data into string with Base64 algorithm
     * @param target
     * @return base64 string
     */
    public static String transToString(int[] target) {
        IntBuffer ibTarget = IntBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
        bbTarget.asIntBuffer().put(ibTarget);
        byte[] targetArray = bbTarget.array();
        byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
        String targetStr = new String(new Base64().encode(compressedArray));
        return targetStr;
    }

    /**
     * compress the int array with zlib algorithm
     * @param target
     * @return compressed data
     */
    public static byte[] transToByte(int[] target) {
        IntBuffer ibTarget = IntBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
        bbTarget.asIntBuffer().put(ibTarget);
        byte[] targetArray = bbTarget.array();
        byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
        return compressedArray;
    }

    /**
     * compress the float array with zlib algorithm
     * @param target
     * @return compressed data
     */
    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        byte[] targetArray = bbTarget.array();
        byte[] compressedArray = CompressUtil.zlibEncoder(targetArray);
        return compressedArray;
    }

    /**
     * decompress the binary data with zlib algorithm
     * @param value
     * @return decompressed data
     */
    public static float[] transToFloat(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(value);
        byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(byteBuffer.array()));

        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }

        byteBuffer.clear();
        return floatValues;
    }

    /**
     * decompress the binary data with zlib algorithm
     * @param value
     * @return decompressed data
     */
    public static int[] transToInteger(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(value);
        byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(byteBuffer.array()));

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }

        byteBuffer.clear();
        return intValues;
    }
}
