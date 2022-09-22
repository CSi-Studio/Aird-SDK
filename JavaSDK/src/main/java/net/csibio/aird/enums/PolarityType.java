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
 * Represents the polarity of ionization.
 */
public enum PolarityType {

    POSITIVE("POSITIVE", 1), NEGATIVE("NEGATIVE", -1), NEUTRAL("NEUTRAL", 0),
    ;

    private final int code;
    private final String name;

    PolarityType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static PolarityType fromCode(int code) {
        for (PolarityType p : values()) {
            if (p.getCode() == code) {
                return p;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
