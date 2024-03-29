/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.exception;

import net.csibio.aird.enums.ResultCodeEnum;

/**
 * Scan Exception
 */
public class ScanException extends RuntimeException {

    private static final long serialVersionUID = 4564124491192825748L;

    /**
     * result code
     */
    public ResultCodeEnum resultCode;

    /**
     * 构造函数
     */
    public ScanException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param resultCode result code
     */
    public ScanException(ResultCodeEnum resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
