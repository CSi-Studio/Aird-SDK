/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird;

import com.westlake.aird.api.DIAParser;
import com.westlake.aird.bean.BlockIndex;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.util.AirdScanUtil;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SampleCode {


    public static void main(String[] args) {
        List<File> files = AirdScanUtil.scanIndexFiles("\\ProproNas\\ProproNAS\\data\\Aird\\HCC_SCIEX_ALL");
        if (files == null || files.size() == 0) {
            return;
        }

        for (File indexFile : files) {
            System.out.println(indexFile.getAbsolutePath());
            DIAParser airdParser = new DIAParser(indexFile.getAbsolutePath());
            List<BlockIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

            for (BlockIndex index : swathIndexList) {
                MzIntensityPairs pairs = airdParser.getSpectrum(index, index.getRts().get(10));
                Float[][] location = new Float[2000][1000];
                int[] rgb = new int[2000 * 1000];
                for (int i = 0; i < pairs.getMzArray().length; i++) {
                    Float mz = pairs.getMzArray()[i];
                    int x = mz.intValue();
                    int y = (int) ((mz - x) * 1000);
                    location[x][y] = pairs.getIntensityArray()[i];
                }

                for (int i = 0; i < 2000; i++) {
                    for (int j = 0; j < 1000; j++) {
//                        if (location[i][j] != null) {
                            rgb[i * j] = 256;
//                        }
                    }
                }
                BufferedImage image = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_RGB);
                image.setRGB(0, 0, 2000, 1000, rgb, 0, 2000);
                saveImage(image, "D:\\image\\" + index.getStartPtr() + ".jpg");
            }
        }
    }

    public static Boolean saveImage(BufferedImage productImage, String path) {
        try {
            File outputFile = new File(path);
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                Boolean isSuccess = outputFile.createNewFile();
                if (!isSuccess) {
                    return false;
                }
            }

            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(1);

            writer.setOutput(ImageIO.createImageOutputStream(outputFile));
            writer.write(null, new IIOImage(productImage, null, null), iwp);
            writer.dispose();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
