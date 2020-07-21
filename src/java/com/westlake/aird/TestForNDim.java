package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.BlockIndex;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.structure.SortInt;
import com.westlake.aird.util.ArrayUtil;
import com.westlake.aird.util.CompressUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestForNDim {


    public static void main(String[] args) {
        String[] fileNames = {
//                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json",
//                "HYE110_TTOF6600_32fix_lgillet_I160308_002.json",
//                "HYE110_TTOF6600_32fix_lgillet_I160308_003.json",
//                "HYE110_TTOF6600_32fix_lgillet_I160308_004.json",
//                "HYE110_TTOF6600_32fix_lgillet_I160308_010.json",
//                "HYE110_TTOF6600_32fix_lgillet_I160308_011.json",
//
//                "HYE110_TTOF6600_32var_lgillet_I160309_001.json",
//                "HYE110_TTOF6600_32var_lgillet_I160309_002.json",
//                "HYE110_TTOF6600_32var_lgillet_I160309_003.json",
//                "HYE110_TTOF6600_32var_lgillet_I160309_004.json",
//                "HYE110_TTOF6600_32var_lgillet_I160309_005.json",
//                "HYE110_TTOF6600_32var_lgillet_I160309_006.json",
//
//                "HYE110_TTOF6600_64fix_lgillet_I160310_001.json",
//                "HYE110_TTOF6600_64fix_lgillet_I160310_002.json",
//                "HYE110_TTOF6600_64fix_lgillet_I160310_003.json",
//                "HYE110_TTOF6600_64fix_lgillet_I160310_004.json",
//                "HYE110_TTOF6600_64fix_lgillet_I160310_005.json",
//                "HYE110_TTOF6600_64fix_lgillet_I160310_006.json",
//
//                "HYE110_TTOF6600_64var_lgillet_I160305_001.json",
//                "HYE110_TTOF6600_64var_lgillet_I160305_002.json",
//                "HYE110_TTOF6600_64var_lgillet_I160305_003.json",
//                "HYE110_TTOF6600_64var_lgillet_I160305_004.json",
//                "HYE110_TTOF6600_64var_lgillet_I160305_005.json",
//                "HYE110_TTOF6600_64var_lgillet_I160305_006.json",
                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json",
//                "HYE124_TTOF5600_32fix_lgillet_L150206_001.json",
//                "HYE124_TTOF5600_32fix_lgillet_L150206_002.json",
//                "HYE124_TTOF5600_32fix_lgillet_L150206_003.json",
//                "HYE124_TTOF5600_32fix_lgillet_L150206_005.json",
//                "HYE124_TTOF5600_32fix_lgillet_L150206_013.json",
//                "HYE124_TTOF5600_32fix_lgillet_L150206_014.json",

        };

        ArrayList<String> results = new ArrayList<>();

        for (String fileName : fileNames) {

            String path = "E:\\data\\HYE4\\";
            System.out.println(fileName);

            File indexFile = new File(path + fileName);

            AtomicLong aSize = new AtomicLong(0);
            AtomicLong bSize = new AtomicLong(0);
            AtomicLong totalMzCount = new AtomicLong(0);
            AtomicInteger iter = new AtomicInteger(0);
            AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
            List<BlockIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();

            swathIndexList.stream().forEach(index -> {
                int nowIter = iter.incrementAndGet();
                System.out.println("正在扫描:" + nowIter + "/" + swathIndexList.size());
                int N = 16; // 初始化的堆叠层数
                int layerCount = (int) Math.ceil(Math.log(N) / Math.log(2)); //转换为二进制以后需要保留的展示位数,例如8层需要保留3位用于存储原layer
                long aSizeSum = 0;
                long bSizeSum = 0;
                long mzCountSum = 0;
                double avgDelta = 0;
                for (int i = 0; i < index.getRts().size(); i = i + N) {
                    if (i + N >= index.getRts().size()) {
                        N = index.getRts().size() - i;
                    }
                    SortInt[] totalMz = new SortInt[0];
                    long totalA = 0; //原算法压缩后的大小
                    for (int j = 0; j < N; j++) {
                        MzIntensityPairs pairs = airdParser.getSpectrumAsInteger(index, index.getRts().get(i + j));
                        int[] pairsMz = pairs.getMz();
                        try {
                            totalMz = ArrayUtils.addAll(totalMz, ArrayUtil.transToSortIntArray(pairsMz, j));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        byte[] compressed = compressForMz(pairsMz);
                        totalA += compressed.length;
                    }

                    Arrays.sort(totalMz);
                    mzCountSum += totalMz.length;
                    Pair<int[], byte[]> pair = ArrayUtil.transToOriginArrayAndLayerNote(totalMz, layerCount);
                    long totalCompressed = compressForMz(pair.getFirst()).length;
//                    long totalB = totalCompressed + totalMz.length / 8 * layerCount; //堆叠算法压缩后的大小
                    byte[] compressLayerNote = CompressUtil.zlibCompress(pair.getSecond());
                    long totalB = totalCompressed + compressLayerNote.length; //堆叠算法压缩后的大小
                    avgDelta += ArrayUtil.avgDelta(pair.getFirst());
                    aSize.getAndAdd(totalA);
                    bSize.getAndAdd(totalB);
                    aSizeSum += totalA;
                    bSizeSum += totalB;
                    totalMzCount.getAndAdd(mzCountSum);
                }
                System.out.println("Avg Mz:" + mzCountSum * 1d / index.getRts().size());
                System.out.println("Avg Mz Delta:" + avgDelta / index.getRts().size());
                System.out.println("Swath Ratio:" + bSizeSum * 1d / aSizeSum);
            });

            DecimalFormat df = new DecimalFormat("#.###");
            StringBuilder sb = new StringBuilder(fileName);
            sb.append(",");
            sb.append(fileName.contains("5600") ? "5600" : "6600");
            sb.append(",");
            sb.append(fileName.contains("32") ? "32" : "64");
            sb.append(fileName.contains("fix") ? "fix" : "var");
            sb.append(",");
            sb.append(df.format(aSize.get() / 1024d / 1024 / 1024));
            sb.append(",");
            sb.append(df.format(bSize.get() / 1024d / 1024 / 1024));
            sb.append(",");
            sb.append(df.format(bSize.get() * 1.0 / aSize.get()));
            results.add(sb.toString());
        }

        results.forEach(System.out::println);
    }

    public static byte[] compressForMz(int[] mz) {
        int[] pairsMzCompressed = CompressUtil.fastPForEncoder(mz);
        byte[] pairsMzByte = CompressUtil.transToByte(pairsMzCompressed);

        return pairsMzByte;
    }
}
