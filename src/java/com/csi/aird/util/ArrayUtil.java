/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.csi.aird.util;

import com.csi.aird.structure.SortInt;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Pair;

public class ArrayUtil {

    /**
     * @param originalArray 原数组
     * @param currentLayer  当前层数
     * @return
     */
    public static SortInt[] transToSortIntArray(int[] originalArray, int currentLayer) throws Exception {
        SortInt[] sortInts = new SortInt[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            sortInts[i] = new SortInt(originalArray[i], currentLayer);
        }
        return sortInts;
    }

    /**
     * @param sortInts
     * @param totalLayersCount 堆叠占位数,例如2层堆叠需要1位表示(即0和1),4层堆叠需要2位数表示(即00,01,10,11)
     * @return
     */
    public static Pair<int[], byte[]> transToOriginArrayAndLayerNote(SortInt[] sortInts, int totalLayersCount) {

        int[] mz = new int[sortInts.length];
        Integer layerNote = 0b00;

        byte[] layerNoteBytes = new byte[(int) (Math.ceil(sortInts.length / 8d * totalLayersCount))];
        int count = 0;
        int addWhenMatch = 8;
        int currentByteLocation = 0;

        for (int i = 0; i < sortInts.length; i++) {
            mz[i] = sortInts[i].getNumber();
            layerNote = layerNote << totalLayersCount;
            layerNote |= sortInts[i].getLayer();
            count += totalLayersCount;

            if (count < addWhenMatch) {
                continue;
            }

            if (count == addWhenMatch) {
                layerNoteBytes[currentByteLocation] = layerNote.byteValue();
            } else {
                layerNoteBytes[currentByteLocation] = Integer.valueOf(layerNote >> (count - addWhenMatch)).byteValue();
                layerNote = layerNote << (32 - (count - addWhenMatch)) >>> (32 - (count - addWhenMatch));
            }
            currentByteLocation++;
            count -= addWhenMatch;
        }
        //最后补齐
        if (count != 0) {
            layerNoteBytes[currentByteLocation] = layerNote.byteValue();
        }
        return new Pair<>(mz, ArrayUtils.subarray(layerNoteBytes, 0, currentByteLocation + 1));
    }

    public static long avgDelta(int[] array){
        long delta = 0;
        for (int i = 0; i < array.length - 1; i++) {
            if ((array[i+1]-array[i]) == 0){
                delta++;
            }
        }
        return delta;
    }
}
