package net.csibio.aird;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Layers;
import net.csibio.aird.bean.MsCycle;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.StackCompressUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Test2Gen {
//    public static void main(String[] args) throws Exception {
//        String indexFilePath = "/Users/jinyinwang/Documents/Test/C20181208yix_HCC_DIA_T_46A_1st.json";
//        String indexFilePath2 = "/Users/jinyinwang/Documents/Test/C20181208yix_HCC_DIA_T_46A_2st.json";
//        String filePath = "/Users/jinyinwang/Documents/Test/C20181208yix_HCC_DIA_T_46A.csv";
//
//        DIAParser DIAParser = new DIAParser(indexFilePath);
//        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
//
////        for (int i = 0; i < swathIndexList.size(); i++) {
////            BlockIndex index = swathIndexList.get(i);
////            long t = System.currentTimeMillis();
////            TreeMap<Float, MzIntensityPairs> spectrums = DIAParser.getSpectrums(index);
////            System.out.println("Gen1 time:" + (System.currentTimeMillis() - t));
////
////
////            BlockIndex index2 = swathIndexList2.get(i);
////            long t2 = System.currentTimeMillis();
////            TreeMap<Float, MzIntensityPairs> spectrums2 = DIAParser2.getSpectrums(index2);
////            System.out.println("Gen2 time:" + (System.currentTimeMillis() - t2));
////
////            boolean a = true;
////            for (float rt : spectrums.keySet()) {
////                a = a && isMzSizeSame(spectrums.get(rt), spectrums2.get(rt));
////                if (!a) {
////                    break;
////                }
////            }
////            System.out.println("block" + i + a);
////        }
//
//        int blockNum = swathIndexList.size();
//        long[] mzSize = new long[blockNum];
//        long[] mzSize2 = new long[blockNum];
//        long[] tagSize = new long[blockNum];
//        long[] intSize = new long[blockNum];
//        long[] intSize2 = new long[blockNum];
//
//        long[] mzSizeLayer = new long[blockNum];
//        long[] tagSizeLayer = new long[blockNum];
//        long[] tagSizeLayerShift = new long[blockNum];
//
//
//        for (int i = 0; i < swathIndexList.size(); i++) {
//            BlockIndex index = swathIndexList.get(i);
//            mzSize[i] = getMzSize(index);
//            intSize[i] = getIntSize(index);
//            List<Float> rts = index.getRts();
//            int mzNum = rts.size();
//            List<int[]> mzGroup = new ArrayList<>();
//            for (Float rt : rts) {
//                int[] arr;
//                arr = DIAParser.getSpectrumAsInteger(index, rt).getMz();
//                mzGroup.add(arr);
//            }
//            int arrNum = 256;
//            int groupNum = (mzNum - 1) / arrNum + 1;
//            List<Layers> layersList = new ArrayList<>();
//            List<Layers> layersList2 = new ArrayList<>();
//            int fromIndex = 0;
//            for (int j = 0; j < groupNum - 1; j++) {
//                List<int[]> arrGroup = mzGroup.subList(fromIndex, fromIndex + arrNum);
//                Layers layers = StackCompressUtil.stackEncode(arrGroup, true);
//                layersList.add(layers);
//                Layers layers2 = StackCompressUtil.stackEncode(arrGroup, true,false);
//                layersList2.add(layers2);
//                fromIndex += arrNum;
//            }
//            //处理余数
//            List<int[]> arrGroup = mzGroup.subList(fromIndex, mzNum);
//            Layers stackRemainder = StackCompressUtil.stackEncode(arrGroup, false);
//            layersList.add(stackRemainder);
//            Layers stackRemainder2 = StackCompressUtil.stackEncode(arrGroup, false,false);
//            layersList2.add(stackRemainder2);
//
//            for (Layers layers : layersList) {
//                tagSizeLayerShift[i] += layers.getTagArray().length;
//                mzSizeLayer[i] += layers.getMzArray().length;
//            }
//            for (Layers layers : layersList2) {
//                tagSizeLayer[i] += layers.getTagArray().length;
//            }
//            System.out.println("block"+i+" finished!");
//        }
//
//        DIAParser DIAParser2 = new DIAParser(indexFilePath2);
//        List<BlockIndex> swathIndexList2 = DIAParser2.getAirdInfo().getIndexList();
//        for (int i = 0; i < swathIndexList2.size(); i++) {
//            BlockIndex index = swathIndexList2.get(i);
//            mzSize2[i] = getMzSize(index);
//            tagSize[i] = getTagSize(index);
//            intSize2[i] = getIntSize(index);
//        }
//
//        File file = new File(filePath);
//        FileWriter out = new FileWriter(file);
//        out.write(" blockNum,mzSize,mzSize2,tagSize,intSize,intSize2");
//        out.write(",mzSizeLayer,tagSizeLayerShift,tagSizeLayer");
//        out.write("\r\n");
//        for (int i = 0; i < mzSize.length; i++) {
//            out.write(i + "," + mzSize[i] + "," + mzSize2[i] + "," + tagSize[i] + "," + intSize[i] + "," + intSize2[i]);
//            out.write("," + mzSizeLayer[i] + "," + tagSizeLayerShift[i]+","+tagSizeLayer[i]);
//            out.write("\r\n");
//        }
//        out.close();
//
////        DDAParser DDAParser = new DDAParser(indexFilePath);
////        List<BlockIndex> swathIndexList = DDAParser.getAirdInfo().getIndexList();
////        List<MsCycle> msCycles = DDAParser.parseToMsCycle1st();
////
////        DDAParser DDAParser2 = new DDAParser(indexFilePath2);
////        List<BlockIndex> swathIndexList2 = DDAParser2.getAirdInfo().getIndexList();
////        List<MsCycle> msCycles2 = DDAParser2.parseToMsCycle();
////
////        for (int i = 0; i < msCycles.size(); i++) {
////            msCycles.get(i).getMs1Spectrum().getMzArray().equals(msCycles2.get(i).getMs1Spectrum().getMzArray());
////        }
////        System.out.println("ok");
//
//    }
public static void main(String[] args) throws IOException {
    String indexFilePath2 = "/Users/jinyinwang/Documents/Test/C20181208yix_HCC_DIA_T_46A_2st.json";
    DIAParser DIAPaser = new DIAParser(indexFilePath2);
    BlockIndex index = DIAPaser.getAirdInfo().getIndexList().get(1);
    MzIntensityPairs pair = DIAPaser.getSpectrumAsInteger(index, (float) 240.651);
    int[] mzArray = pair.getMz();
    File file = new File("/Users/jinyinwang/Documents/Test/数据分布C20181208.csv");
    FileWriter out = new FileWriter(file);
    for (int i = 0; i < mzArray.length; i++) {
        out.write(mzArray[i]+"\r\n");
    }
    out.close();

}


    public static boolean isMzSizeSame(MzIntensityPairs pairs1, MzIntensityPairs pairs2) {
        if (pairs1.getMzArray().length == pairs2.getMzArray().length) {
            return true;
        } else {
            return false;
        }
    }

    public static long getMzSize(BlockIndex index) {
        long mzSize = 0;
        for (long mz : index.getMzs()
        ) {
            mzSize += mz;
        }
        return mzSize;
    }

    public static long getTagSize(BlockIndex index) {
        long tagSize = 0;
        for (long tag : index.getTags()
        ) {
            tagSize += tag;
        }
        return tagSize;
    }

    public static long getIntSize(BlockIndex index) {
        long intensitySize = 0;
        for (long intensity : index.getInts()
        ) {
            intensitySize += intensity;
        }
        return intensitySize;
    }
}
