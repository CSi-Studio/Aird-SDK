/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

namespace AirdSDK.Compressor
{
    public class ComboComp
    {
        public static byte[] encode(ByteComp byteComp, int[] target)
        {
            return byteComp.encode(ByteTrans.intToByte(target));
        }

        public static int[] decode(ByteComp byteComp, byte[] target)
        {
            return ByteTrans.byteToInt(byteComp.decode(target));
        }

        public static byte[] encode(IntComp intComp, ByteComp byteComp, int[] target)
        {
            return byteComp.encode(ByteTrans.intToByte(intComp.encode(target)));
        }

        public static int[] decode(IntComp intComp, ByteComp byteComp, byte[] target)
        {
            return intComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
        }

        public static byte[] encode(SortedIntComp sortedIntComp, ByteComp byteComp, int[] target)
        {
            return byteComp.encode(ByteTrans.intToByte(sortedIntComp.encode(target)));
        }

        public static int[] decode(SortedIntComp sortedIntComp, ByteComp byteComp, byte[] target)
        {
            return sortedIntComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
        }

        public static byte[] encode(BaseComp<int> intComp, ByteComp byteComp, int[] target)
        {
            return byteComp.encode(ByteTrans.intToByte(intComp.encode(target)));
        }

        public static int[] decode(BaseComp<int> intComp, ByteComp byteComp, byte[] target)
        {
            return intComp.decode(ByteTrans.byteToInt(byteComp.decode(target)));
        }
    }
}