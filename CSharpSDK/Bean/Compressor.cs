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
using CSharpFastPFOR.Port;

namespace AirdSDK.Beans
{
    public class Compressor
    {
        public static string TARGET_MZ = "mz";
        public static string TARGET_INTENSITY = "intensity";
        public static string TARGET_MOBILITY = "mobility";
        public static string TARGET_RT = "rt";

        /**
         * Compression target, support for mz and intensity
         * 压缩对象,支持mz和intensity两种
         */
        public string target;

        /**
         * The compression method, sorted by the used order.
         * 压缩对象使用的压缩方法列表,按照顺序进行压缩
         */
        public List<string> methods;

        /**
         * Compressor precision.
         * 1000: 0.001, 3dp
         * 10000: 0.0001, 4dp
         * 100000: 0.00001, 5dp
         * 10000000: 0.0000001, 7dp
         * mz压缩精度, 有1000,10000和100000三种,代表精确到小数点后3,4,5位
         * mobility压缩精度, 目前只有10000000一种,代表精确到小数点后7位
         */
        public int precision;

        /**
         * 2^digit = layers
         * if layers = 256, then digit=8
         */
        public int digit;

        /**
         * ByteOrder,Aird格式的默认ByteOrder为LITTLE_ENDIAN,此项为扩展项,目前仅支持默认值LITTLE_ENDIAN ByteOrder
         */
        public string byteOrder;

        public Compressor(string target)
        {
            this.target = target;
        }

        public void addMethod(string method)
        {
            if (methods == null)
            {
                methods = new List<string>();
            }

            methods.Add(method);
        }

        public ByteOrder fetchByteOrder()
        {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }
}