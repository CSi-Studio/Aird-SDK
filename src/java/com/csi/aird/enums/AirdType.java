/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.csi.aird.enums;

public enum AirdType {

    COMMON("COMMON", 0), // RT Indexing
    DIA_SWATH("DIA_SWATH", 1), // Precursor-mz -> MS2 Group
    DDA("DDA",2), //MS1 -> MS2 Group
    PRM("PRM",3), //Precursor-mz -> MS2 Group
    SCANNING_SWATH("SCANNING_SWATH", 4)
    ; //

    public Integer code;
    public String name;

    AirdType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public Integer getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public static AirdType getType(String typeName){
        return valueOf(typeName);
    }
}
