package com.westlake.aird.visualize;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;


import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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


        String imgDir = "F:\\data\\images\\" + fileName.split("\\.")[0];
        File outDir = new File(imgDir);
        if (!outDir.exists() && !outDir.isDirectory()) {
            outDir.mkdir();
        }
        System.out.println(fileName.split("\\\\")[0]);

        String path = "F:\\data\\HYE110_6600_32_Fix\\";
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
                ScanToImage.transform(String.format("%s//%d.jpg", outSwathDir, (int)(rt*1000)),
                        pairs.getMzArray(),
                        pairs.getIntensityArray());
            });
        });
    }
}
