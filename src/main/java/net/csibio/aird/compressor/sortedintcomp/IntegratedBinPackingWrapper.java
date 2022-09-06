/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.compressor.sortedintcomp;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import net.csibio.aird.enums.SortedIntCompType;

/**
 * 入参必须是有序数组,经过SIMD优化的算法
 */
public class IntegratedBinPackingWrapper implements SortedIntComp {

    @Override
    public String getName() {
        return SortedIntCompType.IBP.getName();
    }

    /**
     * compress the data with fastpfor algorithm
     *
     * @param uncompressed sorted integers to be compressed
     * @return compressed data
     */
    @Override
    public int[] encode(int[] uncompressed) {
        int[] compressed = new IntegratedIntCompressor().compress(uncompressed);
        return compressed;
    }

    /**
     * decompress the data with fastpfor algorithm
     *
     * @param compressed 压缩对象
     * @return decompressed data
     */
    @Override
    public int[] decode(int[] compressed) {
        int[] uncompressed = new IntegratedIntCompressor().uncompress(compressed);
        return uncompressed;
    }
}
