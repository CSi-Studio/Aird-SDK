/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.enums;

/**
 * Activation Method
 */
public enum ActivationMethod {

    /**
     * CID Activation Method
     */
    CID("CID", "collision induced dissociation", "eV"),

    /**
     * HCD Activation Method
     */
    HCD("HCD", "higher-energy C-trap dissociation", "a.u."),

    /**
     * ECD Activation Method
     */
    ECD("ECD", "electron capture dissociation", ""),

    /**
     * ETD Activation Method
     */
    ETD("ETD", "electron transfer dissociation", ""),

    /**
     * Unknown Activation Method
     */
    UNKNOWN("N.A.", "Unknown", "");

    /**
     * Activation Name
     */
    private final String name;

    /**
     * Activation Description
     */
    private final String desc;

    /**
     * Activation Unit
     */
    private final String unit;

    /**
     * 构造函数
     *
     * @param name activation name
     * @param unit activation unit
     * @param desc activation description
     */
    ActivationMethod(String name, String unit, String desc) {
        this.desc = desc;
        this.name = name;
        this.unit = unit;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the desc
     */
    public String getUnit() {
        return unit;
    }
}
