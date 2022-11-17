/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using AirdSDK.Beans;
using System.Collections.Generic;
using AirdSDK.Beans.Common;
using AirdSDK.Utils;

namespace AirdSDK.Parser;

public class DDAPasefParser : BaseParser
{
    public DDAPasefParser(string indexFilePath) : base(indexFilePath)
    {
    }

    public DDAPasefParser(string indexFilePath, AirdInfo airdInfo) : base(indexFilePath, airdInfo)
    {
    }

    public BlockIndex getMs1Index()
    {
        if (airdInfo != null && airdInfo.indexList != null && airdInfo.indexList.Count > 0)
        {
            return airdInfo.indexList[0];
        }

        return null;
    }

    public List<BlockIndex> getAllMs2Index()
    {
        if (airdInfo != null && airdInfo.indexList != null && airdInfo.indexList.Count > 0)
        {
            return airdInfo.indexList.GetRange(1, airdInfo.indexList.Count);
        }

        return null;
    }

    /**
     * DDA文件采用一次性读入内存的策略 DDA reader using the strategy of loading all the information into the memory
     *
     * @return DDA文件中的所有信息, 以MsCycle的模型进行存储 the mz-intensity pairs read from the aird. And store as
     * MsCycle in the memory
     * @throws Exception exception when reading the file
     */
    public List<DDAPasefMs> readAllToMemory()
    {
        List<DDAPasefMs> ms1List = new List<DDAPasefMs>();
        BlockIndex ms1Index = getMs1Index(); //所有的ms1谱图都在第一个index中
        List<BlockIndex> ms2IndexList = getAllMs2Index();
        Dictionary<double, Spectrum> ms1Map = getSpectra(ms1Index.startPtr, ms1Index.endPtr, ms1Index.rts,
            ms1Index.mzs, ms1Index.ints, ms1Index.mobilities);
        List<double> ms1RtList = new List<double>(ms1Map.Keys);

        for (int i = 0; i < ms1RtList.Count; i++)
        {
            DDAPasefMs ms1 = new DDAPasefMs(ms1RtList[i], ms1Map[ms1RtList[i]]);
            DDAUtil.initFromIndex(ms1, ms1Index, i);
            BlockIndex ms2Index = ms2IndexList.Find(delegate(BlockIndex index)
            {
                return index.getParentNum().Equals(ms1.num);
            });

            if (ms2Index != null)
            {
                Dictionary<double, Spectrum> ms2Map = getSpectra(ms2Index.startPtr, ms2Index.endPtr,
                    ms2Index.rts, ms2Index.mzs, ms2Index.ints, ms2Index.mobilities);
                List<double> ms2RtList = new List<double>(ms2Map.Keys);
                List<DDAPasefMs> ms2List = new List<DDAPasefMs>();
                for (int j = 0; j < ms2RtList.Count; j++)
                {
                    DDAPasefMs ms2 = new DDAPasefMs(ms2RtList[j], ms2Map[ms2RtList[j]]);
                    DDAUtil.initFromIndex(ms2, ms2Index, j);
                    ms2List.Add(ms2);
                }

                ms1.ms2List = ms2List;
            }

            ms1List.Add(ms1);
        }

        return ms1List;
    }
}