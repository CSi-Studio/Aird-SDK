/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.csi.aird.visualize;

import com.csi.aird.bean.BlockIndex;
import com.csi.aird.api.DIAParser;
import com.csi.aird.bean.MzIntensityPairs;

import java.io.File;
import java.util.List;

/**
 * 数据图像化测试
 */
public class VisualizeTest {
    /**
     * 对Aird文件中所有swath进行压缩
     * @param airdParser Aird文件解析器
     * @param swathIndexList swathIndexList
     * @param imgDir 图片存放路径
     */
    public static void testForAllSwath(DIAParser airdParser, List<BlockIndex> swathIndexList, String imgDir){
        swathIndexList.forEach(index -> {
            String imgSwathDir = imgDir+"\\"+index.getStartPtr()+"_MS"+index.getLevel();
            System.out.println(index.getStartPtr());
            File outSwathDir = new File(imgSwathDir);
            if (!outSwathDir.exists() && !outSwathDir.isDirectory()) {
                outSwathDir.mkdir();
            }
            index.getRts().parallelStream().forEach(rt -> {
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                System.out.println("scan to image " + rt*1000);
                String imgPath = String.format("%s\\%d.png", outSwathDir, (int)(rt*1000));
                float[] mzArray = pairs.getMzArray();
                float[] intArray = pairs.getIntensityArray();
                new ImageWriter().write(imgPath,
                        mzArray,
                        intArray);
//                System.out.println(imgPath);
            });
        });
    }

    /**
     * 对一个swath进行读写测试，如果存储的数据与原数据有差，print出现差错的文件名和原数据与解析后的数据
     * @param airdParser Aird文件解析器
     * @param swathIndexList swathIndexList
     * @param swath 要测试的swath的位置
     */
    public static void testForOneSwath(DIAParser airdParser,List<BlockIndex> swathIndexList, int swath){
        BlockIndex index = swathIndexList.get(swath);
        String imgSwathDir = "D:\\Propro\\projet\\images\\OneSwathTest\\images";
        File outSwathDir = new File(imgSwathDir);
        if (!outSwathDir.exists() && !outSwathDir.isDirectory()) {
            outSwathDir.mkdir();
        }
        List<Float> rts = index.getRts();
        for (int i = 0; i < rts.size(); i++) {
            MzIntensityPairs pairs = airdParser.getSpectrum(index, rts.get(i));
            String imgPath = String.format("%s\\%d", outSwathDir, i);
            float[] mzArray = pairs.getMzArray();
            float[] intArray = pairs.getIntensityArray();
            //写入图片
            new ImageWriter().write2(imgPath,
                    mzArray,
                    intArray);
            System.out.println(imgPath);
            //从图片中读出数据
//            ImageReader reader = new ImageReader();
//            reader.read(imgPath);
//            float[] mzGet = reader.getMzArray();
//            float[] intGet = reader.getIntensityArray();
//            //print出现错误的数据
//            for (int j = 0; j < mzArray.length; j++) {
//                if(mzArray[j] - mzGet[j] >= 0.001 || intArray[j] - intGet[j] >= 0.001){
//                    System.out.println(
//                            imgPath + "   "+ mzArray[j] + "-" + intArray[j] + " -> " + mzGet[j] + "-" + intGet[j]);
//                }
//            }
        }
    }

    /**
     * 测试对一张谱图的读写，print出原数据与解析后的数据
     * @param airdParser Aird文件解析器
     * @param swathIndexList swathIndexList
     * @param swath 要测试的swath的位置
     * @param rt 要测试的帧的位置
     */
    public static void testForOneScan(DIAParser airdParser,List<BlockIndex> swathIndexList, int swath, int rt){
        BlockIndex index = swathIndexList.get(swath);
        String imgSwathDir = "D:\\Propro\\projet\\images\\OneScanTest";
        File outSwathDir = new File(imgSwathDir);
        if (!outSwathDir.exists() && !outSwathDir.isDirectory()) {
            outSwathDir.mkdir();
        }
        List<Float> rts = index.getRts();
        MzIntensityPairs pairs = airdParser.getSpectrum(index, rts.get(rt));
        String imgPath = String.format("%s\\%d.png", outSwathDir, rt);
        float[] mzArray = pairs.getMzArray();
        float[] intArray = pairs.getIntensityArray();
        //写入图片
        new ImageWriter().write(imgPath,
                mzArray,
                intArray);
        //从图片中读出数据
        ImageReader reader = new ImageReader();
        reader.read(imgPath);
        float[] mzGet = reader.getMzArray();
        float[] intGet = reader.getIntensityArray();
        //print数据
        for (int i = 0; i < mzArray.length; i++) {
            System.out.println(
                    mzArray[i] + "-" + intArray[i] + " -> " + mzGet[i] + "-" + intGet[i]);
        }
    }

    public static void main(String[] args) {
        String fileName =
                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
//                "C20181205yix_HCC_DIA_N_38A.json";
//                "HYE124_TTOF5600_64var_lgillet_L150206_007.json";
//                "napedro_L120224_010_SW.json";
//                "C20181208yix_HCC_DIA_T_46A_with_zero_lossless.json";
//                "D20181207yix_HCC_SW_T_46A_with_zero_lossless.json";
//                "HYE110_TTOF6600_32fix_lgillet_I160308_001_with_zero_lossless.json";

        String imgDir = "D:\\Propro\\projet\\images\\" + fileName.split("\\.")[0];
        File outDir = new File(imgDir);
        if (!outDir.exists() && !outDir.isDirectory()) {
            outDir.mkdir();
        }
        System.out.println(fileName.split("\\\\")[0]);
        String path = "D:\\Propro\\projet\\data\\";
        File indexFile = new File(path + fileName);

        DIAParser airdParser = new DIAParser(indexFile.getAbsolutePath());
        List<BlockIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
//        VisualizeTest.testForAllSwath(airdParser, swathIndexList, imgDir);
        VisualizeTest.testForOneSwath(airdParser, swathIndexList, 10);
//        VisualizeTest.testForOneScan(airdParser, swathIndexList, 1, 1);
//        videoTest(airdParser, swathIndexList, 11);
    }

    public static void videoTest(DIAParser airdParser,List<BlockIndex> swathIndexList, int swath){
        BlockIndex index = swathIndexList.get(swath);
        String imgSwathDir = "D:\\Propro\\projet\\images\\OneSwathVideoTest";
        File outSwathDir = new File(imgSwathDir);
        if (!outSwathDir.exists() && !outSwathDir.isDirectory()) {
            outSwathDir.mkdir();
        }
        String fileName = String.format("%s\\%s_gray_6600_swath_%d", outSwathDir, index.getStartPtr(), swath);

        VideoParser videoParser = new VideoParser();
        videoParser.writeToVideo(fileName, airdParser, index);
//        ImageReader imageReader = new ImageReader();
//        imageReader.readVideo(fileName);
    }
}
