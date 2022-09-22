/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System;

namespace AirdSDK.Constants;

public class Features
{
    /**
    * 原始文件中前体的窗口大小,未经过overlap参数调整,precursor mz The precursor width in the vendor file, which is not
    * fixed by overlap
    */
    public static String original_width = "owid";

    /**
     * 原始文件中前体的荷质比窗口开始位置,未经过overlap参数调整,precursor mz The precursor mz start in the vendor file, which
     * is not fixed by overlap
     */
    public static String original_precursor_mz_start = "oMzStart";

    /**
     * 原始文件中前体的荷质比窗口结束位置,未经过overlap参数调整,precursor mz The precursor mz end in the vendor file, which is
     * not fixed by overlap
     */
    public static String original_precursor_mz_end = "oMzEnd";

    /**
     * 从Vendor文件中得到的msd.id msd.id from the vendor file
     */
    public static String raw_id = "rawId";

    /**
     * 是否忽略Intensity为0的键值对,默认为true If ignore the zero intensity pairs
     */
    public static String ignore_zero_intensity = "ignoreZeroIntensity";

    /**
     * SWATH的各个窗口间的重叠部分 the overlap between adjacent swath windows
     */
    public static String overlap = "overlap";

    /**
     * irt所得斜率 the slope value by IRT algorithm
     */
    public static String slope = "slope";

    /**
     * irt所得截距 the intercept value by IRT algorithm
     */
    public static String intercept = "intercept";
}