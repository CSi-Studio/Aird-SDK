package net.csibio.aird.mzmineResultCompare;

import lombok.Data;

@Data
public class Peak extends Object implements PeakControl {
    float rt;
    float mz;
    float area;

    @Override
    public boolean equals(Object arg0, float tolerance) {
        Peak o = (Peak) arg0;
        if (((this.mz-tolerance) <= o.mz)&&((this.mz+tolerance) >= o.mz)) {
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public boolean containsValue(Object arg0, float tolerance) {
//        Peak o = (Peak) arg0;
//        if (((this.mz-tolerance) <= o.mz)&&((this.mz+tolerance) >= o.mz)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
