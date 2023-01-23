/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

namespace AirdSDK.Enums
{
    public static class AirdType
    {
        public const string DIA_PASEF = "DIA_PASEF";
        public const string DDA_PASEF = "DDA_PASEF";
        public const string PRM_PASEF = "PRM_PASEF";
        public const string DIA = "DIA"; // Precursor-mz -> MS2 Group
        public const string PRM = "PRM"; // Precursor-mz -> MS2 Group
        public const string SCANNING_SWATH = "SCANNING_SWATH"; // 暂未支持
        public const string DDA = "DDA"; // MS1 -> MS2 Group
        public const string MRM = "MRM"; // Chromatogram
    }
}