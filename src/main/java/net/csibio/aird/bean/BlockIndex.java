package net.csibio.aird.bean;

import net.csibio.aird.enums.MsLevel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : An Shaowei
 * 2019/11/19 15:33
 */
@Data
public class BlockIndex {

    private static final long serialVersionUID = -3258834219160663625L;

    /**
     * MS1->1, MS2->2
     * @see MsLevel
     */
    Integer level;

    /**
     * 该数据块在文件中的开始位置
     * The start pointer in the aird file
     */
    Long startPtr;

    /**
     * 在文件中的结束位置
     * The end pointer in the aird file
     */
    Long endPtr;

    /**
     * 每一个MS2对应的MS1序号;MS1中为空
     * The MS1 Number of Current Block Index(Current Block Index should be MS2 Index). It is null when Current Block is an MS1 Block Index
     */
    Integer num;

    /**
     * 每一个MS2对应的前体窗口;MS1中为空
     * The Precursor Range Windows for MS2,It is null when Current Block is an MS1 Block Index
     */
    List<WindowRange> rangeList;

    /**
     * 当msLevel=1时,本字段为每一个MS1谱图的序号,,当msLevel=2时本字段为每一个MS2谱图序列号
     * When msLevel=1, this field is every MS1's num. When msLevel=2, this field is every MS2's num
     */
    List<Integer> nums;

    /**
     * 所有该块中的rt时间列表
     * all the retention time in the aird
     */
    List<Float> rts;

    /**
     * 一个块中所有子谱图的mz的压缩后的大小列表
     * every compressed mz array's binary size in the block index
     */
    List<Long> mzs;

    /**
     * 每一个mz对应的原有的层数, 使用zlib压缩,每一个存储的是压缩后的块大小
     * the original layers of every mz point, with zlib compress.
     */
    List<Long> tags;
    
    /**
     * 一个块中所有子谱图的intenisty的压缩后的大小列表
     * every compressed intensity array's binary size in the block index
     */
    List<Long> ints;

    /**
     * PSI Controlled Vocabulary
     * PSI可控词汇表
     */
    List<List<CV>> cvList;

    /**
     * 用于存储KV键值对
     * Pairs with key/value for extension features
     */
    String features;

    public WindowRange getWindowRange() {
        if (rangeList == null || rangeList.size() == 0) {
            return null;
        } else {
            return rangeList.get(0);
        }
    }

    public void setWindowRange(WindowRange windowRange) {
        if (rangeList == null) {
            rangeList = new ArrayList<>();
        }
        rangeList.add(windowRange);
    }
}
