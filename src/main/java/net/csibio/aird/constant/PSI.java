/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.constant;

public class PSI {

    // Scan start time
    /**
     * Constant <code>MS_RT_SCAN_START="1000016"</code>
     */
    public static final String MS_RT_SCAN_START = "1000016"; // "scan start time"
    /**
     * Constant <code>MS_RT_RETENTION_TIME="1000894"</code>
     */
    public static final String MS_RT_RETENTION_TIME = "1000894"; // "retention time"
    /**
     * Constant <code>MS_RT_RETENTION_TIME_LOCAL="1000895"</code>
     */
    public static final String MS_RT_RETENTION_TIME_LOCAL = "1000895"; // "local retention time"
    /**
     * Constant <code>MS_RT_RETENTION_TIME_NORMALIZED="1000896"</code>
     */
    public static final String MS_RT_RETENTION_TIME_NORMALIZED = "1000896"; // "normalized
    // retention time"

    // MS level
    /**
     * Constant <code>cvMSLevel="1000511"</code>
     */
    public static final String cvMSLevel = "1000511";
    /**
     * Constant <code>cvMS1Spectrum="1000579"</code>
     */
    public static final String cvMS1Spectrum = "1000579";

    // m/z and charge state
    /**
     * Constant <code>cvMz="1000040"</code>
     */
    public static final String cvMz = "1000040";
    /**
     * Constant <code>cvChargeState="1000041"</code>
     */
    public static final String cvChargeState = "1000041";

    // Minutes unit. 1000038 is used in mzML 1.0, while UO:000003 is used in
    // mzML 1.1.0
    /**
     * Constant <code>cvUnitsMin1="1000038"</code>
     */
    public static final String cvUnitsMin1 = "1000038";
    /**
     * Constant <code>cvUnitsMin2="UO:0000031"</code>
     */
    public static final String cvUnitsMin2 = "UO:0000031";
    /**
     * Constant <code>cvUnitsSec="UO:0000010"</code>
     */
    public static final String cvUnitsSec = "UO:0000010";

    // Scan filter string
    /**
     * Constant <code>cvScanFilterString="1000512"</code>
     */
    public static final String cvScanFilterString = "1000512";

    // Precursor m/z.
    /**
     * Constant <code>cvPrecursorMz="1000744"</code>
     */
    public static final String cvPrecursorMz = "1000744";

    // Polarity
    /**
     * Constant <code>cvPolarityPositive="1000130"</code>
     */
    public static final String cvPolarityPositive = "1000130";
    /**
     * Constant <code>cvPolarityNegative="1000129"</code>
     */
    public static final String cvPolarityNegative = "1000129";

    // Centroid vs profile
    /**
     * Constant <code>cvCentroidSpectrum="1000127"</code>
     */
    public static final String cvCentroidSpectrum = "1000127";
    /**
     * Constant <code>cvProfileSpectrum="1000128"</code>
     */
    public static final String cvProfileSpectrum = "1000128";

    // Total Ion Current
    /**
     * Constant <code>cvTIC="1000285"</code>
     */
    public static final String cvTIC = "1000285";

    // m/z range
    /**
     * Constant <code>cvLowestMz="1000528"</code>
     */
    public static final String cvLowestMz = "1000528";
    /**
     * Constant <code>cvHighestMz="1000527"</code>
     */
    public static final String cvHighestMz = "1000527";

    // Scan window range

    /**
     * Constant <code>cvScanWindowUpperLimit="1000500"</code>
     */
    public static final String cvScanWindowUpperLimit = "1000500";
    /**
     * Constant <code>cvScanWindowLowerLimit="1000501"</code>
     */
    public static final String cvScanWindowLowerLimit = "1000501";

    // Chromatograms
    /**
     * Constant <code>cvChromatogramTIC="1000235"</code>
     */
    public static final String cvChromatogramTIC = "1000235";
    /**
     * Constant <code>cvChromatogramMRM_SRM="1001473"</code>
     */
    public static final String cvChromatogramMRM_SRM = "1001473";
    /**
     * Constant <code>cvChromatogramSIC="1000627"</code>
     */
    public static final String cvChromatogramSIC = "1000627";
    /**
     * Constant <code>cvChromatogramBPC="1000628"</code>
     */
    public static final String cvChromatogramBPC = "1000628";

    // Activation
    /// activation methods
    /**
     * Constant <code>cvActivationEnergy="1000045"</code>
     */
    public static final String cvActivationEnergy = "1000045";
    public static final String cvPercentCollisionEnergy = "1000138";
    public static final String cvActivationEnergy2 = "1000509";
    /// activation energies
    /**
     * Constant <code>cvActivationCID="1000133"</code>
     */
    public static final String cvActivationCID = "1000133";
    public static final String cvElectronCaptureDissociation = "1000250";
    public static final String cvHighEnergyCID = "1000422";
    public static final String cvLowEnergyCID = "1000433";

    // Isolation
    /**
     * Constant <code>cvIsolationWindowTarget="1000827"</code>
     */
    public static final String cvIsolationWindowTarget = "1000827";
    /**
     * Constant <code>cvIsolationWindowLowerOffset="1000828"</code>
     */
    public static final String cvIsolationWindowLowerOffset = "1000828";
    /**
     * Constant <code>cvIsolationWindowUpperOffset="1000829"</code>
     */
    public static final String cvIsolationWindowUpperOffset = "1000829";

    // Data arrays
    /**
     * Constant <code>cvMzArray="1000514"</code>
     */
    public static final String cvMzArray = "1000514";
    /**
     * Constant <code>cvIntensityArray="1000515"</code>
     */
    public static final String cvIntensityArray = "1000515";
    /**
     * Constant <code>cvRetentionTimeArray="1000595"</code>
     */
    public static final String cvRetentionTimeArray = "1000595";

    // UV spectrum, actually "electromagnetic radiation spectrum"
    /**
     * Constant <code>cvUVSpectrum="1000804"</code>
     */
    public static final String cvUVSpectrum = "1000804";

    // Intensity array unit
    /**
     * Constant <code>cvUnitsIntensity1="1000131"</code>
     */
    public static final String cvUnitsIntensity1 = "1000131";

    // Ion mobility
    // <cvParam cvRef="MS" accession="1002476" name="ion mobility drift time" value="4.090608"
    // unitCvRef="UO" unitAccession="UO:0000028" unitName="millisecond"/>
    public static final String cvMobilityDriftTime = "1002476";
    //    public static final String cvMobilityDriftTimeUnit = "UO:0000028";
    // <cvParam cvRef="MS" accession="1002815" name="inverse reduced ion mobility"
    // value="1.572618927197" unitCvRef="MS" unitAccession="1002814" unitName="volt-second per
    // square centimeter"/>
    public static final String cvMobilityInverseReduced = "1002815";
    public static final String cvMobilityInverseReducedUnit = "1002814";

    public static final String cvBasePeakMz = "1000504";
    public static final String cvBasePeakIntensity = "1000505";
}
