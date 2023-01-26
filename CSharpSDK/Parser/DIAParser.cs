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
using AirdSDK.Enums;

namespace AirdSDK.Parser;

public class DIAParser : BaseParser
{
    public DIAParser(string indexFilePath) : base(indexFilePath)
    {
    }

    public DIAParser(string indexFilePath, AirdInfo airdInfo) : base(indexFilePath, airdInfo)
    {
    }

    /**
     * 构造函数
     *
     * @param airdPath      aird file path
     * @param mzCompressor  mz compressor
     * @param intCompressor intensity compressor
     * @throws ScanException scan exception
     */
    public DIAParser(string airdPath, Beans.Compressor mzCompressor, Beans.Compressor intCompressor) : base(airdPath,
        mzCompressor, intCompressor, null, AcquisitionMethod.DIA)
    {
    }
}