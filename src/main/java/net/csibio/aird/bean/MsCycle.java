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

import java.io.Serializable;
import java.util.List;

/**
 * every sycle mean ms1 spectrum with related ms2 spectra
 * @deprecated use DDAMs instead
 */
@Deprecated
@Data
public class MsCycle implements Serializable {

    private static final long serialVersionUID = -123L;

    /**
     * Retention Time, unit: minutes
     */
    Double rt;

    /**
     * Retention Time Index, used for some specific scene
     */
    Double ri;

    /**
     * the tic data for ms1
     */
    Long tic;

    /**
     * cvList for ms1 scan
     */
    List<CV> cvList;

    /**
     * the ms1 spectrum data pairs
     */
    MzIntensityPairs ms1Spectrum;

    /**
     * the window range for ms2
     */
    List<WindowRange> rangeList;

    /**
     * the rt list for ms2
     */
    List<Float> rts;

    /**
     * the tic list for related ms2
     */
    List<Long> tics;

    /**
     * the cv list for related ms2
     */
    List<List<CV>> ms2CvList;

    /**
     * the related ms2 spectrums
     */
    List<MzIntensityPairs> ms2Spectrums;
}
