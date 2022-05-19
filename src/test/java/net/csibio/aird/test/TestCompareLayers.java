package net.csibio.aird.test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Layers;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.ArrayUtil;
import net.csibio.aird.util.StackCompressUtil;

public class TestCompareLayers {

  public static void main(String[] args) throws Exception {
    String indexFilePath = "D:\\omicsdata\\proteomics\\C20181208yix_HCC_DIA_T_46A.json";
    DIAParser diaParser = new DIAParser(indexFilePath);
    List<BlockIndex> swathIndexList = diaParser.getAirdInfo().getIndexList();
    System.out.println("block数：" + swathIndexList.size());
    System.out.println();
    int testBlockNum = 5;
    long sizeOrigin = 0;
    long tAird1 = 0;
    long tAird1Decode = 0;
    long sizeAird1 = 0;

    long[][] recordSize = new long[15][testBlockNum];//记录2的幂次堆叠每个block的size
    long[][] recordEncodeTime = new long[15][testBlockNum];//记录2的幂次堆叠每个block的EncodeTime
    long[][] recordDecodeTime = new long[15][testBlockNum];
    long[][] recordIndexSize = new long[15][testBlockNum];
    long[][] recordMzSize = new long[15][testBlockNum];

    for (int m = 0; m < testBlockNum; m++) {
      BlockIndex index = swathIndexList.get(m + 1);
      List<Double> rts = index.getRts();
      int mzNum = rts.size();

      //取出一个block的数组
      List<int[]> mzGroup = new ArrayList<>();
      for (Double rt : rts) {
        int[] arr;
        double[] temp = diaParser.getSpectrumByRt(index, rt).getMzs();
        mzGroup.add(ArrayUtil.d2i(temp, diaParser.getMzPrecision()));
      }
//            sizeOrigin += RamUsageEstimator.sizeOf((Accountable) mzGroup);
//            String size1 = RamUsageEstimator.humanSizeOf(mzGroup);
//            System.out.println("原数组：" + size1);

      //计算一代压缩时间
      List<byte[]> comMZs = new ArrayList<>();
      long t1 = 0;
      for (int i = 0; i < mzNum; i++) {
        long tempT = System.currentTimeMillis();
        byte[] comMZ = ByteTrans.intToByte(
            new IntegratedBinPackingWrapper().encode(mzGroup.get(i)));
        t1 += (System.currentTimeMillis() - tempT);
        comMZs.add(comMZ);
      }
//            sizeAird1 += RamUsageEstimator.sizeOf((Accountable) comMZs);
      tAird1 += t1;
      //一代解压时间
      long tDecode = 0;
      for (byte[] comMz : comMZs) {
        long tempT = System.currentTimeMillis();
        new IntegratedBinPackingWrapper().decode(ByteTrans.byteToInt(comMz));
        tDecode += (System.currentTimeMillis() - tempT);
      }
      tAird1Decode += tDecode;

      for (int k = 1; k < 12; k++) {
        long t2 = System.currentTimeMillis();
        int arrNum = (int) Math.pow(2, k);
        int groupNum = (mzNum - 1) / arrNum + 1;
        List<Layers> layersList = new ArrayList<>();
        int fromIndex = 0;
        for (int i = 0; i < groupNum - 1; i++) {
          List<int[]> arrGroup = mzGroup.subList(fromIndex, fromIndex + arrNum);
          Layers layers = StackCompressUtil.stackEncode(arrGroup, true);
          layersList.add(layers);
          fromIndex += arrNum;
        }
        //处理余数
        List<int[]> arrGroup = mzGroup.subList(fromIndex, mzNum);
        Layers stackRemainder = StackCompressUtil.stackEncode(arrGroup, false);
        layersList.add(stackRemainder);
        long t3 = System.currentTimeMillis();
//                recordSize[k - 1][m] = RamUsageEstimator.sizeOf((Accountable) layersList);
        recordEncodeTime[k - 1][m] = (t3 - t2);
        for (Layers layers : layersList) {
//          recordIndexSize[k - 1][m] += RamUsageEstimator.sizeOf(layers.getTagArray());
//          recordMzSize[k - 1][m] += RamUsageEstimator.sizeOf(layers.getMzArray());
          long tempT = System.currentTimeMillis();
          StackCompressUtil.stackDecode(layers);
          recordDecodeTime[k - 1][m] += (System.currentTimeMillis() - tempT);
        }
        System.out.println(k);
      }
      System.out.println("block" + m + " finished!");
    }

    File file = new File(indexFilePath.replace("json", "csv"));
    FileWriter out = new FileWriter(file);
    out.write(testBlockNum + " blocks recorded" + "\r\n");
    out.write("sizeOrigin" + "," + sizeOrigin + "\r\n");
    out.write("k,size,encodeTime,decodeTime,sizeIndex,sizeMz" + "\r\n");
    out.write("0" + "," + sizeAird1 + "," + tAird1 + "," + tAird1Decode);
    for (int i = 0; i < 11; i++) {
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
      out.write(
          (i + 1) + "," + stackSize + "," + stackTime + "," + stackTimeDecode + "," + stackSizeIndex
              + "," + stackSizeMz);
    }
    out.close();
  }
}
