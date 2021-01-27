package com.westlake.aird.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: An Shaowei
 * @Date: 2019/11/19 15:33
 * @Description:
 */
@Data
public class BlockIndex {

    private static final long serialVersionUID = -3258834219160663625L;

    /**
     * MS1->1, MS2->2
     * @see com.westlake.aird.enums.MsLevel
     */
    Integer level;

    /**
     * 该数据块在文件中的开始位置
     * The start pointer
     */
    Long startPtr;

    //在文件中的结束位置
    Long endPtr;

    //每一个MS2对应的MS1序号;MS1中为空
    Integer num;

    //每一个MS2对应的前体窗口;MS1中为空
    List<WindowRange> rangeList;

    //当msLevel=1时,本字段为每一个MS1谱图的序号,,当msLevel=2时本字段为每一个MS2谱图序列号
    List<Integer> nums;

    //所有该块中的rt时间列表
    List<Float> rts;

    //一个块中所有子谱图的mz的压缩后的大小列表
    List<Long> mzs;

    //一个块中所有子谱图的intenisty的压缩后的大小列表
    List<Long> ints;

    //用于存储KV键值对
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