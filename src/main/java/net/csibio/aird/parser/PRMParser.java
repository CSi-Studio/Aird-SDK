/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.parser;

import net.csibio.aird.exception.ScanException;

/**
 * @see DIAParser
 * PRM Parser now can be replaced by DIA Parser because the have the same inner logic for spectra storage.
 */
public class PRMParser extends DIAParser {
    
    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public PRMParser(String indexFilePath) throws ScanException {
        super(indexFilePath);
    }
}
