/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.sample;

import net.csibio.aird.AirdManager;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.AirdScanUtil;
import net.csibio.aird.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Sample Code for Aird SDK
 */
public class SampleCode {

    public static void main(String[] args) {

    }

    /**
     * 读取某一个文件夹下所有的Aird Index文件
     */
    public static void scanDIAFiles(){
        List<File> files = AirdScanUtil.scanIndexFiles("E:\\data\\SGS");
        files.forEach(file -> {
            AirdManager.getInstance().load(file.getPath());
        });
    }

    /**
     * 读取一个Aird文件的元数据文件,并且根据文件中的Block块索引信息读取Aird文件中的光谱图信息
     */
    public static void getAirdInfo(){
        DIAParser parser = new DIAParser("D:\\pData\\HYE4_64_fix\\HYE110_TTOF6600_64fix_lgillet_I160310_001.json");
        AirdInfo airdInfo = parser.getAirdInfo();
        System.out.println(airdInfo.getVersion());
        System.out.println(airdInfo.getType());

        MzIntensityPairs pairs = parser.getSpectrum(0);
        System.out.println(pairs.getMzArray().length+"");
        airdInfo.getIndexList().forEach(blockIndex -> {
            TreeMap<Float, MzIntensityPairs> map = parser.getSpectrums(blockIndex);
            System.out.println(map.size());
        });
    }
}
