/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import com.alibaba.fastjson2.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.ColumnIndex;
import net.csibio.aird.bean.ColumnInfo;
import net.csibio.aird.constant.SuffixConst;
import net.csibio.aird.constant.SymbolConst;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;

/**
 * Aird Scan Util for scanning the aird files quickly
 */
public class AirdScanUtil {

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
        HashSet<String> fileNameSet = new HashSet<>();
        HashMap<String, File> indexFileMap = new HashMap<>();
        //返回所有JSON文件的数组
        for (File file : fileList) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(SuffixConst.JSON)) {
                indexFileMap.put(file.getName().toLowerCase(), file);
            }
        }
        //再扫描一遍后缀为.index的索引文件,如果检测到有.index格式的索引文件,则优先使用该索引文件
        for (File file : fileList) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(SuffixConst.INDEX)) {
                indexFileMap.put(file.getName().toLowerCase(), file);
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
    public static AirdInfo loadAirdInfo(String indexFile) {
        AirdInfo airdInfo = new AirdInfo();
        if (indexFile.toLowerCase().endsWith(SuffixConst.JSON)) {
            File file = new File(indexFile);
            if (file.exists() && file.canRead()) {
                String content = FileUtil.readFile(indexFile);
                try {
                    airdInfo = JSON.parseObject(content, AirdInfo.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }

                return airdInfo;
            }
        } else if (indexFile.toLowerCase().endsWith(SuffixConst.INDEX)) {
            try {
                FileInputStream fis = new FileInputStream(indexFile);
                net.csibio.aird.bean.proto.AirdInfo.AirdInfoProto proto = net.csibio.aird.bean.proto.AirdInfo.AirdInfoProto.parseFrom(fis);
                airdInfo = AirdInfo.fromProto(proto);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return airdInfo;
    }

    /**
     * load Aird Index Information from index file(JSON Format),and parse into ColumnInfo
     *
     * @param indexFile 索引文件
     * @return 该索引文件内的JSON信息, 即ColumnInfo信息
     */
    public static ColumnInfo loadColumnInfo(String indexFile) {
        ColumnInfo columnInfo = new ColumnInfo();
        if (indexFile.toLowerCase().endsWith(SuffixConst.CJSON)) {
            String content = FileUtil.readFile(indexFile);
            try {
                columnInfo = JSON.parseObject(content, ColumnInfo.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else if (indexFile.toLowerCase().endsWith(SuffixConst.CINDEX)) {
            try {
                FileInputStream fis = new FileInputStream(indexFile);
                net.csibio.aird.bean.proto.ColumnInfo.ColumnInfoProto proto = net.csibio.aird.bean.proto.ColumnInfo.ColumnInfoProto.parseFrom(fis);
                columnInfo = ColumnInfo.fromProto(proto);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return columnInfo;
    }

    /**
     * 批量加载索引文件 Batch Load index files
     *
     * @param indexFiles 索引文件列表
     * @return 索引文件JSON列表
     */
    public static List<AirdInfo> loadAirdInfoList(List<File> indexFiles) {
        List<AirdInfo> airdInfos = new ArrayList<AirdInfo>();
        for (File file : indexFiles) {
            AirdInfo airdInfo = loadAirdInfo(file.getAbsolutePath());
            if (airdInfo != null) {
                airdInfos.add(airdInfo);
            } else {
                System.out.println(file.getAbsolutePath());
                System.out.println(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
            }
        }

        return airdInfos;
    }

    /**
     * 批量加载索引文件,以Map的形式返回JSON文件列表
     *
     * @param indexFiles 索引文件列表
     * @return key为索引文件的path, value为JSON格式的索引文件
     */
    public static HashMap<String, AirdInfo> loadAirdInfoMap(List<File> indexFiles) {
        HashMap<String, AirdInfo> airdInfoMap = new HashMap<String, AirdInfo>();
        for (File file : indexFiles) {
            AirdInfo airdInfo = loadAirdInfo(file.getAbsolutePath());
            if (airdInfo != null) {
                airdInfoMap.put(file.getAbsolutePath(), airdInfo);
            } else {
                System.out.println(file.getAbsolutePath());
                System.out.println(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
            }
        }

        return airdInfoMap;
    }

    /**
     * 根据aird文件路径获取对应的索引文件路径
     * if proto index exists, return proto index first
     * @param airdPath aird文件路径
     * @return 索引文件路径
     */
    public static String getIndexPathByAirdPath(String airdPath) {
        if (airdPath == null || !airdPath.contains(SymbolConst.DOT) || !airdPath.endsWith(SuffixConst.AIRD)) {
            return null;
        }
        String protoIndex = airdPath.substring(0, airdPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.INDEX;
        File file = new File(protoIndex);
        if (file.exists()) {
            return protoIndex;
        } else {
            return airdPath.substring(0, airdPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.JSON;
        }
    }

    /**
     * 根据aird文件路径获取对应的索引文件路径
     *
     * @param airdPath aird文件路径
     * @return 索引文件路径
     */
    public static String getProtoIndexPathByAirdPath(String airdPath) {
        if (airdPath == null || !airdPath.contains(SymbolConst.DOT) || !airdPath.endsWith(SuffixConst.AIRD)) {
            return null;
        }
        return airdPath.substring(0, airdPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.INDEX;
    }

    /**
     * 根据aird文件路径获取对应的索引文件路径
     *
     * @param airdPath aird文件路径
     * @return 索引文件路径
     */
    public static String getJsonIndexPathByAirdPath(String airdPath) {
        if (airdPath == null || !airdPath.contains(SymbolConst.DOT) || !airdPath.endsWith(SuffixConst.AIRD)) {
            return null;
        }
        return airdPath.substring(0, airdPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.JSON;
    }

    public static String getProtoPathByCjsonPath(String jsonPath) {
        if (jsonPath == null || !jsonPath.contains(SymbolConst.DOT) || !jsonPath.endsWith(SuffixConst.CJSON)) {
            return null;
        }
        return jsonPath.substring(0, jsonPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.INDEX;
    }

    /**
     * 根据索引文件路径获取aird文件路径
     *
     * @param indexPath 索引文件路径
     * @return aird文件路径
     */
    public static String getAirdPathByIndexPath(String indexPath) {
        if (indexPath != null && (indexPath.toLowerCase().endsWith(SuffixConst.CINDEX) || indexPath.toLowerCase().endsWith(SuffixConst.CJSON) ||
                indexPath.toLowerCase().endsWith(SuffixConst.INDEX) || indexPath.toLowerCase().endsWith(SuffixConst.JSON))) {
            return indexPath.substring(0, indexPath.lastIndexOf(SymbolConst.DOT)) + SuffixConst.AIRD;
        }
        return null;
    }

    /**
     * 是否是aird文件路径
     *
     * @param airdPath aird文件路径
     * @return 是否是aird文件路径
     */
    public static boolean isAirdFile(String airdPath) {
        if (airdPath == null || !airdPath.contains(SymbolConst.DOT) || !airdPath.endsWith(SuffixConst.AIRD)) {
            return false;
        }
        return airdPath.toLowerCase().endsWith(SuffixConst.AIRD);
    }

    /**
     * 是否是索引文件路径
     *
     * @param indexPath 索引文件路径
     * @return 是否是索引文件路径
     */
    public static boolean isIndexFile(String indexPath) {
        if (indexPath == null || !indexPath.contains(SymbolConst.DOT) || !indexPath.endsWith(SuffixConst.JSON)) {
            return false;
        }
        return indexPath.toLowerCase().endsWith(SuffixConst.JSON);
    }
}
