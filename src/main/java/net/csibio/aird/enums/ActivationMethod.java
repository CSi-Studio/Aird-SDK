/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.enums;

public enum ActivationMethod {

    CID("CID", "collision induced dissociation", "eV"),
    HCD("HCD", "higher-energy C-trap dissociation", "a.u."),
    ECD("ECD", "electron capture dissociation", ""),
    ETD("ETD", "electron transfer dissociation", ""),
    UNKNOWN("N.A.", "Unknown", "");

    private final String name;
    private final String desc;
    private final String unit;

    ActivationMethod(String name, String unit, String desc) {
        this.desc = desc;
        this.name = name;
        this.unit = unit;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }
}
