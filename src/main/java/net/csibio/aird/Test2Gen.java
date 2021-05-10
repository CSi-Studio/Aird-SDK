package net.csibio.aird;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.parser.DIAParser;

import java.util.List;
import java.util.TreeMap;

public class Test2Gen {
    public static void main(String[] args) {
        String indexFilePath ="/Users/jinyinwang/Documents/Test/QE-HFX-20190719_50cm_60min_DC18_4_1ddd.json";
//        String indexFilePath ="/Users/jinyinwang/Documents/stackTestData/DIA/C20181208yix_HCC_DIA_T_46A.json";
        DIAParser DIAParser = new DIAParser(indexFilePath);
        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
        BlockIndex index = swathIndexList.get(1);

        TreeMap<Float, MzIntensityPairs> spectrums = DIAParser.getSpectrums(index);

    }
}
