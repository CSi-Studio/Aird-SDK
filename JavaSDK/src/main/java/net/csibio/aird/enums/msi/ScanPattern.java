/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.enums.msi;

import lombok.Getter;

@Getter
public enum ScanPattern{
    MEANDERING("MEANDERING", 1),
    FLY_BACK ("FLY BACK",2),
    RANDOM_ACCESS ("RANDOM ACCESS",3);

    final String name;
    final Integer code;

    ScanPattern(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
}