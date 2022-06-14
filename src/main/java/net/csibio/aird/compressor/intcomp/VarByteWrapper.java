/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.intcomp;

import me.lemire.integercompression.IntCompressor;
import me.lemire.integercompression.VariableByte;
import net.csibio.aird.enums.IntCompType;

public class VarByteWrapper implements IntComp {

    @Override
    public String getName() {
        return IntCompType.VB.getName();
    }

    /**
     * Delta+VByte+ByteCompressor Encoder. Default ByteCompressor is Zlib. The compress target must be
     * sorted integers.
     *
     * @param uncompressed the sorted integers
     * @return the compressed data
     */
    @Override
    public int[] encode(int[] uncompressed) {
        int[] compressedInts = new IntCompressor(new VariableByte()).compress(uncompressed);
        return compressedInts;
    }

    @Override
    public int[] decode(int[] compressed) {
        int[] uncompressed = new IntCompressor(new VariableByte()).uncompress(compressed);
        return uncompressed;
    }
}
