package com.westlake.aird.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MsCycle implements Serializable {

    private static final long serialVersionUID = -123L;

    double rt;

    double ri;

    MzIntensityPairs ms1Spectrum;

    //MS2的RT沿用MS1
    List<WindowRange> rangeList;

    //MS2的RT时间列表
    List<Float> rts;

    List<MzIntensityPairs> ms2Spectrums;
}
