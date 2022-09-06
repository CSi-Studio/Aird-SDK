/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * data processing information
 */
@Data
public class DataProcessing {

    /**
     * Any additional manipulation not included elsewhere in the dataProcessing element.
     */
    List<String> processingOperations;

    /**
     * Any additional manipulation not included elsewhere in the dataProcessing element.
     *
     * @param processingOperation processing operations name
     */
    public void addProcessingOperation(String processingOperation) {
        if (processingOperations == null) {
            processingOperations = new ArrayList<String>();
        }
        processingOperations.add(processingOperation);
    }
}
