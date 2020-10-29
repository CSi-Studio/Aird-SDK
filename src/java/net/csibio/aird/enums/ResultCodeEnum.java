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

import java.io.Serializable;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-06-06 09:38
 */
public enum ResultCodeEnum implements Serializable {

    /**
     * ******
     * 系统错误 System Error
     * *******
     */
    ERROR("SYSTEM_ERROR", "系统繁忙,请稍后再试!"),
    EXCEPTION("SYSTEM_EXCEPTION", "系统繁忙,稍后再试!"),
    IO_EXCEPTION("IO_EXCEPTION", "文件读写错误"),
    PARAMS_NOT_ENOUGH("PARAMS_NOT_ENOUGH", "入参不齐"),

    NOT_DIRECTORY("THIS_PATH_IS_NOT_A_DIRECTORY_PATH", "该路径不是文件夹"),
    DIRECTORY_NOT_EXISTS("DIRECTORY_NOT_EXISTS", "该文件夹路径不存在"),

    NOT_AIRD_INDEX_FILE("THIS_FILE_IS_NOT_AIRD_INDEX_FILE", "该文件不是AIRD索引文件"),
    AIRD_INDEX_FILE_PARSE_ERROR("AIRD_INDEX_FILE_PARSE_ERROR", "索引文件解析失败"),
    AIRD_FILE_PARSE_ERROR("AIRD_FILE_PARSE_ERROR", "Aird文件解析失败"),

    ;


    private String code = "";
    private String message = "";

    private static final long serialVersionUID = -799302222165012777L;

    /**
     * @param code
     * @param message
     */
    ResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message + "(" + code + ")";
    }

}
