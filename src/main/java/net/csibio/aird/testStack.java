package net.csibio.aird;


import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class testStack {
    public static void main(String[] args) {
//        List<File> files = AirdScanUtil.scanIndexFiles("\\\\ProproNas\\ProproNAS\\data\\Aird\\DIA\\ThermoQE");
//        if (files == null || files.size() == 0) {
//            return;
//        }
//        for (File indexFile : files) {
//            System.out.println(indexFile.getAbsolutePath());
//        }whether git is ready.

//        DIAParser DIAParser = new DIAParser("/Users/jinyinwang/Documents/HYE110_TTOF6600_64var_lgillet_I160305_001.json");
//        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
//        BlockIndex index = swathIndexList.get(0);
//
//        for (int k = 4; k < 5; k++) {
//            int arrNum = (int) Math.pow(2, k);
//            List<int[]> arrGroup = new LinkedList<>();
//            for (int i = 0; i < arrNum; i++) {
//                float[] mzArray = DIAParser.getSpectrum(index, index.getRts().get(i)).getMzArray();
//                int[] arr = new int[mzArray.length];
//                for (int j = 0; j < mzArray.length; j++) {
//                    arr[j] = (int) (mzArray[j] * 10000);
//                }
//                arrGroup.add(arr);
//            }
////            stackData.Stack stack = stackData.stackEncode(arrGroup);
////            List<int[]> stackDecode = stackData.stackDecode(stack);
//            stackData2.Stack stack2 = stackData2.stackEncode(arrGroup);
//            List<int[]> stackDecode2 = stackData2.stackDecode(stack2);
//
//            //压缩原数组
//            List<byte[]> comArr = new LinkedList<>();
//            Boolean a = Boolean.TRUE;
//            Boolean b = Boolean.TRUE;
//            for (int i = 0; i < arrGroup.size(); i++) {
////                a = a && Arrays.equals(arrGroup.get(i), stackDecode.get(i));
//                b = b && Arrays.equals(arrGroup.get(i), stackDecode2.get(i));
//                comArr.add(CompressUtil.transToByte(CompressUtil.fastPforEncoder(arrGroup.get(i))));
//            }
//            System.out.println("对照不移位压缩前后数组是否相同" + a);
//            System.out.println("对照移位压缩前后数组是否相同" + b);
//
//            String size1 = RamUsageEstimator.humanSizeOf(comArr);
////            String size2 = RamUsageEstimator.humanSizeOf(stack);
//            String size3 = RamUsageEstimator.humanSizeOf(stack2);
//            System.out.println("堆叠压缩前" + size1);
////            System.out.println("不移位压缩后" + size2);
//            System.out.println("移位压缩后" + size3);
//        }
    }
}
