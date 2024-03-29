/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.bytecomp;

import net.csibio.aird.enums.ByteCompType;
import net.csibio.aird.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibWrapper implements ByteComp {

    static int BUFFER_SIZE = 2048;

    @Override
    public String getName() {
        return ByteCompType.Zlib.getName();
    }

    @Override
    public byte[] encode(byte[] input) {
        Deflater compressor = new Deflater();
        compressor.reset();
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            while (!compressor.finished()) {
                int i = compressor.deflate(buf);
                bos.write(buf, 0, i);
            }
            compressor.finish();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(bos);
            compressor.end();
        }

        return new byte[0];
    }

    @Override
    public byte[] decode(byte[] input) {
        return decode(input, 0, input.length);
    }

    @Override
    public byte[] decode(byte[] input, int offset, int length) {
        Inflater inflater = new Inflater();
        inflater.setInput(input, offset, length);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
        try {
            byte[] buffer = new byte[10240];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(outputStream);
        }
        inflater.end();
        return outputStream.toByteArray();

    }
}
