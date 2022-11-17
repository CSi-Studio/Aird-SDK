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
    public abstract class IntComp : BaseComp<int>
    {
        public static IntComp build(IntCompType type)
        {
            switch (type)
            {
                case IntCompType.VB:
                    return new VarByteWrapper();
                case IntCompType.BP:
                    return new BinPackingWrapper();
                case IntCompType.Empty:
                    return new Empty();
                default: throw new System.Exception("No Implementation for " + type);
            }
        }

        public static IntCompType getType(string type)
        {
            switch (type)
            {
                case "VB":
                    return IntCompType.VB;
                case "BP":
                    return IntCompType.BP;
                case "Empty":
                    return IntCompType.Empty;
                default: throw new System.Exception("No Implementation for " + type);
            }
        }
    }
}