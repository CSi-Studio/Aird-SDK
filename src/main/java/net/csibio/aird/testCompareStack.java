package net.csibio.aird;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;
import org.apache.lucene.util.RamUsageEstimator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

public class testCompareStack {
    public static void main(String[] args) throws IOException {
        String indexFilePath = "/Users/jinyinwang/Documents/stackTestData/DIA/HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
        DIAParser DIAParser = new DIAParser(indexFilePath);
        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
        System.out.println("block数：" + swathIndexList.size());
        System.out.println();
        int testBlockNum = 10;
        long sizeOrigin = 0;
        long tAird1 = 0;
        long tAird1Decode = 0;
        long sizeAird1 = 0;

        long[][] recordSize = new long[9][testBlockNum];//记录2的幂次堆叠每个block的size
        long[][] recordEncodeTime = new long[9][testBlockNum];//记录2的幂次堆叠每个block的EncodeTime
        long[][] recordDecodeTime = new long[9][testBlockNum];
        long[][] recordIndexSize = new long[9][testBlockNum];
        long[][] recordMzSize = new long[9][testBlockNum];

        for (int m = 0; m < testBlockNum; m++) {
            BlockIndex index = swathIndexList.get(m);
            List<Float> rts = index.getRts();
            int mzNum = rts.size();

            //取出一个block的数组
            List<int[]> mzGroup = new ArrayList<>();
            for (Float rt : rts) {
                int[] arr;
                arr = DIAParser.getSpectrumAsInteger(index, rt).getMz();
                mzGroup.add(arr);
            }
            sizeOrigin += RamUsageEstimator.sizeOf(mzGroup);
//            String size1 = RamUsageEstimator.humanSizeOf(mzGroup);
//            System.out.println("原数组：" + size1);

            //计算一代压缩时间
            List<byte[]> comMZs = new ArrayList<>();
            long t1 = 0;
            for (int i = 0; i < mzNum; i++) {
                long tempT = System.currentTimeMillis();
                byte[] comMZ = CompressUtil.transToByte(CompressUtil.fastPforEncoder(mzGroup.get(i)));
                t1 += (System.currentTimeMillis() - tempT);
                comMZs.add(comMZ);
            }
            sizeAird1 += RamUsageEstimator.sizeOf(comMZs);
            tAird1 += t1;
//            String size2 = RamUsageEstimator.humanSizeOf(comMZs);
//            System.out.println("一代：" + size2);
//            System.out.println("一代压缩时间：" + t1);
            //一代解压时间
            long tDecode = 0;
            for (byte[] comMz : comMZs
            ) {
                long tempT = System.currentTimeMillis();
                CompressUtil.fastPforDecoder(CompressUtil.transToInteger(comMz));
                tDecode += (System.currentTimeMillis() - tempT);
            }
            tAird1Decode += tDecode;

            for (int k = 9; k < 11; k++) {
                long t2 = System.currentTimeMillis();
                int arrNum = (int) Math.pow(2, k);
                int groupNum = (mzNum - 1) / arrNum + 1;
                List<stackData2Rep.Stack> stacks = new ArrayList<>();
                int fromIndex = 0;
                for (int i = 0; i < groupNum - 1; i++) {
                    List<int[]> arrGroup = mzGroup.subList(fromIndex, fromIndex + arrNum);
                    stackData2Rep.Stack stack = stackData2Rep.stackEncode(arrGroup, true);
                    stacks.add(stack);
                    fromIndex += arrNum;
                }
                //处理余数
                List<int[]> arrGroup = mzGroup.subList(fromIndex, mzNum);
                stackData2Rep.Stack stackRemainder = stackData2Rep.stackEncode(arrGroup, false);
                stacks.add(stackRemainder);
                long t3 = System.currentTimeMillis();
                recordSize[k - 2][m] = RamUsageEstimator.sizeOf(stacks);
                recordEncodeTime[k - 2][m] = (t3 - t2);
                for (stackData2Rep.Stack stack : stacks) {
                    recordIndexSize[k - 2][m] += RamUsageEstimator.sizeOf(stack.getComIndex());
                    recordMzSize[k - 2][m] += RamUsageEstimator.sizeOf(stack.getComArr());
                    long tempT = System.currentTimeMillis();
                    stackData2Rep.stackDecode(stack);
                    recordDecodeTime[k - 2][m] += (System.currentTimeMillis() - tempT);
                }
                System.out.println(k);
//                String size3 = RamUsageEstimator.humanSizeOf(stacks);
//                System.out.println("二代：" + size3);
//                System.out.println("二代压缩时间：" + (t3 - t2));
            }
            System.out.println("block" + m + " finished!");
        }

        File file = new File(indexFilePath.replace("json", "csv"));
        FileWriter out = new FileWriter(file);
        out.write(testBlockNum + " blocks recorded" + "\r\n");
        out.write("sizeOrigin" + "," + sizeOrigin + "\r\n");
        out.write("k,size,encodeTime,decodeTime,sizeIndex,sizeMz" + "\r\n");
        out.write("0" + "," + sizeAird1 + "," + tAird1 + "," + tAird1Decode);
        for (int i = 0; i < 9; i++) {
            out.write("\r\n");
            long stackSize = 0;
            long stackTime = 0;
            long stackTimeDecode = 0;
            long stackSizeIndex = 0;
            long stackSizeMz = 0;
            for (int j = 0; j < testBlockNum; j++) {
                stackSize += recordSize[i][j];
                stackTime += recordEncodeTime[i][j];
                stackTimeDecode += recordDecodeTime[i][j];
                stackSizeIndex += recordIndexSize[i][j];
                stackSizeMz += recordMzSize[i][j];
            }
            out.write((i + 2) + "," + stackSize + "," + stackTime + "," + stackTimeDecode + "," + stackSizeIndex + "," + stackSizeMz);
        }
        out.close();
    }
}
