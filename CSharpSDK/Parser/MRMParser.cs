/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System;
using System.Collections.Generic;
using System.IO;
using AirdSDK.Beans;
using AirdSDK.Beans.Common;
using AirdSDK.Compressor;

namespace AirdSDK.Parser;

public class MRMParser : DDAParser
{
    public MRMParser(string indexFilePath) : base(indexFilePath)
    {
    }

    public MRMParser(string indexFilePath, AirdInfo airdInfo) : base(indexFilePath, airdInfo)
    {
    }

    public ChromatogramIndex getChromatogramIndex()
    {
        if (airdInfo != null && airdInfo.chromatogramIndex != null) return airdInfo.chromatogramIndex;

        return null;
    }

    public List<SrmPair> getAllSrmPairs()
    {
        var index = getChromatogramIndex();
        if (index == null || index.precursors == null || index.products == null || index.precursors.Count == 0 ||
            index.products.Count == 0) return null;

        var pairs = new List<SrmPair>();
        var dict = getChromatograms(index.startPtr, index.endPtr, index.ids, index.rts, index.ints);
        for (var i = 0; i < index.precursors.Count; i++)
        {
            var pair = new SrmPair();
            pair.id = index.ids[i];
            pair.num = index.nums[i];
            pair.polarity = index.polarities[i];
            pair.activator = index.activators[i];
            pair.energy = index.energies[i];
            pair.cvList = index.cvs[i];
            pair.precursor = index.precursors[i];
            pair.product = index.products[i];
            pair.key = pair.precursor.mz + "-" + pair.product.mz;
            if (dict.ContainsKey(pair.id))
            {
                pair.rts = dict[pair.id].rts;
                pair.ints = dict[pair.id].ints;
            }
            pairs.Add(pair);
        }

        return pairs;
    }

    /**
   * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息
   * 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
   */
    public Dictionary<string, Xic> getChromatograms(long start, long end, List<string> keyList, List<int> rtOffsets,
        List<int> intOffsets)
    {
        Dictionary<string, Xic> map = new Dictionary<string, Xic>();
        fs.Seek(start, SeekOrigin.Begin);
        long delta = end - start;
        byte[] result = new byte[(int)delta];
        fs.Read(result, 0, result.Length);
        int iter = 0;
        for (int i = 0; i < keyList.Count; i++)
        {
            map.Add(keyList[i], getChromatogram(result, iter, rtOffsets[i], intOffsets[i]));
            iter = iter + rtOffsets[i] + intOffsets[i];
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
        var doubleValues = new double[intValues.Length];
        for (var index = 0; index < intValues.Length; index++) doubleValues[index] = intValues[index] / 100000d;

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

        var intensityValues = new double[intValues.Length];
        for (var i = 0; i < intValues.Length; i++)
        {
            double intensity = intValues[i];
            if (intensity < 0) intensity = Math.Pow(2, -intensity / 100000d);

            intensityValues[i] = intensity / 1;
        }

        return intensityValues;
    }
}