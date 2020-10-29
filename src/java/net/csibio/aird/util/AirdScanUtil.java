/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import com.alibaba.fastjson.JSONObject;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.constant.SuffixConst;
import net.csibio.aird.constant.SymbolConst;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AirdScanUtil {

    /**
     * Scan a folder for all the aird index files(json format file) but not parse the json files
     * Recursive scanning under subfolders is not supported
     * The directory name will display as the Project name
     * <p>
     * 扫描一个文件夹下所有的Aird索引文件.但是不解析这些json文件
     * 不支持子文件夹的递归扫描
     * 文件夹名称会显示为项目名称
     *
     * @param directoryPath 文件夹路径
     * @return
     */
    public static List<File> scanIndexFiles(String directoryPath) {

        //Check and filter for effective aird files
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new ScanException(ResultCodeEnum.NOT_DIRECTORY);
        }
        if (!directory.exists()) {
            throw new ScanException(ResultCodeEnum.DIRECTORY_NOT_EXISTS);
        }

        List<File> indexFileList = new ArrayList<File>();
        File[] fileList = directory.listFiles();
        if (fileList == null) {
            return null;
        }

        //返回所有JSON文件的数组
        for (File file : fileList) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(SuffixConst.JSON)) {
                indexFileList.add(file);
            }
        }

        return indexFileList;
    }

    /**
     * load Aird Index Infomation from index file(JSON Format),and parse into AirdInfo
     *
     * @param indexFile
     * @return
     * @throws IOException
     * @see AirdInfo
     */
    public static AirdInfo loadAirdInfo(File indexFile) {
        String content = FileUtil.readFile(indexFile);
        AirdInfo airdInfo = null;
        try {
            airdInfo = JSONObject.parseObject(content, AirdInfo.class);
        } catch (Exception e) {
            System.out.println(indexFile.getAbsolutePath());
            System.out.println(ResultCodeEnum.NOT_AIRD_INDEX_FILE.getMessage());
            e.printStackTrace();
            return null;
        }

        return airdInfo;
    }

    public static List<AirdInfo> loadAirdInfoList(List<File> indexFiles) {
        List<AirdInfo> airdInfos = new ArrayList<AirdInfo>();
        for (File file : indexFiles) {
            AirdInfo airdInfo = loadAirdInfo(file);
            if (airdInfo != null) {
                airdInfos.add(airdInfo);
            } else {
                System.out.println(file.getAbsolutePath());
                System.out.println(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
            }
        }

        return airdInfos;
    }

    public static HashMap<String, AirdInfo> loadAirdInfoMap(List<File> indexFiles) {
        HashMap<String, AirdInfo> airdInfoMap = new HashMap<String, AirdInfo>();
        for (File file : indexFiles) {
            AirdInfo airdInfo = loadAirdInfo(file);
            if (airdInfo != null) {
                airdInfoMap.put(file.getAbsolutePath(), airdInfo);
            } else {
                System.out.println(file.getAbsolutePath());
                System.out.println(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
            }
        }

        return airdInfoMap;
    }

    public static String getIndexPathByAirdPath(String airdPath) {
        if (airdPath == null || airdPath.isEmpty() || !airdPath.contains(SymbolConst.DOT) || airdPath.endsWith(SuffixConst.AIRD)){
            return null;
        }
        return airdPath.substring(0, airdPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.JSON;
    }

    public static String getAirdPathByIndexPath(String indexPath) {
        if (indexPath == null || indexPath.isEmpty() || !indexPath.contains(SymbolConst.DOT) || indexPath.endsWith(SuffixConst.JSON)){
            return null;
        }
        return indexPath.substring(0, indexPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.AIRD;
    }

    public static boolean isAirdFile(String airdPath) {
        if (airdPath == null || airdPath.isEmpty() || !airdPath.contains(SymbolConst.DOT) || airdPath.endsWith(SuffixConst.AIRD)){
            return false;
        }
        return airdPath.toLowerCase().endsWith(SuffixConst.AIRD);
    }

    public static boolean isIndexFile(String indexPath) {
        if (indexPath == null || indexPath.isEmpty() || !indexPath.contains(SymbolConst.DOT) || indexPath.endsWith(SuffixConst.JSON)){
            return false;
        }
        return indexPath.toLowerCase().endsWith(SuffixConst.JSON);
    }
}
