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

import java.util.List;

@Data
public class Instrument {

    /**
     * 设备仪器厂商 Instrument manufacturer
     */
    String manufacturer;

    /**
     * 电离方式 Ionisation Method
     */
    String ionisation;

    /**
     * 仪器分辨率 The Instrument Resolution
     */
    String resolution;

    /**
     * 设备类型 Instrument Model
     */
    String model;

    /**
     * 来源 source
     */
    List<String> source;

    /**
     * 分析方式 Analyzer
     */
    List<String> analyzer;

    /**
     * 探测器 Detector
     */
    List<String> detector;

    /**
     * 其他特征,使用K:V;K:V;K:V;类似的格式进行存储 Other features, using K:V;K:V;K:V; to store the information
     */
    String features;

}
