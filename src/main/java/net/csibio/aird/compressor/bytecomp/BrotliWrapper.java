/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.bytecomp;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import com.aayushatharva.brotli4j.decoder.DirectDecompress;
import com.aayushatharva.brotli4j.encoder.Encoder;
import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import net.csibio.aird.enums.ByteCompType;

import java.util.Arrays;

/**
 * @author lms
 */
public class BrotliWrapper implements ByteComp {

    static {
        Brotli4jLoader.ensureAvailability();
    }

    @Override
    public String getName() {
        return ByteCompType.Brotli.getName();
    }

    @Override
    public byte[] encode(byte[] input) {
        try {
            Parameters params = new Parameters();
//      params.setMode(-1);
//      params.setQuality(5); //[0,11] or -1
//      params.setWindow(12);
            return Encoder.compress(input, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decode(byte[] input) {
        try {
            DirectDecompress directDecompress = DirectDecompress.decompress(input);
            if (directDecompress.getResultStatus() == DecoderJNI.Status.DONE) {
                return directDecompress.getDecompressedData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decode(byte[] input, int offset, int length) {
        return decode(Arrays.copyOfRange(input, offset, offset + length));
    }
}
