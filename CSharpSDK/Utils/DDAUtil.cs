/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using AirdSDK.Beans;
namespace AirdSDK.Utils;

public class DDAUtil
{
    public static void initFromIndex(DDAMs ms, BlockIndex index, int loc)
    {
        if (index.nums != null && index.nums.Count > 0)
        {
            ms.num = index.nums[loc];
        }

        if (index.cvList != null && index.cvList.Count > 0)
        {
            ms.cvList = index.cvList[loc];
        }

        if (index.tics != null && index.tics.Count > 0)
        {
            ms.tic = index.tics[loc];
        }

        if (index.rangeList != null && index.rangeList.Count > 0)
        {
            ms.range = index.rangeList[loc];
        }
    }

    public static void initFromIndex(DDAPasefMs ms, BlockIndex index, int loc)
    {
        if (index.nums != null && index.nums.Count > 0)
        {
            ms.num = index.nums[loc];
        }

        if (index.cvList != null && index.cvList.Count > 0)
        {
            ms.cvList = index.cvList[loc];
        }

        if (index.tics != null && index.tics.Count > 0)
        {
            ms.tic = index.tics[loc];
        }

        if (index.rangeList != null && index.rangeList.Count > 0)
        {
            ms.range = index.rangeList[loc];
        }
    }
}