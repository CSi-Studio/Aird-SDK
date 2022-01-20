package net.csibio.aird.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Layers;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;
import net.csibio.aird.util.StackCompressUtil;

//比较压缩率随着block的变化规律
public class TestLayers {

  public static void main(String[] args) throws IOException {
//        List<File> files = AirdScanUtil.scanIndexFiles("\\\\ProproNas\\ProproNAS\\data\\Aird\\DIA\\ThermoQE");
//        if (files == null || files.size() == 0) {
//            return;
//        }
//        for (File indexFile : files) {
//            System.out.println(indexFile.getAbsolutePath());
//        }whether git is ready.
    String indexFilePath = "/Users/jinyinwang/Documents/stackTestData/DIA/HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
    DIAParser DIAParser = new DIAParser(indexFilePath);
    List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
    int testBlockNum = 10;
    long[][] record = new long[10][testBlockNum];
    int k = 8;//根据测试结果 测试256层堆叠效果
    int arrNum = (int) Math.pow(2, k);
    for (int m = 0; m < testBlockNum; m++) {
      BlockIndex index = swathIndexList.get(m);
      List<Float> rts = index.getRts();
      int mzNum = rts.size();
      record[9][m] = mzNum;

      //取出一个block的数组
      List<int[]> mzGroup = new ArrayList<>();
      for (int i = 0; i < mzNum; i++) {
        int[] arr;
        arr = DIAParser.getSpectrumAsInteger(index, rts.get(i)).getMz();
        mzGroup.add(arr);
      }
//      record[0][m] = RamUsageEstimator.sizeOf((Query) mzGroup);
//            String size1 = RamUsageEstimator.humanSizeOf(mzGroup);
//            System.out.println("原数组：" + size1);

      //计算一代压缩/解压时间
      List<byte[]> comMZs = new LinkedList<>();
      long t1 = 0, t2 = 0;
      for (int i = 0; i < mzNum; i++) {
        long tempT = System.currentTimeMillis();
        byte[] comMZ = CompressUtil.transToByte(CompressUtil.fastPforEncoder(mzGroup.get(i)));
        t1 += (System.currentTimeMillis() - tempT);
//                System.out.println(System.currentTimeMillis() - tempT);
        comMZs.add(comMZ);
        long tempT2 = System.currentTimeMillis();
        CompressUtil.fastPforDecoder(CompressUtil.transToInteger(comMZ));
        t2 += (System.currentTimeMillis() - tempT2);
//                System.out.println(System.currentTimeMillis() - tempT2);
      }
//      record[1][m] = RamUsageEstimator.sizeOf((Query) comMZs);
      record[2][m] = t1;
      record[3][m] = t2;
//            String size2 = RamUsageEstimator.humanSizeOf(comMZs);
//            System.out.println("一代：" + size2);
//            System.out.println("一代压缩时间：" + t1);

      long t3 = System.currentTimeMillis();
      int groupNum = (mzNum - 1) / arrNum + 1;
      List<Layers> layersList = new LinkedList<>();
      int fromIndex = 0;
      for (int i = 0; i < groupNum - 1; i++) {
        List<int[]> arrGroup = mzGroup.subList(fromIndex, fromIndex + arrNum);
        Layers stack = StackCompressUtil.stackEncode(arrGroup, true);
        layersList.add(stack);
        fromIndex += arrNum;
      }
      //处理余数
      List<int[]> arrGroup = mzGroup.subList(fromIndex, mzNum);
      Layers stackRemainder = StackCompressUtil.stackEncode(arrGroup, false);
      layersList.add(stackRemainder);
      record[5][m] = System.currentTimeMillis() - t3;
//      record[4][m] = RamUsageEstimator.sizeOf((Query) layersList);

      for (Layers stack : layersList) {
        long tempT = System.currentTimeMillis();
        StackCompressUtil.stackDecode(stack);
        record[6][m] += System.currentTimeMillis() - tempT;
//        record[7][m] += RamUsageEstimator.sizeOf(stack.getTagArray());
//        record[8][m] += RamUsageEstimator.sizeOf(stack.getMzArray());
      }

//                String size3 = RamUsageEstimator.humanSizeOf(layersList);
//                System.out.println("二代：" + size3);
//                System.out.println("二代压缩时间：" + (t3 - t2));
      System.out.println("block" + m + " finished!");

    }

    File file = new File(indexFilePath.replace(".json", "blockTest.csv"));
    FileWriter out = new FileWriter(file);
    out.write(testBlockNum + " blocks tested; " + arrNum + " layers stacked.\r\n");
    out.write(
        "sizeOrigin,size1,encodeTime1,decodeTime1,size2,encodeTime2,decodeTime2,sizeIndex,sizeMz,mzNum\r\n");
    for (int i = 0; i < testBlockNum; i++) {
      for (int j = 0; j < 10; j++) {
        out.write(record[j][i] + ",");
      }
      out.write("\r\n");
    }
    out.close();
  }
}
