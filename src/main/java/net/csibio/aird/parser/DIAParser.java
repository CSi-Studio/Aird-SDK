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

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.exception.ScanException;

/**
 * DIA Parser
 */
public class DIAParser extends BaseParser {

    /**
     * 构造函数
     *
     * @param indexFilePath index file path
     * @throws ScanException scan exception
     */
    public DIAParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    public DIAParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
        super(indexFilePath, airdInfo);
    }

    /**
     * 构造函数
     *
     * @param airdPath      aird file path
     * @param mzCompressor  mz compressor
     * @param intCompressor intensity compressor
     * @throws ScanException scan exception
     */
    public DIAParser(String airdPath, Compressor mzCompressor, Compressor intCompressor) throws Exception {
        super(airdPath, mzCompressor, intCompressor, null, AirdType.DIA.getName());
    }
}
