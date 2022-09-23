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

namespace AirdSDK.Beans
{
    /**
     * Description of any manipulation (from the first conversion to aird format until the creation of the current aird instance document) applied to the data.
     */
    public class DataProcessing
    {
        /**
         * Any additional manipulation not included elsewhere in the dataProcessing element.
         */
        List<string> processingOperations;

        public void addProcessingOperation(string processingOperation)
        {
            if (processingOperations == null)
            {
                processingOperations = new List<string>();
            }

            processingOperations.Add(processingOperation);
        }
    }
}