/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System.Collections.Generic;

namespace AirdSDK.Enums
{
    public class ResultCodeEnum
    {
        /// <summary>
        /// ******
        /// 系统错误 System Error
        /// *******
        /// </summary>
        public static readonly ResultCodeEnum ERROR =
            new ResultCodeEnum("ERROR", InnerEnum.ERROR, "SYSTEM_ERROR", "系统繁忙,请稍后再试!");

        public static readonly ResultCodeEnum EXCEPTION =
            new ResultCodeEnum("EXCEPTION", InnerEnum.EXCEPTION, "SYSTEM_EXCEPTION", "系统繁忙,稍后再试!");

        public static readonly ResultCodeEnum IO_EXCEPTION =
            new ResultCodeEnum("IO_EXCEPTION", InnerEnum.IO_EXCEPTION, "IO_EXCEPTION", "文件读写错误");

        public static readonly ResultCodeEnum PARAMS_NOT_ENOUGH = new ResultCodeEnum("PARAMS_NOT_ENOUGH",
            InnerEnum.PARAMS_NOT_ENOUGH, "PARAMS_NOT_ENOUGH", "入参不齐");

        public static readonly ResultCodeEnum NOT_DIRECTORY = new ResultCodeEnum("NOT_DIRECTORY",
            InnerEnum.NOT_DIRECTORY, "THIS_PATH_IS_NOT_A_DIRECTORY_PATH", "该路径不是文件夹");

        public static readonly ResultCodeEnum DIRECTORY_NOT_EXISTS = new ResultCodeEnum("DIRECTORY_NOT_EXISTS",
            InnerEnum.DIRECTORY_NOT_EXISTS, "DIRECTORY_NOT_EXISTS", "该文件夹路径不存在");

        public static readonly ResultCodeEnum NOT_AIRD_INDEX_FILE = new ResultCodeEnum("NOT_AIRD_INDEX_FILE",
            InnerEnum.NOT_AIRD_INDEX_FILE, "THIS_FILE_IS_NOT_AIRD_INDEX_FILE", "该文件不是AIRD索引文件");

        public static readonly ResultCodeEnum AIRD_INDEX_FILE_PARSE_ERROR =
            new ResultCodeEnum("AIRD_INDEX_FILE_PARSE_ERROR", InnerEnum.AIRD_INDEX_FILE_PARSE_ERROR,
                "AIRD_INDEX_FILE_PARSE_ERROR", "索引文件解析失败");

        public static readonly ResultCodeEnum AIRD_FILE_PARSE_ERROR = new ResultCodeEnum("AIRD_FILE_PARSE_ERROR",
            InnerEnum.AIRD_FILE_PARSE_ERROR, "AIRD_FILE_PARSE_ERROR", "Aird文件解析失败");

        private static readonly List<ResultCodeEnum> valueList = new List<ResultCodeEnum>();

        static ResultCodeEnum()
        {
            valueList.Add(ERROR);
            valueList.Add(EXCEPTION);
            valueList.Add(IO_EXCEPTION);
            valueList.Add(PARAMS_NOT_ENOUGH);
            valueList.Add(NOT_DIRECTORY);
            valueList.Add(DIRECTORY_NOT_EXISTS);
            valueList.Add(NOT_AIRD_INDEX_FILE);
            valueList.Add(AIRD_INDEX_FILE_PARSE_ERROR);
            valueList.Add(AIRD_FILE_PARSE_ERROR);
        }

        public enum InnerEnum
        {
            ERROR,
            EXCEPTION,
            IO_EXCEPTION,
            PARAMS_NOT_ENOUGH,
            NOT_DIRECTORY,
            DIRECTORY_NOT_EXISTS,
            NOT_AIRD_INDEX_FILE,
            AIRD_INDEX_FILE_PARSE_ERROR,
            AIRD_FILE_PARSE_ERROR
        }

        public readonly InnerEnum innerEnumValue;
        private readonly string nameValue;
        private readonly int ordinalValue;
        private static int nextOrdinal = 0;

        private string code = "";
        private string message = "";

        private const long serialVersionUID = -799302222165012777L;

        /// <param name="code"> </param>
        /// <param name="message"> </param>
        internal ResultCodeEnum(string name, InnerEnum innerEnum, string code, string message)
        {
            this.code = code;
            this.message = message;

            nameValue = name;
            ordinalValue = nextOrdinal++;
            innerEnumValue = innerEnum;
        }

        public string Code
        {
            get { return code; }
        }

        public string Message
        {
            get { return message + "(" + code + ")"; }
        }


        public static ResultCodeEnum[] values()
        {
            return valueList.ToArray();
        }

        public int ordinal()
        {
            return ordinalValue;
        }

        public override string ToString()
        {
            return nameValue;
        }

        public static ResultCodeEnum valueOf(string name)
        {
            foreach (ResultCodeEnum enumInstance in ResultCodeEnum.valueList)
            {
                if (enumInstance.nameValue == name)
                {
                    return enumInstance;
                }
            }

            throw new System.ArgumentException(name);
        }
    }
}