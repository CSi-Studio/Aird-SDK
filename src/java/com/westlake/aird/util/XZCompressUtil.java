/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.util;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class XZCompressUtil {

    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        // 第二个参数为xz压缩等级
        byte[] compressed = xzCompress(bbTarget.array(), 1);
        return compressed;
    }

    public static float[] transToFloat(byte[] target){
        byte[] decompressed = xzDecompress(target);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decompressed);
        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }
        byteBuffer.clear();
        return floatValues;
    }

    public static byte[] xzCompress(byte[] target, int level)  {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (bos;XZOutputStream xzOutputStream = new XZOutputStream(bos, new LZMA2Options(level));){
            xzOutputStream.write(target);
        }catch (IOException e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public static byte[] xzDecompress(byte[] target) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(out; InputStream is = new ByteArrayInputStream(target); XZInputStream xzInputStream = new XZInputStream(is);) {
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = xzInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
