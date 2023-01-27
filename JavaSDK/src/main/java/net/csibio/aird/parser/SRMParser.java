/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.parser;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.ChromatogramIndex;
import net.csibio.aird.bean.common.SrmPair;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.exception.ScanException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see SRMParser for SRM/MRM acquisition method
 * for chromatograms and spectra storage.
 */
public class SRMParser extends DDAParser {

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public SRMParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    public SRMParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    public ChromatogramIndex getChromatogramIndex()
    {
        if (airdInfo != null && airdInfo.getChromatogramIndex() != null) return airdInfo.getChromatogramIndex();

        return null;
    }

    public List<SrmPair> getAllSrmPairs() throws IOException {
        var index = getChromatogramIndex();
        if (index == null || index.getPrecursors() == null || index.getProducts() == null || index.getPrecursors().size() == 0 ||
                index.getProducts().size() == 0) return null;

        ArrayList<SrmPair> pairs = new ArrayList<>();
        HashMap<String, Xic> dict = getChromatograms(index.getStartPtr(), index.getEndPtr(), index.getIds(), index.getRts(), index.getInts());
        for (var i = 0; i < index.getPrecursors().size(); i++)
        {
            var pair = new SrmPair();
            pair.setId(index.getIds().get(i));
            pair.setNum(index.getNums().get(i));
            pair.setPolarity(index.getPolarities().get(i));
            pair.setActivator(index.getActivators().get(i));
            pair.setEnergy(index.getEnergies().get(i));
            pair.setCvList(index.getCvs().get(i));
            pair.setPrecursor(index.getPrecursors().get(i));
            pair.setProduct(index.getProducts().get(i));
            pair.setKey(pair.getPrecursor().getMz() + "-" + pair.getProduct().getMz());

            if (dict.containsKey(pair.getId()))
            {
                pair.setRts(dict.get(pair.getId()).getRts());
                pair.setInts(dict.get(pair.getId()).getInts());
            }
            pairs.add(pair);
        }

        return pairs;
    }

    /**
     * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息
     * 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
     */
    public HashMap<String, Xic> getChromatograms(long start, long end, List<String> keyList, List<Integer> rtOffsets,
                                                 List<Integer> intOffsets) throws IOException {
        HashMap<String, Xic> map = new HashMap<String, Xic>();
        raf.seek(start);
        long delta = end - start;
        byte[] result = new byte[(int)delta];
        raf.read(result, 0, result.length);
        int iter = 0;
        for (int i = 0; i < keyList.size(); i++)
        {
            map.put(keyList.get(i), getChromatogram(result, iter, rtOffsets.get(i), intOffsets.get(i)));
            iter = iter + rtOffsets.get(i) + intOffsets.get(i);
        }

        return map;
    }

    /**
     * 根据位移偏差解析单张光谱图
     */
    public Xic getChromatogram(byte[] bytes, int offset, int rtOffset, int intOffset)
    {
        if (rtOffset == 0) return new Xic(new double[0], new double[0]);

        double[] rtArray = getRts4Chroma(bytes, offset, rtOffset);
        offset = offset + rtOffset;
        double[] intensityArray = getInts4Chroma(bytes, offset, intOffset);
        return new Xic(rtArray, intensityArray);
    }

    /**
     * get mz values only for aird file 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
     * 用于获取色谱图中的rt的,其压缩框架为IVB+Zstd,精度为1,即精确到小数点后第四位,单位为秒
     * @param value  压缩后的数组
     * @param offset 起始位置
     * @param length 读取长度
     * @return 解压缩后的数组
     */
    public double[] getRts4Chroma(byte[] value, int offset, int length)
    {
        var decodedData = rtByteComp4Chroma.decode(value, offset, length);
        var intValues = ByteTrans.byteToInt(decodedData);
        intValues = rtIntComp4Chroma.decode(intValues);
        var doubleValues = new double[intValues.length];
        for (var index = 0; index < intValues.length; index++) doubleValues[index] = intValues[index] / 100000d;

        return doubleValues;
    }

    /**
     * get intensity values from the start point with a specified length
     * 用于获取色谱图中的强度的,其压缩框架为VB+Zstd,精度为1,即精确到个位数
     * @param value  the original array
     * @param start  the start point
     * @param length the specified length
     * @return the decompression intensity array
     */
    public double[] getInts4Chroma(byte[] value, int start, int length)
    {
        var decodedData = intByteComp4Chroma.decode(value, start, length);
        var intValues = ByteTrans.byteToInt(decodedData);
        intValues = intIntComp4Chroma.decode(intValues);

        var intensityValues = new double[intValues.length];
        for (var i = 0; i < intValues.length; i++)
        {
            double intensity = intValues[i];
            if (intensity < 0) intensity = Math.pow(2, -intensity / 100000d);

            intensityValues[i] = intensity;
        }

        return intensityValues;
    }
}
