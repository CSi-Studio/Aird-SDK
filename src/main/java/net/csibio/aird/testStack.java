package net.csibio.aird;


import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;
import org.apache.lucene.util.RamUsageEstimator;

import java.util.*;

public class testStack {
    public static void main(String[] args) {
//        List<File> files = AirdScanUtil.scanIndexFiles("\\\\ProproNas\\ProproNAS\\data\\Aird\\DIA\\ThermoQE");
//        if (files == null || files.size() == 0) {
//            return;
//        }
//        for (File indexFile : files) {
//            System.out.println(indexFile.getAbsolutePath());
//        }whether git is ready.

        DIAParser DIAParser = new DIAParser("/Users/jinyinwang/Documents/HYE110_TTOF6600_64var_lgillet_I160305_001.json");
        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
        BlockIndex index = swathIndexList.get(1);
        List<Float> rts = index.getRts();
        int mzNum = rts.size();

        //取出一个block的数组
        List<int[]> mzGroup = new ArrayList<>();
        for (int i = 0; i < mzNum; i++) {
            int[] arr;
//            arr = DIAParser.getMzValuesAsInteger();
            arr = DIAParser.getSpectrumAsInteger(index, rts.get(i)).getMz();
            mzGroup.add(arr);
        }
        String size1 = RamUsageEstimator.humanSizeOf(mzGroup);
        System.out.println("原数组：" + size1);

        for (int k = 8; k < 9; k++) {
            long t2 = System.currentTimeMillis();
            int arrNum = (int) Math.pow(2, k);
            int groupNum = (mzNum - 1) / arrNum + 1;
            List<stackData2Rep.Stack> stacks = new LinkedList<>();
            int fromIndex = 0;
            for (int m = 0; m < groupNum - 1; m++) {
                List<int[]> arrGroup = mzGroup.subList(fromIndex, fromIndex + arrNum);
                stackData2Rep.Stack stack = stackData2Rep.stackEncode(arrGroup);
                stacks.add(stack);
                fromIndex += arrNum;
            }
            //处理余数
            List<int[]> arrGroup = mzGroup.subList(fromIndex, mzNum);
            stackData2Rep.Stack stack = stackData2Rep.stackEncode(arrGroup,false);
            stacks.add(stack);

            long t3 = System.currentTimeMillis();
            String size3 = RamUsageEstimator.humanSizeOf(stacks);

            //计算一代压缩时间
            List<byte[]> comMZs = new LinkedList<>();
            long t0 = System.currentTimeMillis();
            for (int i = 0; i < mzNum; i++) {
                byte[] comMZ = CompressUtil.zlibEncoder(CompressUtil.transToByte(CompressUtil.fastPforEncoder(mzGroup.get(i))));
                comMZs.add(comMZ);
            }
            long t1 = System.currentTimeMillis();
            String size2 = RamUsageEstimator.humanSizeOf(comMZs);

            System.out.println("一代：" + size2);
            System.out.println("一代压缩时间：" + (t1 - t0));
            System.out.println("二代：" + size3);
            System.out.println("二代压缩时间：" + (t3 - t2));
        }
    }
}
