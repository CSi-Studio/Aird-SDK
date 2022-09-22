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
using System.Collections.Generic;
using AirdSDK.Beans.Common;

namespace AirdSDK.Beans
{
    public class DDAMs
    {
        /**
      * order number for current spectrum
      */
        public int num;

        /**
         * Retention Time, unit: reference from raw file, default is second required
         */
        public double rt;

        /**
         * the tic data for current scan
         */
        public long tic;

        /**
         * cvList for current scan
         */
        public List<CV> cvList;

        /**
         * the window range for current scan
         */
        public WindowRange range;

        /**
         * the ms1 spectrum data pairs required
         */
        public Spectrum spectrum;

        /**
         * related ms2 list
         */
        public List<DDAMs> ms2List;

        public DDAMs()
        {
        }

        public DDAMs(double rt, Spectrum spectrum)
        {
            this.rt = rt;
            this.spectrum = spectrum;
        }
    }
}