/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean.common;

import lombok.Data;

/**
 * Pair for any type
 *
 * @param <T> type T
 * @param <R> type R
 */
@Data
public class AnyPair<T, R> {

    /**
     * Type T
     */
    T left;

    /**
     * Type R
     */
    R right;

    /**
     * 构造函数
     */
    public AnyPair() {
    }

    /**
     * 构造函数
     *
     * @param l value A
     * @param r value B
     */
    public AnyPair(T l, R r) {
        this.left = l;
        this.right = r;
    }
}
