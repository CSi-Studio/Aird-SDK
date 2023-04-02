using System.Collections.Generic;

namespace AirdSDK.Beans;

public class ColumnIndex
{
    public int level;

    /**
     * the column start position in the file
     * 在文件中的开始位置
     */
    public long startPtr;

    /**
      * the column end position in the file
      * 在文件中的结束位置
      */
    public long endPtr;

    /**
     * 对应的前体范围，在DDA模式中本字段为空，因为DDA文件的二级数据块没有逻辑连续性，在面向列存储的场景下无意义
     * 在DIA模式下，如果level==1则本字段为空，如果level==2则本字段为对应的前体范围
     */
    public WindowRange range;

    public long startMzListPtr;
    public long endMzListPtr;

    public long startRtListPtr;
    public long endRtListPtr;

    public long startSpecrtaIdListPtr;
    public long endSpecrtaIdListPtr;

    public long startIntensityListPtr;
    public long endIntensityListPtr;

    public int[] mzs; //矩阵横坐标

    public int[] rts; //矩阵纵坐标

    public int[] spectraIds; //spectraId的数组文件坐标delta值

    public int[] intensities; //强度数组坐标delta

    public string toString()
    {
        if (level == 1)
        {
            return "MS1-Col:";
        }
        else
        {
            return "MS2-" + range.mz + ":";
        }
    }

}