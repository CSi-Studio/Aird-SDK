/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.constant;

public class Features {

    /**
     * 原始文件中前体的窗口大小,未经过overlap参数调整,precursor mz
     */
    public static String original_width = "owid";

    /**
     * 原始文件中前体的荷质比窗口开始位置,未经过overlap参数调整,precursor mz
     */
    public static String original_precursor_mz_start = "oMzStart";

    /**
     * 原始文件中前体的荷质比窗口结束位置,未经过overlap参数调整,precursor mz
     */
    public static String original_precursor_mz_end = "oMzEnd";

    /**
     * 从Vendor文件中得到的msd.id
     */
    public static String raw_id = "rawId";

    /**
     * 是否忽略Intensity为0的键值对,默认为true
     */
    public static String ignore_zero_intensity = "ignoreZeroIntensity";

    /**
     * SWATH的各个窗口间的重叠部分
     */
    public static String overlap = "overlap";

    /**
     * irt所得斜率
     */
    public static String slope = "slope";

    /**
     * irt所得截距
     */
    public static String intercept = "intercept";

}
