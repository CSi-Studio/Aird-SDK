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

namespace AirdSDK.Compressor
{
    public class ByteTrans
    {
        //将short数组转化为byte数组
        public static byte[] shortToByte(short[] src)
        {
            var bytes = new byte[src.Length * 2];
            for (var i = 0; i < src.Length; i++)
            {
                BitConverter.GetBytes(src[i]).CopyTo(bytes, i * 2);
            }

            return bytes;
        }

        //将int数组转化为byte数组
        public static byte[] intToByte(int[] src)
        {
            var bytes = new byte[src.Length * 4];
            for (var i = 0; i < src.Length; i++)
            {
                BitConverter.GetBytes(src[i]).CopyTo(bytes, i * 4);
            }

            return bytes;
        }

        //将float数组转化为byte数组
        public static byte[] floatToByte(float[] src)
        {
            var bytes = new byte[src.Length * 4];
            for (var i = 0; i < src.Length; i++)
            {
                BitConverter.GetBytes(src[i]).CopyTo(bytes, i * 4);
            }

            return bytes;
        }

        //将byte数组转化为float数组,使用BigEndian进行转换
        public static float[] byteToFloat(byte[] src)
        {
            float[] fArray = new float[src.Length / 4];
            for (int i = 0; i < fArray.Length; i++)
            {
                fArray[i] = BitConverter.ToSingle(src, i * 4);
            }

            return fArray;
        }

        //将byte数组转化为float数组,使用BigEndian进行转换
        public static int[] byteToInt(byte[] src)
        {
            int[] fArray = new int[src.Length / 4];
            for (int i = 0; i < fArray.Length; i++)
            {
                fArray[i] = BitConverter.ToInt32(src, i * 4);
            }

            return fArray;
        }
    }
}