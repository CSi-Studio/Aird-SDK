package net.csibio.aird;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.parser.DIAParser;

import java.util.List;
import java.util.TreeMap;

public class Test2Gen {
    public static void main(String[] args) {

    }

    public void testForDIA(){
        String indexFilePath ="/Users/jinyinwang/Documents/Test/QE-HFX-20190719_50cm_60min_DC18_4_1.json";
        DIAParser DIAParser = new DIAParser(indexFilePath);
        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
        BlockIndex index = swathIndexList.get(1);
        long t = System.currentTimeMillis();
        TreeMap<Float, MzIntensityPairs> spectrums = DIAParser.getSpectrums(index);
        System.out.println("Gen time:" + (System.currentTimeMillis() - t));

        String indexFilePath2 = "/Users/jinyinwang/Documents/Test/QE-HFX-20190719_50cm_60min_DC18_4_1ddd.json";
        DIAParser DIAParser2 = new DIAParser(indexFilePath2);
        List<BlockIndex> swathIndexList2 = DIAParser2.getAirdInfo().getIndexList();
        BlockIndex index2 = swathIndexList2.get(1);
        long t2 = System.currentTimeMillis();
        TreeMap<Float, MzIntensityPairs> spectrums2 = DIAParser2.getSpectrums(index2);
        System.out.println("Gen2 time:" + (System.currentTimeMillis() - t2));
    }

}
