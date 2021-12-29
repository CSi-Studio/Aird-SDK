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

public enum MsType {

    PROFILE("PROFILE"),
    THRESHOLDED("THRESHOLDED"),
    CENTROIDED("CENTROIDED"),
    ;
    public String name;

    MsType(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static MsType getType(String typeName){
        return valueOf(typeName);
    }
}
