/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor;

import java.nio.*;

/**
 * type trans for different types
 */
public class ByteTrans {

    /**
     * short to byte conversion
     *
     * @param shorts the short data to convert
     * @return the converted data
     */
    public static byte[] shortToByte(short[] shorts) {
        ShortBuffer fbTarget = ShortBuffer.wrap(shorts);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 2);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asShortBuffer().put(fbTarget);
        return bbTarget.array();
    }

    /**
     * int to byte conversion
     *
     * @param ints the integers array
     * @return the converted data
     */
    public static byte[] intToByte(int[] ints) {
        IntBuffer ibTarget = IntBuffer.wrap(ints);
        ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asIntBuffer().put(ibTarget);
        return bbTarget.array();
    }

    /**
     * double to Float conversion
     *
     * @param doubles the doubles array
     * @return the converted data
     */
    public static float[] doubleToFloat(double[] doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = (float) doubles[i];
        }
        return floats;
    }

    /**
     * double to byte conversion
     *
     * @param doubles the doubles array to convert
     * @return the converted data
     */
    public static byte[] doubleToByte(double[] doubles) {
        DoubleBuffer fbTarget = DoubleBuffer.wrap(doubles);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 8);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asDoubleBuffer().put(fbTarget);
        return bbTarget.array();
    }

    /**
     * double to int conversion
     *
     * @param doubles the doubles array to convert
     * @return the converted data
     */
    public static int[] doubleToInt(double[] doubles, int precision) {
        int[] newArray = new int[doubles.length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = (int)Math.round(doubles[i]*precision);
        }
        return newArray;
    }

    /**
     * int to double conversion
     *
     * @param ints the int array to convert
     * @return the converted data
     */
    public static double[] intToDouble(int[] ints, double precision) {
        double[] newArray = new double[ints.length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = ints[i]/precision;
        }
        return newArray;
    }

    /**
     * float to Byte conversion
     *
     * @param floats the floats array
     * @return the converted data
     */
    public static byte[] floatToByte(float[] floats) {
        FloatBuffer fbTarget = FloatBuffer.wrap(floats);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

    /**
     * float to ByteBuffer conversion
     *
     * @param floats the floats array
     * @return the converted data
     */
    public static ByteBuffer floatToByteBuffer(float[] floats) {
        FloatBuffer fbTarget = FloatBuffer.wrap(floats);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget;
    }

    /**
     * byte to int conversion
     *
     * @param bytes the bytes array
     * @return the converted data
     */
    public static int[] byteToInt(byte[] bytes) {
        return byteToInt(bytes, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * byte to int conversion
     *
     * @param bytes the bytes array
     * @param order the target byte order
     * @return the converted data
     */
    public static int[] byteToInt(byte[] bytes, ByteOrder order) {
        order = (order == null ? ByteOrder.LITTLE_ENDIAN : order);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(order);
        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }

        byteBuffer.clear();
        return intValues;
    }

    /**
     * byte to Float conversion
     *
     * @param bytes the bytes array
     * @return the converted data
     */
    public static float[] byteToFloat(byte[] bytes) {
        return byteToFloat(bytes, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * byte to Float conversion
     *
     * @param bytes the bytes array
     * @param order the target byte order
     * @return the converted data
     */
    public static float[] byteToFloat(byte[] bytes, ByteOrder order) {
        order = (order == null ? ByteOrder.LITTLE_ENDIAN : order);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer = ByteBuffer.wrap(byteBuffer.array());
        byteBuffer.order(order);
        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }

        byteBuffer.clear();
        return floatValues;
    }

    /**
     * byte to Short conversion
     *
     * @param bytes the bytes array
     * @return the converted data
     */
    public static short[] byteToShort(byte[] bytes) {
        return byteToShort(bytes, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * byte to Short conversion
     *
     * @param bytes the bytes array
     * @param order the target byte order
     * @return the converted data
     */
    public static short[] byteToShort(byte[] bytes, ByteOrder order) {
        order = (order == null ? ByteOrder.LITTLE_ENDIAN : order);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(order);
        ShortBuffer shorts = byteBuffer.asShortBuffer();
        short[] shortValues = new short[shorts.capacity()];
        for (int i = 0; i < shorts.capacity(); i++) {
            shortValues[i] = shorts.get(i);
        }

        byteBuffer.clear();
        return shortValues;
    }
}
