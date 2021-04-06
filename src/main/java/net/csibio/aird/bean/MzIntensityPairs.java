/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-07-22 22:40
 */
@Data
public class MzIntensityPairs {

    /**
     * m/z array with float type
     */
    float[] mzArray;

    /**
     * m/z array with integer type which is directly from Aird file
     */
    int[] mz;

    /**
     * intensity array with float type
     */
    float[] intensityArray;

    public MzIntensityPairs(){}

    public MzIntensityPairs(float[] mzArray, float[] intensityArray){
        this.mzArray = mzArray;
        this.intensityArray = intensityArray;
    }

    public MzIntensityPairs(int[] mz, float[] intensityArray){
        this.mz = mz;
        this.intensityArray = intensityArray;
    }
}
