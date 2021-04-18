/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.structure;

import lombok.Data;

/**
 * Try for Aird V2
 */
@Data
public class SortInt implements Comparable{
    int number;
    Integer layer; //二进制表达,表示所在的原层数

    public SortInt(int number, Integer layer){
        this.number = number;
        this.layer = layer;
    }

    @Override
    public int compareTo(Object obj) {
        SortInt sortInt = (SortInt)obj;
        return this.number - sortInt.number;
    }
}
