/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System.Runtime.InteropServices;

namespace AirdSDK.Utils;

public class TdfUtil
{
    /**
   * Open data set.
   * <p>
   * On success, returns a non-zero instance handle that needs to be passed to subsequent API calls,
   * in particular to the required call to tims_close().
   *
   * @param analysis_dir the name of the directory in the file system that * contains the analysis
   *                     data, in UTF-8 encoding.
   * @param use_recalib  if non-zero, use the most recent recalibrated state * of the analysis, if
   *                     there is one; if zero, use the original "raw" calibration * written during
   *                     acquisition time.
   * @return On failure, returns 0, and you can use tims_get_last_error_string() to obtain a string
   * describing the problem.
   */
    [DllImport("timsdata.dll")]
    public static extern long tims_open(string analysis_dir, long use_recalib);

    /**
     * Close data set.
     *
     * @param handle btained by tims_open(); passing 0 is ok and has no effect.
     * @return not documented
     */
    [DllImport("timsdata.dll")]
    public static extern void tims_close(long handle);

    /**
     * mobility transformation: convert back and forth between (possibly non-integer) scan numbers and
     * 1/K0 values.
     *
     * @param handle    see {@link TDFLibrary#tims_open(String, long)}.
     * @param frameId   from .tdf SQLite: Frames.Id
     * @param scannum   in: array of values
     * @param oneOverK0 out: array of values
     * @param len       number of values to convert (arrays must have corresponding size)
     * @return 1 on success, 0 on failure
     */
    [DllImport("timsdata.dll")]
    public static extern long tims_scannum_to_oneoverk0(long handle, long frameId, double[] scannum, double[] oneOverK0,
        long len);

    /**
     * Converts the 1/K0 value to CCS (in Angstrom^2) using the Mason-Shamp equation
     *
     * @param ook0   the 1/K0 value in Vs/cm2
     * @param charge the charge
     * @param mz     the mz of the ion
     * @return the CCS value in Angstrom^2
     */
    [DllImport("timsdata.dll")]
    public static extern double tims_oneoverk0_to_ccs_for_mz(double ook0, long charge, double mz);

    /**
     * Converts the CCS (in Angstrom^2) to 1/K0 using the Mason-Shamp equation
     *
     * @param ccs    the ccs value in Angstrom^2
     * @param charge the charge
     * @param mz     the mz of the ion
     * @return the 1/K0 value in Vs/cm2
     **/
    [DllImport("timsdata.dll")]
    public static extern double tims_ccs_to_oneoverk0_for_mz(double ccs, long charge, double mz);
}