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
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.Arrays;

public class SnappyWrapper implements ByteComp {

    @Override
    public String getName() {
        return ByteCompType.Snappy.getName();
    }

    @Override
    public byte[] encode(byte[] input) {
        try {
            return Snappy.compress(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decode(byte[] input) {
        try {
            return Snappy.uncompress(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decode(byte[] input, int offset, int length) {
        return decode(Arrays.copyOfRange(input, offset, offset + length));
    }
}
