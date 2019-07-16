package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import com.westlake.aird.util.AirdScanUtil;

import java.io.File;
import java.util.List;

public class SampleCode {

    public static void main(String[] args) {
        List<File> files = AirdScanUtil.scanIndexFiles("E:\\");
        if (files == null || files.size() == 0){
            return;
        }

        for(File airdIndexFile : files){
            System.out.println(airdIndexFile.getAbsolutePath());
            AirdParser airdParser = new AirdParser(airdIndexFile.getAbsolutePath());
            List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
            for(SwathIndex index : swathIndexList){
                MzIntensityPairs pairs = airdParser.getSpectrum(index, index.getRts().get(10));
                System.out.println("数组长度:"+pairs.getMzArray().length);
            }
        }
    }
}
