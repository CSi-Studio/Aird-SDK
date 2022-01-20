package net.csibio.aird.compressor;

import java.nio.*;

public class ByteTrans {

    public static byte[] intToByte(int[] ints) {
        IntBuffer ibTarget = IntBuffer.wrap(ints);
        ByteBuffer bbTarget = ByteBuffer.allocate(ibTarget.capacity() * 4);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asIntBuffer().put(ibTarget);
        return bbTarget.array();
    }

    public static float[] doubleToFloat(double[] doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = (float) doubles[i];
        }
        return floats;
    }

    public static byte[] floatToByte(float[] floats) {
        FloatBuffer fbTarget = FloatBuffer.wrap(floats);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.order(ByteOrder.LITTLE_ENDIAN);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

    public static int[] byteToInt(byte[] bytes) {
        return byteToInt(bytes, ByteOrder.LITTLE_ENDIAN);
    }

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

    public static float[] byteToFloat(byte[] bytes) {
        return byteToFloat(bytes, ByteOrder.LITTLE_ENDIAN);
    }

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

    public static short[] byteToShort(byte[] bytes) {
        return byteToShort(bytes, ByteOrder.LITTLE_ENDIAN);
    }

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
