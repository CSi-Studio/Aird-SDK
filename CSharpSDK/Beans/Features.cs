/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

namespace AirdSDK.Beans
{
    public class Features
    {
        //原始文件中前体的窗口大小,未经过overlap参数调整,precursor mz
        public static string original_width = "owid";

        //原始文件中前体的荷质比窗口开始位置,未经过overlap参数调整,precursor mz
        public static string original_precursor_mz_start = "oMzStart";

        //原始文件中前体的荷质比窗口结束位置,未经过overlap参数调整,precursor mz
        public static string original_precursor_mz_end = "oMzEnd";

        //从Vendor文件中得到的msd.id
        public static string raw_id = "rawId";

        //是否忽略Intensity为0的键值对,默认为true
        public static string ignore_zero_intensity = "ignoreZeroIntensity";

        //SWATH的各个窗口间的重叠部分
        public static string overlap = "overlap";

        //源文件数据格式
        public static string source_file_format = "sourceFileFormat";

        //Aird文件版本码
        public static string aird_version = "aird_version";

        //进行zlib压缩时使用的byteOrder编码,C#默认使用的是LITTLE_ENDIAN
        public static string byte_order = "byte_order";

        //使用的Aird核心压缩算法,有ZDPD, StackZDPD两种
        public static string aird_algorithm = "aird_algorithm";
    }
}