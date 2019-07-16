package com.westlake.aird.bean;

import lombok.Data;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-07-26 23:32
 */
@Data
public class WindowRange {

    /**
     * precursor mz start
     */
    Float start;
    /**
     * precursor mz end
     */
    Float end;
    /**
     * precursor mz
     */
    Float mz;

    /**
     * 扩展字段
     */
    String features;

    public WindowRange() {}

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        if(obj instanceof WindowRange){
            WindowRange windowRange = (WindowRange) obj;
            if(start == null || end == null || windowRange.getStart() == null || windowRange.getEnd()==null){
                return false;
            }

            return (this.start.equals(windowRange.getStart()) && this.end.equals(windowRange.getEnd()));
        }else{
            return false;
        }
    }
}
