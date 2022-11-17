/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using AirdSDK.Enums;

namespace AirdSDK.Compressor
{
    public abstract class SortedIntComp : BaseComp<int>
    {
        // /**
        // * Apply differential coding (in-place).
        // */
        // public static int[] delta(int[] data)
        // {
        //     if (data.Length == 0)
        //     {
        //         return new int[0];
        //     }
        //
        //     int[] output = new int[data.Length];
        //     output[0] = data[0];
        //     for (int i = data.Length - 1; i > 0; --i)
        //     {
        //         output[i] = data[i] - data[i - 1];
        //     }
        //
        //     return output;
        // }
        //
        // /**
        //  * Undo differential coding (in-place). 
        //  */
        // public static int[] inverseDelta(int[] data)
        // {
        //     if (data.Length == 0)
        //     {
        //         return new int[0];
        //     }
        //
        //     int[] output = new int[data.Length];
        //     output[0] = data[0];
        //     for (int i = 1; i < data.Length; ++i)
        //     {
        //         output[i] = data[i] + data[i - 1];
        //     }
        //
        //     return output;
        // }

        public static SortedIntComp build(SortedIntCompType type)
        {
            switch (type)
            {
                case SortedIntCompType.IBP:
                    return new IntegratedBinPackingWrapper();
                case SortedIntCompType.IVB:
                    return new IntegratedVarByteWrapper();
                case SortedIntCompType.Delta:
                    return new DeltaWrapper();
                default: throw new System.Exception("No Implementation for " + type);
            }
        }

        public static SortedIntCompType getType(string type)
        {
            switch (type)
            {
                case "IBP":
                    return SortedIntCompType.IBP;
                case "IVB":
                    return SortedIntCompType.IVB;
                case "Delta":
                    return SortedIntCompType.Delta;
                default: throw new System.Exception("No Implementation for " + type);
            }
        }
    }
}