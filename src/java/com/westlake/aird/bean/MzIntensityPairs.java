package com.westlake.aird.bean;

import lombok.Data;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-07-22 22:40
 */
@Data
public class MzIntensityPairs {

    float[] mzArray;

    float[] intensityArray;

    public MzIntensityPairs(){}

    public MzIntensityPairs(float[] mzArray, float[] intensityArray){
        this.mzArray = mzArray;
        this.intensityArray = intensityArray;
    }
}
