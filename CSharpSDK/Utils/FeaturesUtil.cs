/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System.Collections;
using System.Text;

namespace AirdSDK.Utils;

public class FeaturesUtil
{
    public static char SP = ';';
    public static char SSP = ':';

    /**
         * 通过Map转换成String
         *
         * @param attrs
         * @return
         */
    public static string toString(Hashtable attrs)
    {
        StringBuilder sb = new StringBuilder();
        if (null != attrs && attrs.Count != 0)
        {
            foreach (DictionaryEntry entry in attrs)
            {
                string key = entry.Key.ToString();
                string val = entry.Value.ToString();
                sb.Append(key).Append(SSP).Append(val).Append(SP);
            }
        }

        return sb.ToString();
    }

    /**
     * 通过字符串解析成attributes
     *
     * @param str (格式比如为: "k:v;k:v;k:v")
     * @return
     */
    public static Hashtable toMap(string str)
    {
        Hashtable attrs = new Hashtable();
        if (str != null)
        {
            string[] arr = str.Split(SP);
            foreach (string kv in arr)
            {
                if (null != kv && "".Equals(kv))
                {
                    string[] ar = kv.Split(SSP);
                    if (ar.Length == 2)
                    {
                        attrs.Add(ar[0], ar[1]);
                    }
                }
            }
        }

        return attrs;
    }
}