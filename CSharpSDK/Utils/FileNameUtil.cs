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
using System.IO;
using System.Linq;
using AirdSDK.Constants;

namespace AirdSDK.Utils;

// public class SymbolConst
// {
//     public static string COMMA = ",";
//     public static string TAB = "\t";
//     public static string DOT = ".";
//     public static string CHANGE_LINE = "\r\n";
// }
public class FileNameUtil
{
    public static string parseFileName(string inputFilePath)
    {
        var outputFileName = string.Empty;

        try
        {
            if (string.IsNullOrEmpty(outputFileName))
            {
                outputFileName = Path.GetFileNameWithoutExtension(inputFilePath) ?? string.Empty;
            }

            // this list is for Windows; it's a superset of the POSIX list
            const string illegalChar = "\\/*:?<>|\"";
            foreach (var illegal in illegalChar)
            {
                if (outputFileName.Contains(illegal))
                {
                    outputFileName = outputFileName.Replace(illegal, '_');
                }
            }

            string newFilename = outputFileName;
            return newFilename;
        }
        catch (ArgumentException e)
        {
            throw new ArgumentException(
                string.Format("error generating output filename for input file '{0}' and output run id '{1}'",
                    inputFilePath, outputFileName), e);
        }
    }

    /**
        * 根据索引文件路径获取aird文件路径
        * @param indexPath 索引文件路径
        * @return aird文件路径
        */
    public static string getAirdPathByIndexPath(string indexPath)
    {
        if (string.IsNullOrEmpty(indexPath) || !indexPath.Contains(SymbolConst.DOT) ||
            !(Path.GetExtension(indexPath).ToLower() == SuffixConst.JSON))
        {
            return null;
        }

        return indexPath.Substring(0, indexPath.LastIndexOf(SymbolConst.DOT)) + SuffixConst.AIRD;
    }

    /// <summary>
    /// 根据aird文件路径获取对应的索引文件路径 </summary>
    /// <param name="airdPath"> aird文件路径 </param>
    /// <returns> 索引文件路径 </returns>
    public static string getIndexPathByAirdPath(string airdPath)
    {
        if (string.IsNullOrEmpty(airdPath) || !airdPath.Contains(SymbolConst.DOT) ||
            !airdPath.EndsWith(SuffixConst.AIRD, StringComparison.Ordinal))
        {
            return null;
        }

        return airdPath.Substring(0, airdPath.LastIndexOf(SymbolConst.DOT)) + SuffixConst.JSON;
    }

    /// <summary>
    /// 判断aird文件是否存在
    /// </summary>
    /// <param name="path">aird文件或索引文件路径</param>
    /// <returns></returns>
    public static bool airdFileVerification(string path)
    {
        bool value = File.Exists(path);
        if (path.EndsWith(SuffixConst.AIRD, StringComparison.Ordinal))
        {
            value = value && File.Exists(getIndexPathByAirdPath(path));
        }
        else if (path.EndsWith(SuffixConst.JSON, StringComparison.Ordinal))
        {
            value = value && File.Exists(getAirdPathByIndexPath(path));
        }

        return value;
    }

    /// <summary>
    /// 根据aird或index文件地址获取index地址
    /// </summary>
    /// <param name="path">aird或index地址</param>
    /// <returns></returns>
    public static string getIndexPath(string path)
    {
        if (!airdFileVerification(path))
        {
            return null;
        }

        if (path.EndsWith(SuffixConst.AIRD, StringComparison.Ordinal))
        {
            return getIndexPathByAirdPath(path);
        }

        return path;
    }
}