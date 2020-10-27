/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.csi.aird.bean;

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
