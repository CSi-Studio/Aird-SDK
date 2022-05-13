/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.parser;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.FileUtil;

import java.io.RandomAccessFile;
import java.util.List;

/**
 * 常规的格式转换模式,转换后的Aird文件同mzXML和mzML一样,所有的光谱图是以rt为顺序进行排序的 The Parser for normal format just like mzXML
 * or mzML, whose spectras are ordered by retention time
 */
public class CommonParser extends BaseParser {

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public CommonParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    public CommonParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    /**
     * 根据序列号查询光谱 get Spectrum by spectrum index
     *
     * @param num 光谱的索引号 the num of the spectrum
     * @return 光谱信息 spectrum data pairs
     */
    public Spectrum<double[], float[], double[]> getSpectrum(int num) {
        List<BlockIndex> indexList = getAirdInfo().getIndexList();
        for (int i = 0; i < indexList.size(); i++) {
            BlockIndex blockIndex = indexList.get(i);
            if (blockIndex.getNums().contains(num)) {
                int targetIndex = blockIndex.getNums().indexOf(num);
                return getSpectrum(blockIndex, targetIndex);
            }
        }
        return null;
    }

    /**
     * COMMON类型的文件中,每一个mzs和ints数组中存储的是位置而不是块大小 In the common type file. every data stored in the mzs
     * and ints is the spectrum location but not the block size
     *
     * @param index    索引信息 block index
     * @param position 指定的光谱位置 the specific spectrum index
     * @return 该光谱中的信息 spectrum data pairs
     */
    public Spectrum<double[], float[], double[]> getSpectrum(BlockIndex index, int position) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");

            int start = index.getMzs().get(position);
            raf.seek(start);
            long delta = index.getInts().get(position) - start;
            byte[] reader = new byte[(int) delta];
            raf.read(reader);
            double[] mzArray = getMzs(reader);
            start = index.getInts().get(position);
            raf.seek(start);
            if (position == (index.getInts().size() - 1)) {
                delta = index.getEndPtr() - start;
            } else {
                delta = index.getMzs().get(position + 1) - start;
            }
            reader = new byte[(int) delta];
            raf.read(reader);
            float[] intensityArray = null;
            intensityArray = getInts(reader);
            return new Spectrum<double[], float[], double[]>(mzArray, intensityArray);

        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.close(raf);
        }

        return null;
    }
}
