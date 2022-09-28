/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.intcomp;

import me.lemire.integercompression.BinaryPacking;
import me.lemire.integercompression.IntCompressor;
import net.csibio.aird.enums.IntCompType;

/**
 * Binary Packing Wrapper implementation
 */
public class BinPackingWrapper implements IntComp {

    @Override
    public String getName() {
        return IntCompType.BP.getName();
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
        int[] compressedInts = new IntCompressor().compress(uncompressed);
        return compressedInts;
    }

    @Override
    public int[] decode(int[] compressed) {
        int[] uncompressed = new IntCompressor().uncompress(compressed);
        return uncompressed;
    }
}
