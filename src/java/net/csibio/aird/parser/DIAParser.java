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

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.FileUtil;

import java.io.RandomAccessFile;
import java.util.*;

public class DIAParser extends BaseParser {
    public DIAParser(String indexFilePath) throws ScanException {
        super(indexFilePath);
    }

    public Map<Float, MzIntensityPairs> getSpectrums(BlockIndex index) {
        return getSpectrums(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(), index.getInts());
    }

    /**
     * the result key is rt,value is the spectrum
     * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息,原始谱图信息包含mz数组和intensity两个相同长度的数组
     * @param startPtr 起始指针位置
     * @param endPtr 结束指针位置
     * @param rtList rt列表,包含所有的光谱产出时刻
     * @param mzSizeList mz块的大小列表
     * @param intensitySizeList intensity块的大小列表
     * @return 每一个时刻对应的光谱信息
     */
    public Map<Float, MzIntensityPairs> getSpectrums(long startPtr, long endPtr, List<Float> rtList, List<Long> mzSizeList, List<Long> intensitySizeList) {

        try {
            Map<Float, MzIntensityPairs> map = Collections.synchronizedMap(new TreeMap<>());

            raf.seek(startPtr);
            long delta = endPtr - startPtr;
            byte[] result = new byte[(int) delta];

            raf.read(result);

            int start = 0;

            List<int[]> allPtrList = new ArrayList<>();
            // 准备多线程读取
            for (int i = 0; i < mzSizeList.size(); i++) {
                int[] ptrs = new int[3];
                ptrs[0] = start;
                ptrs[1] = ptrs[0] + mzSizeList.get(i).intValue();
                ptrs[2] = ptrs[1] + intensitySizeList.get(i).intValue();
                allPtrList.add(ptrs);
                start = ptrs[2];
            }

            // 使用多线程进行信息读取
            rtList.parallelStream().forEach(rt -> {
                int[] ptrs = allPtrList.get(rtList.indexOf(rt));
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
     * @param index 索引信息
     * @param rt    获取某一个时刻原始谱图
     * @return 某个时刻的光谱信息
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
     * @param index 索引序列号
     * @return 该索引号对应的光谱信息
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
     * @param index 索引序列号
     * @param rt 光谱产出时刻
     * @return 该时刻产生的光谱信息,其中mz数据以int类型返回
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
