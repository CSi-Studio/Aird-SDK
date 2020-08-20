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
import org.apache.commons.lang3.ArrayUtils;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.TreeMap;

public class DIAParser extends BaseParser {
    public DIAParser(String indexFilePath) throws ScanException {
        super(indexFilePath);
    }

    /**
     * the result key is rt,value is the spectrum
     * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息,原始谱图信息包含mz数组和intensity两个相同长度的数组
     *
     * @param index 需要解析的SWATH窗口
     * @return
     * @throws Exception
     */
    public TreeMap<Float, MzIntensityPairs> getSpectrums(BlockIndex index) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");
            TreeMap<Float, MzIntensityPairs> map = new TreeMap<Float, MzIntensityPairs>();
            List<Float> rts = index.getRts();

            raf.seek(index.getStartPtr());
            Long delta = index.getEndPtr() - index.getStartPtr();
            byte[] result = new byte[delta.intValue()];

            raf.read(result);
            List<Long> mzSizes = index.getMzs();
            List<Long> intensitySizes = index.getInts();

            int start = 0;
            for (int i = 0; i < mzSizes.size(); i++) {
                byte[] mz = ArrayUtils.subarray(result, start, start + mzSizes.get(i).intValue());
                start = start + mzSizes.get(i).intValue();
                byte[] intensity = ArrayUtils.subarray(result, start, start + intensitySizes.get(i).intValue());
                start = start + intensitySizes.get(i).intValue();
                try {

                    float[] intensityArray = null;
                    if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
                        intensityArray = getLogedIntValues(intensity);
                    } else {
                        intensityArray = getIntValues(intensity);
                    }

                    MzIntensityPairs pairs = new MzIntensityPairs(getMzValues(mz), intensityArray);
                    map.put(rts.get(i), pairs);
                } catch (Exception e) {
                    System.out.println("index size error:" + i);
                }

            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.close(raf);
        }
        return null;
    }





    /**
     * 从aird文件中获取某一条记录
     * 从一个完整的Swath Block块中取出一条记录
     *
     * @param index
     * @param rt    获取某一个时刻原始谱图
     * @return
     */
    public MzIntensityPairs getSpectrum(BlockIndex index, float rt) {
        List<Float> rts = index.getRts();
        int position = rts.indexOf(rt);
        return getSpectrum(index, position);
    }

    public MzIntensityPairs getSpectrum(BlockIndex index, int position) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");
            long start = index.getStartPtr();

            for (int i = 0; i < position; i++) {
                start += index.getMzs().get(i);
                start += index.getInts().get(i);
            }

            raf.seek(start);
            byte[] reader = new byte[index.getMzs().get(position).intValue()];
            raf.read(reader);
            float[] mzArray = getMzValues(reader);
            start += index.getMzs().get(position).intValue();
            raf.seek(start);
            reader = new byte[index.getInts().get(position).intValue()];
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

    /**
     * 根据序列号查询光谱
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
     * 从Aird文件中读取,但是不要将m/z数组的从Integer改为Float
     * @param index
     * @param rt
     * @return
     */
    public MzIntensityPairs getSpectrumAsInteger(BlockIndex index, float rt) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");
            List<Float> rts = index.getRts();
            int position = rts.indexOf(rt);

            long start = index.getStartPtr();

            for (int i = 0; i < position; i++) {
                start += index.getMzs().get(i);
                start += index.getInts().get(i);
            }

            raf.seek(start);
            byte[] reader = new byte[index.getMzs().get(position).intValue()];
            raf.read(reader);
            int[] mzArray = getMzValuesAsInteger(reader);
            start += index.getMzs().get(position).intValue();
            raf.seek(start);
            reader = new byte[index.getInts().get(position).intValue()];
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
