package net.csibio.aird.bean;

import lombok.Data;

import java.util.List;

@Data
public class ColumnIndex {

    Integer level;

    /**
     * the column start position in the file
     * 在文件中的开始位置
     */
    Long startPtr;

    /**
     * the column end position in the file
     * 在文件中的结束位置
     */
    Long endPtr;

    /**
     * 对应的前体范围，在DDA模式中本字段为空，因为DDA文件的二级数据块没有逻辑连续性，在面向列存储的场景下无意义
     * 在DIA模式下，如果level==1则本字段为空，如果level==2则本字段为对应的前体范围
     */
    WindowRange range;

    long startMzListPtr;
    long endMzListPtr;

    long startRtListPtr;
    long endRtListPtr;

    List<Integer> mzs; //矩阵横坐标

    List<Integer> rts; //矩阵纵坐标

    List<Integer> spectraIds; //spectraId的数组文件坐标delta值

    List<Integer> intensities; //强度数组坐标delta
}
