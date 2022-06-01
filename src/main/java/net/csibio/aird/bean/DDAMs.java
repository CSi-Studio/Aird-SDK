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
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.constant.PSI;

import java.io.Serializable;
import java.util.List;

/**
 * New Structure for MsCycle every ms mean ms1 spectrum with related ms2 spectra
 */
@Data
public class DDAMs implements Serializable {

    private static final long serialVersionUID = -123222L;

    /**
     * order number for current spectrum
     */
    Integer num;

    /**
     * Retention Time, unit: reference from raw file, default is second required
     */
    Double rt;

    /**
     * the tic data for current scan
     */
    Long tic;

    /**
     * cvList for current scan
     */
    List<CV> cvList;

    /**
     * the window range for current scan
     */
    WindowRange range;

    /**
     * the ms1 spectrum data pairs required
     */
    Spectrum spectrum;

    /**
     * related ms2 list
     */
    List<DDAMs> ms2List;

    public DDAMs() {
    }

    public DDAMs(Double rt, Spectrum spectrum) {
        this.rt = rt;
        this.spectrum = spectrum;
    }

    public Integer isPolarity() {
        if (cvList != null) {
            for (CV cv : cvList) {
                String cvid = cv.getCvid();
                if (cvid.contains(PSI.cvPolarityPositive)) {
                    return 1;
                }
                if (cvid.contains(PSI.cvPolarityNegative)) {
                    return -1;
                }
            }
        }
        return 0;
    }

    public Integer isProfile() {
        if (cvList != null) {
            for (CV cv : cvList) {
                String cvid = cv.getCvid();
                if (cvid.contains(PSI.cvProfileSpectrum)) {
                    return 1;
                }
                if (cvid.contains(PSI.cvCentroidSpectrum)) {
                    return -1;
                }
            }
        }
        return 0;
    }
}
