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
using System.IO;
using AirdSDK.Beans;
using AirdSDK.Constants;
using Newtonsoft.Json;

namespace AirdSDK.Utils;
public class AirdScanUtil
{
    /**
    * Scan a folder for all the aird index files(json format file) but not parse the json files
    * Recursive scanning under subfolders is not supported The directory name will display as the
    * Project name
    * <p>
    * 扫描一个文件夹下所有的Aird索引文件.但是不解析这些json文件 不支持子文件夹的递归扫描 文件夹名称会显示为项目名称
    *
    * @param directoryPath 文件夹路径
    * @return 该文件夹下所有的文件列表
    */
    public static List<FileInfo> scanIndexFiles(string directoryPath)
    {
        //Check and filter for effective aird files
        DirectoryInfo root = new DirectoryInfo(directoryPath);
        FileInfo[] fileList = root.GetFiles();
        List<FileInfo> indexFileList = new List<FileInfo>();

        if (fileList.Length == 0)
        {
            return null;
        }

        //返回所有JSON文件的数组
        foreach (FileInfo file in fileList)
        {
            if (file.Name.ToLower().EndsWith(SuffixConst.JSON))
            {
                indexFileList.Add(file);
            }
        }

        return indexFileList;
    }

    /**
    * load Aird Index Information from index file(JSON Format),and parse into AirdInfo
    *
    * @param indexFile 索引文件
    * @return 该索引文件内的JSON信息, 即AirdInfo信息
    */
    public static AirdInfo loadAirdInfo(FileInfo indexFile)
    {
        string content = FileUtil.readFile(indexFile);
        JsonSerializer serializer = new JsonSerializer();
        AirdInfo airdInfo = JsonConvert.DeserializeObject<AirdInfo>(content);
        return airdInfo;
    }

    /**
     * load Aird Index Information from index file(CJSON Format),and parse into ColumnInfo
     *
     * @param indexFile 索引文件
     * @return 该索引文件内的JSON信息, 即ColumnInfo信息
     */
    public static ColumnInfo loadColumnInfo(FileInfo indexFile) {
        String content = FileUtil.readFile(indexFile);
        JsonSerializer serializer = new JsonSerializer();
        ColumnInfo columnInfo = JsonConvert.DeserializeObject<ColumnInfo>(content);
        return columnInfo;
    }
    
    /**
     * 根据索引文件路径获取aird文件路径
     *
     * @param indexPath 索引文件路径
     * @return aird文件路径
     */
    public static string getAirdPathByIndexPath(string indexPath)
    {
        if (indexPath == null || !indexPath.Contains(SymbolConst.DOT) || !indexPath.EndsWith(SuffixConst.JSON))
        {
            return null;
        }

        return indexPath.Substring(0, indexPath.LastIndexOf(SymbolConst.DOT)) + SuffixConst.AIRD;
    }

    public static string getIndexPathByAirdPath(String airdPath)
    {
        if (airdPath == null || !airdPath.Contains(SymbolConst.DOT) || !airdPath.EndsWith(SuffixConst.AIRD))
        {
            return null;
        }

        return airdPath.Substring(0, airdPath.LastIndexOf(SymbolConst.DOT)) + SuffixConst.JSON;
    }
    
    /**
     * 根据索引文件路径获取aird文件路径
     *
     * @param indexPath 索引文件路径
     * @return aird文件路径
     */
    public static string getAirdPathByColumnIndexPath(string indexPath) {
        if (indexPath == null || !indexPath.Contains(SymbolConst.DOT) || !indexPath.EndsWith(SuffixConst.CJSON)) {
            return null;
        }
        return indexPath.Substring(0, indexPath.LastIndexOf(SymbolConst.DOT)) + SuffixConst.AIRD;
    }
}