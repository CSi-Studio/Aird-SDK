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
 * Integer compressor types
 */
public enum IntCompType {

    /**
     * 空压缩器
     */
    Empty(-1, "Empty"),

    /**
     * Binary Packing
     */
    BP(2, "BP"),

    /**
     * Variable Byte
     */
    VB(3, "VB"),


    BPVB(4, "BPVB"),
    ;

    /**
     * integer compressor code
     */
    public int code;

    /**
     * integer compressor name
     */
    public String name;

    IntCompType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * get by the name
     *
     * @param name compressor name
     * @return the compressor instance
     */
    public static IntCompType getByName(String name) {
        for (IntCompType value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return Empty;
    }

    /**
     * @return the code of the integer compressor
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the name of the integer compressor
     */
    public String getName() {
        return name;
    }
}
