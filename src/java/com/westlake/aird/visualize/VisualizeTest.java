package com.westlake.aird.visualize;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;


import java.io.File;
import java.util.HashMap;
import java.util.List;

public class VisualizeTest {
    public static void main(String[] args) {
        String fileName =
                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
//                "C20181205yix_HCC_DIA_N_38A.json";
//                "HYE124_TTOF5600_64var_lgillet_L150206_007.json";
//                "napedro_L120224_010_SW.json";
//                "C20181208yix_HCC_DIA_T_46A_with_zero_lossless.json";
//                "D20181207yix_HCC_SW_T_46A_with_zero_lossless.json";
//                "HYE110_TTOF6600_32fix_lgillet_I160308_001_with_zero_lossless.json";


        String imgDir = "C:\\Users\\ADMIN\\Documents\\Propro\\projet\\images\\" + fileName.split("\\.")[0];
        File outDir = new File(imgDir);
        if (!outDir.exists() && !outDir.isDirectory()) {
            outDir.mkdir();
        }
        System.out.println(fileName.split("\\\\")[0]);

        String path = "C:\\Users\\ADMIN\\Documents\\Propro\\projet\\data\\";
        File indexFile = new File(path + fileName);

        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

        swathIndexList.forEach(index -> {
            String imgSwathDir = imgDir+"\\"+index.getStartPtr()+"_MS"+index.getLevel();
            File outSwathDir = new File(imgSwathDir);
            if (!outSwathDir.exists() && !outSwathDir.isDirectory()) {
                outSwathDir.mkdir();
            }
            index.getRts().forEach(rt -> {
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                System.out.println("scan to image " + rt*1000);

                String imgPath = String.format("%s\\%d.jpg", outSwathDir, (int)(rt*1000));
                float[] mzArray = pairs.getMzArray();
                float[] intArray = pairs.getIntensityArray();
                ImageTool.scanToImage(imgPath,
                        mzArray,
                        intArray);

                float[][] imgData = ImageTool.imageToScan(imgPath);

                assert imgData[0].length == mzArray.length;

                for (int i = 0; i < intArray.length; i++) {
                    if(mzArray[i] - imgData[0][i] > 0.001 || intArray[i] - imgData[1][i] > 0.001)
                        System.out.println(String.format("(%f, %f) -> (%f, %f)",
                                mzArray[i],
                                intArray[i],
                                imgData[0][i],
                                imgData[1][i]));
                }

                System.out.println(imgPath);


            });
        });

        /*
        MzIntensityPairs pairs = airdParser.getSpectrum( swathIndexList.get(1), swathIndexList.get(1).getRts().get(2000));
        String imgPath = "C:\\Users\\ADMIN\\Documents\\Propro\\projet\\images\\test.png";
        float[] mzArray = pairs.getMzArray();
        float[] intArray = pairs.getIntensityArray();
        ImageTool.scanToImage(imgPath,
                mzArray,
                intArray);

        HashMap<Float, Float> imgData = ImageTool.imageToScan(imgPath);
        double mse = 0;
        for (int i = 0; i < mzArray.length; i++) {
            if(imgData.containsKey(mzArray[i])) {
                mse += Math.pow(intArray[i] - imgData.get(mzArray[i]), 2);
//                System.out.println("mz found: " + mzArray[i] + "->" + intArray[i] + "from image: " + imgData.get(mzArray[i]));
            }
            else {
                double decPart = (mzArray[i] - Math.floor(mzArray[i]));
                System.out.println("mz missed: " + decPart + "->" +(int)(1000*decPart));
            }
        }
        System.out.println(imgPath + " -> MSE: " + mse);
        */
    }
}
