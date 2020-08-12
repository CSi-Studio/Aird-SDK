/*
 * Copyright (c) 2020 Propro Studio
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.bean;

import java.util.List;

public class Instrument {

    /**
     * 设备仪器厂商
     * Instrument manufacturer
     */
    public String manufacturer;

    public String ionisation;

    public String resolution;

    /**
     * 设备类型
     * Instrument Model
     */
    public String model;


    public List<String> source;

    /**
     * 分析方式
     */
    public List<String> analyzer;

    /**
     * 探测器
     */
    public List<String> detector;

    /**
     * 其他特征,使用K:V;K:V;K:V;类似的格式进行存储
     */
    public String features;

}
