/*
 * Copyright (c) 2020 Propro Studio
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.api;

import com.westlake.aird.bean.BlockIndex;
import com.westlake.aird.bean.Compressor;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.exception.ScanException;
import com.westlake.aird.util.FileUtil;

import java.io.RandomAccessFile;
import java.util.List;

public class CommonParser extends BaseParser {
    public CommonParser(String indexFilePath) throws ScanException {
        super(indexFilePath);
    }

    /**
     * 根据序列号查询光谱
     *
     * @param index
     * @return
     */
    public MzIntensityPairs getSpectrum(int index) {
        List<BlockIndex> indexList = getAirdInfo().getIndexList();
        for (int i = 0; i < indexList.size(); i++) {
            BlockIndex blockIndex = indexList.get(i);
            if (blockIndex.getNums().contains(index)) {
                int targetIndex = blockIndex.getNums().indexOf(index);
                return getSpectrum(blockIndex, targetIndex);
            }
        }
        return null;
    }

    /**
     * COMMON类型的文件中,每一个mzs和ints数组中存储的是位置而不是块大小
     *
     * @param index
     * @param position
     * @return
     */
    public MzIntensityPairs getSpectrum(BlockIndex index, int position) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");

            Long start = index.getMzs().get(position);
            raf.seek(start);
            Long delta = index.getInts().get(position) - start;
            byte[] reader = new byte[delta.intValue()];
            raf.read(reader);
            float[] mzArray = getMzValues(reader);
            start = index.getInts().get(position);
            raf.seek(start);
            if (position == (index.getInts().size() - 1)) {
                delta = index.getEndPtr() - start;
            } else {
                delta = index.getMzs().get(position + 1) - start;
            }
            reader = new byte[delta.intValue()];
            raf.read(reader);

            float[] intensityArray = null;
            if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
                intensityArray = getLogedIntValues(reader);
            } else {
                intensityArray = getIntValues(reader);
            }

            return new MzIntensityPairs(mzArray, intensityArray);

        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.close(raf);
        }

        return null;
    }
}
