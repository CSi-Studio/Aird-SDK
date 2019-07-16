package com.westlake.aird.bean;

import lombok.Data;

import java.util.List;

@Data
public class SwathIndex {

    /**
     * 1: ms1 swath block, 2: ms2 swath block
     */
    Integer level;
    /**
     * 在文件中的开始位置
     */
    Long startPtr;
    /**
     * 在文件中的结束位置
     */
    Long endPtr;
    /**
     * SWATH块对应的WindowRange
     */
    WindowRange range;
    /**
     * 当msLevel=1时,本字段为Swath Block中每一个MS1谱图的序号,,当msLevel=2时本字段为Swath Block中每一个MS2谱图对应的MS1谱图的序列号,MS2谱图本身不需要记录序列号
     */
    List<Integer> nums;
    /**
     * 一个Swath块中所有子谱图的rt时间列表
     */
    List<Float> rts;
    /**
     * 一个Swath块中所有子谱图的mz的压缩后的大小列表
     */
    List<Long> mzs;
    /**
     * 一个Swath块中所有子谱图的intenisty的压缩后的大小列表
     */
    List<Long> ints;
    /**
     * 用于存储KV键值对
     */
    String features;
}
