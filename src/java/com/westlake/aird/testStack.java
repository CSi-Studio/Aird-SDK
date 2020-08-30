package com.westlake.aird;

import com.westlake.aird.util.AirdScanUtil;

import java.io.File;
import java.util.List;

public class testStack {
    public static void main(String[] args) {

        List<File> files = AirdScanUtil.scanIndexFiles("\\\\ProproNas\\ProproNAS\\data\\Aird\\DIA\\ThermoQE");
        if (files == null || files.size() == 0) {
            return;
        }
//        for (File indexFile : files) {
//            System.out.println(indexFile.getAbsolutePath());
//            Parser airdParser = new DIAParser(indexFile.getAbsolutePath());
//            List<BlockIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
//
//            for (BlockIndex index : swathIndexList) {
//                MzIntensityPairs pairs = airdParser.getSpectrum(index, index.getRts().get(0));
//                MzIntensityPairs pair = airdParser.getSpectrum(index,index.getRts().get(20));
//                int i =pairs.getMzArray().length;
//                Float mz = pairs.getMzArray()[i];
//            }
//        }

    }
}
