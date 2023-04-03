package net.csibio.aird.bean;

import lombok.Data;

import java.util.List;

@Data
public class ColumnInfo {

    List<ColumnIndex> indexList;

    /**
     * [Core Field]
     * AcquisitionMethod, Support for DIA/SWATH, PRM, DDA, SRM/MRM, DDAPasef, DIAPasef
     * [核心字段]
     * Aird支持的采集模式的类型,目前支持SRM/MRM, DIA, PRM, DDA, DDAPasef, DIAPasef 6种
     */
    String type;

    /**
     * the aird file path.
     * 转换压缩后的aird二进制文件路径,默认读取同目录下的同名文件,如果不存在才去读本字段对应的路径
     */
    String airdPath;

    int mzPrecision;

    int intPrecision;
}
