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
public enum ScanSequence{
    TOP_DOWN("TOP DOWN", 1),
    BOTTOM_UP ("BOTTOM UP",2),
    LEFT_RIGHT ("LEFT RIGHT",3),
    RIGHT_LEFT ("RIGHT LEFT",4);

    final String name;
    final Integer code;

    ScanSequence(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
}