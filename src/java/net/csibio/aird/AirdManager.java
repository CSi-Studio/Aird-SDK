/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird;

import net.csibio.aird.parser.BaseParser;

import java.util.concurrent.ConcurrentHashMap;

public class AirdManager {

    public static AirdManager instance = new AirdManager();

    // key为path, value为该path下的文件的parser对象
    public ConcurrentHashMap<String, BaseParser> parserMap = new ConcurrentHashMap<>();

    private AirdManager() {
    }

    public static AirdManager getInstance() {
        return instance;
    }

    public BaseParser load(String indexPath) {
        BaseParser parser = new BaseParser(indexPath);
        parserMap.put(indexPath, parser);
        return parser;
    }

    /**
     * 如果是使用的本函数,则在ParserMap里面使用自定义的indexId作为Key值
     *
     * @param indexPath aird索引文件路径
     * @param indexId 外部设定的key值
     * @return 返回解析器
     */
    public BaseParser load(String indexPath, String indexId) {
        BaseParser parser = new BaseParser(indexPath);
        parserMap.put(indexId, parser);
        return parser;
    }

    public BaseParser getParser(String indexPath) {
        return parserMap.get(indexPath);
    }

    public BaseParser touchParser(String indexPath) {
        BaseParser parser = parserMap.get(indexPath);
        if (parser == null) {
            return load(indexPath);
        }
        return parser;
    }

    public void removeParser(String indexPath) {
        parserMap.remove(indexPath);
    }

    public void clearParser() {
        parserMap.values().forEach(BaseParser::close);
        parserMap.clear();
    }
}
