/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System.Collections.Generic;

namespace AirdSDK.Beans;

public class BlockIndex
{
    /**
         * 1:MS1;2:MS2
         */
    public int level;

    /**
         * the block start position in the file
         * 在文件中的开始位置
         */
    public long startPtr;

    /**
         * the block end position in the file
         * 在文件中的结束位置
         */
    public long endPtr;

    /**
         * The related MS1 num of every MS2, or this field would be null
         * 每一个MS2对应的MS1序号;如果是MS1块,则本字段为空
         */
    public int num;

    /**
         * The precursor window range of every MS2 block. It is empty when if the Block is for MS1
         * 每一个MS2对应的前体窗口;MS1中为空,在SWATH/DIA和PRM的采集模式下,本数组的长度恒为1
         */
    public List<WindowRange> rangeList;

    /**
         * when msLevel = 1, this field means the related num of every MS1.
         * when msLevel = 2, this field means the related num of every MS2.
         * 当msLevel=1时,本字段为每一个MS1谱图的序号,当msLevel=2时本字段为每一个MS2谱图序列号
         */
    public List<int> nums = new();

    /**
         * Every Spectrum's RT in the block
         * 所有该块中的rt时间列表
         */
    public List<double> rts = new();

    /**
         * Every Spectrum's total intensity in the block
         * 所有该块中的tic列表
         */
    public List<long> tics = new();

    /**
         * Every Spectrum's total base peak intensity in the block
         * 所有该块中的tic列表
         */
    public List<double> basePeakIntensities = new();

    /**
         * Every Spectrum's injection times
         * 该光谱的注入时间
         */
    public List<float> injectionTimes = new();

    /**
        * Every Spectrum's total base peak mz in the block
        * 所有该块中的tic列表
        */
    public List<double> basePeakMzs = new();

    /**
       * Every Spectrum's filterString in the block
       * 所有该块中的filterString列表
       */
    public List<string> filterStrings = new();

    /**
         * Every Spectrum's activator in the block
         * 所有该块中的activator列表
         */
    public List<string> activators = new();

    /**
         * Every Spectrum's energy in the block
         * 所有该块中的energy列表
         */
    public List<float> energies = new();

    /**
         * Every Spectrum's polarity in the block
         * 所有该块中的polarity列表
         */
    public List<string> polarities = new();

    /**
         * Every Spectrum's msType in the block
         * 所有该块中的msType列表
         */
    public List<string> msTypes = new();

    /**
         * COMMON type: it store the start position of every compressed mz block
         * Other types: it store the size of every compressed mz block
         * 一个块中所有子谱图的mz的压缩后的大小列表,当为Common类型时,每一个存储的不是块大小,而是起始位置
         */
    public List<int> mzs = new();

    /**
        * Only using Stack ZDPD. The compressed list for tags of every mz. 
        * 一个块中所有子谱图的mz原层码的压缩后的数组大小列表
        */
    public List<int> tags = new();

    /**
         * COMMON type: it store the start position of every compressed intensity block
         * Other types: it store the size of every compressed intensity block
         * 一个块中所有子谱图的intensity的压缩后的大小列表,当为Common类型时,每一个存储的不是块大小,而是起始位置
         */
    public List<int> ints = new();

    /**
         * Ion Mobility types: it store the size of every compressed intensity block
         * 一个块中所有子谱图的mobility的压缩后的大小列表
         */
    public List<int> mobilities = new();

    /**
         * PSI CV
         * PSI可控词汇表
         */
    public List<List<CV>> cvList = new();

    /**
         * Features of every block index
         * 用于存储KV键值对
         */
    public string features;

    public WindowRange getWindowRange()
    {
        if (rangeList == null || rangeList.Count == 0)
            return null;
        return rangeList[0];
    }

    public void setWindowRange(WindowRange wr)
    {
        if (rangeList == null) rangeList = new List<WindowRange>();

        rangeList.Add(wr);
    }

    public BlockIndex()
    {
    }

    public BlockIndex(int level)
    {
        this.level = level;
    }

    public int getParentNum()
    {
        if (level.Equals(2))
            return num;
        return -1;
    }
}