/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.csi.aird;

import com.csi.aird.bean.AirdInfo;
import com.csi.aird.parser.BaseParser;

import java.util.concurrent.ConcurrentHashMap;

public class AirdClient {

    public static AirdClient instance = new AirdClient();

    // key为path, value为该path下的文件的parser对象
    public ConcurrentHashMap<String, BaseParser> parserMap = new ConcurrentHashMap<>();

    private AirdClient() { }

    public static AirdClient getInstance() {
        return instance;
    }

    public BaseParser load(String indexPath){
        BaseParser parser = new BaseParser(indexPath);
        parserMap.put(indexPath, parser);
        return parser;
    }

    public BaseParser getParser(String indexPath){
        return parserMap.get(indexPath);
    }

    public BaseParser touchParser(String indexPath){
        BaseParser parser = parserMap.get(indexPath);
        if (parser == null){
            return load(indexPath);
        }
        return parser;
    }

    public void removeParser(String indexPath){
        parserMap.remove(indexPath);
    }

    public void clearParser(){
        parserMap.clear();
    }
}
