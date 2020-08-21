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
import java.util.*;

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
    public Map<Float, MzIntensityPairs> getSpectrums(BlockIndex index) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");

            Map<Float, MzIntensityPairs> map = Collections.synchronizedMap(new TreeMap<>());
            List<Float> rts = index.getRts();

            raf.seek(index.getStartPtr());
            Long delta = index.getEndPtr() - index.getStartPtr();
            byte[] result = new byte[delta.intValue()];

            raf.read(result);
            List<Long> mzSizes = index.getMzs();
            List<Long> intensitySizes = index.getInts();

            int start = 0;

            List<int[]> allPtrList = new ArrayList<>();
            for (int i = 0; i < mzSizes.size(); i++) {
                int[] ptrs = new int[3];
                ptrs[0] = start;
                ptrs[1] = ptrs[0] + mzSizes.get(i).intValue();
                ptrs[2] = ptrs[1] + intensitySizes.get(i).intValue();
                allPtrList.add(ptrs);
                start = ptrs[2];
            }

            rts.parallelStream().forEach(rt -> {
                int[] ptrs = allPtrList.get(rts.indexOf(rt));
                try {
                    float[] intensityArray = null;
                    if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
                        intensityArray = getLogedIntValues(result, ptrs[1], ptrs[2] - ptrs[1]);
                    } else {
                        intensityArray = getIntValues(result, ptrs[1], ptrs[2] - ptrs[1]);
                    }
                    MzIntensityPairs pairs = new MzIntensityPairs(getMzValues(result, ptrs[0], ptrs[1] - ptrs[0]), intensityArray);
                    map.put(rt, pairs);
                } catch (Exception e) {
                    System.out.println("index size error, RT:" + rt);
                }
            });
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
     * 从Aird文件中读取,但是不要将m/z数组的从Integer改为Float
     *
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
