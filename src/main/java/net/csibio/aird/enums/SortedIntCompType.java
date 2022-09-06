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

public enum SortedIntCompType {

    /**
     * Integrated Binary Packing
     */
    IBP(0, "IBP"),

    /**
     * Integrated Variable Byte
     */
    IVB(1, "IVB"),

    Unknown(-999, "Unknown"),
    ;

    public String name;
    public int code;

    SortedIntCompType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SortedIntCompType getByName(String name) {
        for (SortedIntCompType value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return SortedIntCompType.Unknown;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
