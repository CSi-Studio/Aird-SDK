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


        String imgDir = "C:\\Users\\zhang\\Documents\\Propro\\projet\\images\\" + fileName.split("\\.")[0];
        File outDir= new File(imgDir);
        if(!outDir.exists()  && !outDir.isDirectory()){
            outDir.mkdir();
        }
        System.out.println(fileName.split("\\\\")[0]);

        String path = "C:\\Users\\zhang\\Documents\\Propro\\projet\\data\\";
        File indexFile = new File(path+fileName);

        AtomicInteger rtCount = new AtomicInteger(0);
        AtomicInteger iter = new AtomicInteger(0);
        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

        for(int i = 0; i < swathIndexList.get(0).getRts().size(); i++){
            MzIntensityPairs pairs1 = airdParser.getSpectrum(swathIndexList.get(0), swathIndexList.get(0).getRts().get(i));
            System.out.println("scan to image " + i);
            ScanToImage.transform(String.format("%s//%d.jpg", imgDir, i),
                    pairs1.getMzArray(),
                    pairs1.getIntensityArray());

        }
    }
}
