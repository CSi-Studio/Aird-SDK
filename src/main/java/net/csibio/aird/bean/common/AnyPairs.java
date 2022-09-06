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

import java.io.Serializable;

/**
 * Pairs for any array typ
 *
 * @param <T> Type of the array A
 * @param <D> Type of the array B
 */
@Data
public class AnyPairs<T, D> implements Serializable {

    /**
     * instance x
     */
    T[] x;

    /**
     * instance y
     */
    D[] y;

    /**
     * 构造函数
     */
    public AnyPairs() {
    }

    /**
     * 构造函数
     *
     * @param x array X
     * @param y array Y
     */
    public AnyPairs(T[] x, D[] y) {
        this.x = x;
        this.y = y;
    }

    /**
     * the pairs length
     *
     * @return the pairs length
     */
    public int length() {
        if (x == null) {
            return 0;
        }
        return x.length;
    }

    /**
     * x instance
     *
     * @return the x instance
     */
    public T[] getX() {
        return x;
    }

    /**
     * y instance
     *
     * @return the y instance
     */
    public D[] getY() {
        return y;
    }
}
