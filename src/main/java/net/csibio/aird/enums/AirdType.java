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
 * Aird Type
 */
public enum AirdType {

    /**
     * RT Indexing
     */
    COMMON("COMMON", 0),

    /**
     * Precursor-mz -> MS2 Group
     */
    DIA("DIA", 1),

    /**
     * MS1 -> MS2 Group
     */
    DDA("DDA", 2),

    /**
     * Precursor-mz -> MS2 Group
     */
    PRM("PRM", 3),

    /**
     * Not Support now
     */
    SCANNING_SWATH("SCANNING_SWATH", 4),

    /**
     * PASEF for DIA
     */
    DIA_PASEF("DIA_PASEF", 5),

    /**
     * PASEF for DDA
     */
    DDA_PASEF("DDA_PASEF", 6),

    /**
     * PASEF for PRM,Not Support now
     */
    PRM_PASEF("PRM_PASEF", 7),
    ;

    /**
     * Aird Type code
     */
    public Integer code;

    /**
     * Aird Type name
     */
    public String name;

    /**
     * 构造函数
     *
     * @param name type name
     * @param code type code
     */
    AirdType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    /**
     * get type by type name
     *
     * @param typeName the type name
     * @return the type instance
     */
    public static AirdType getType(String typeName) {
        return valueOf(typeName);
    }

    /**
     * @return the type code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return the type name
     */
    public String getName() {
        return name;
    }
}
