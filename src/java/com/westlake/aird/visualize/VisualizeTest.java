package com.westlake.aird.visualize;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;


import java.io.File;
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
            index.getRts().parallelStream().forEach(rt -> {
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
//                System.out.println("scan to image " + rt*1000);

                String imgPath = String.format("%s\\%d.png", outSwathDir, (int)(rt*1000));
                float[] mzArray = pairs.getMzArray();
                float[] intArray = pairs.getIntensityArray();
                try{
                    ImageTool.scanToImage(imgPath,
                            mzArray,
                            intArray);

                }catch (Exception e){
                    System.out.println(imgPath);
                    for (int i = 0; i < mzArray.length; i++) {
                        System.out.println(mzArray[i] + "->" + intArray[i]);
                    }
                    e.printStackTrace();
                }

            System.out.println(imgPath);

            });
        });


    }
}
