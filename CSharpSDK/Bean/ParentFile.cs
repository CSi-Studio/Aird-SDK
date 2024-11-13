/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

using System;

namespace AirdSDK.Beans
{
    /**
     * Path to all the ancestor files (up to the native acquisition file) used to generate the current Aird instance document.
     */
    public class ParentFile
    {
        /**
         * Name of the parent file
         */
        public string name;

        /**
         * Location of the parent file
         */
        public string location;

        /**
         * 文件格式
         */
        public string formatType;
        
        public ParentFileProto ToProto()
        {
            ParentFileProto proto = new();
            if (name != null)
            {
                proto.Name = name;
            }
            if (location != null)
            {
                proto.Location = location;
            }
            if (formatType != null)
            {
                proto.FormatType = formatType;
            }
            
            return proto;
        }
        
        public static ParentFile FromProto(ParentFileProto proto)
        {
            if (proto == null)
                throw new ArgumentNullException(nameof(proto));

            return new ParentFile
            {
                name = proto.Name,
                location = proto.Location,
                formatType = proto.FormatType
            };
        }
    }
}