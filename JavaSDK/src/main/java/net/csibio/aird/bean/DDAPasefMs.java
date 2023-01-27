/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;
import net.csibio.aird.bean.common.Spectrum;

import java.io.Serializable;
import java.util.List;

/**
 * New Structure for MsCycle every ms mean ms1 spectrum with related ms2 spectra
 */
@Data
public class DDAPasefMs implements Serializable {

    private static final long serialVersionUID = -123222L;

    /**
     * num for current scan
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
     * the filter string for current scan
     */
    String filterString;

    /**
     * the polarity for current scan, Negative, Position
     */
    String polarity;

    /**
     * the activator for current scan, CID, HCD...
     */
    String activator;

    /**
     * the activator energy;
     */
    Float energy;

    /**
     * the mass spectrum type. Profile or Centroid
     */
    String msType;

    /**
     * injection time for single spectrum
     */
    Float injectionTime;

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
    List<DDAPasefMs> ms2List;

    /**
     * 构造函数
     */
    public DDAPasefMs() {
    }

    /**
     * 构造函数
     *
     * @param rt       retention time
     * @param spectrum spectrum
     */
    public DDAPasefMs(Double rt, Spectrum spectrum) {
        this.rt = rt;
        this.spectrum = spectrum;
    }
}
