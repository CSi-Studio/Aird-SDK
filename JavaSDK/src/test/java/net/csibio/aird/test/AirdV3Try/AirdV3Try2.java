package net.csibio.aird.test.AirdV3Try;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.util.ArrayUtil;
import org.junit.Test;

import java.util.*;

public class AirdV3Try2 {

//    static String indexPath = "E:\\ComboCompTest\\Aird\\DDA-Sciex-MTBLS733-SampleA_1.json";
//    static String indexPath = "E:\\ComboCompTest\\Aird\\DDA-Thermo-MTBLS733-SA1.json";
    static String indexPath = "E:\\ComboCompTest\\Aird\\DDA-Agilent-PXD004712-Set 3_F1.json";

    @Test
    public void test() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        List<DDAMs> ms1List = parser.readAllToMemory();

        ArrayList<Integer>[] buckets = new ArrayList[5000];
        ArrayList<Integer>[] bucketIndexes = new ArrayList[5000];

        BlockIndex index = parser.getAirdInfo().getIndexList().get(0);
        System.out.println("Aird中ms1块-mz大小：" + index.getMzs().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");
        System.out.println("Aird中ms1块-intensity大小：" + index.getInts().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");

        for (int i = 0; i < ms1List.size(); i++) {
            TreeMap<Integer, ArrayList<Double>> bucketMap = groupArray(ms1List.get(i).getSpectrum().getMzs());
            for (int k = 0; k < buckets.length; k++) {
                if (buckets[k] == null) {
                    buckets[k] = new ArrayList<>();
                    bucketIndexes[k] = new ArrayList<>();
                }
                ArrayList<Double> subBucket = bucketMap.get(k);
                if (subBucket != null) {
                    int[] bucket = new IntegratedVarByteWrapper().encode(ByteTrans.doubleToInt(subBucket, 100000));
                    if (i == 0) {
                        bucketIndexes[k].add(bucket.length);
                    } else {
                        bucketIndexes[k].add(bucketIndexes[k].get(i-1)+ bucket.length);
                    }
                    buckets[k].addAll(Arrays.stream(bucket).boxed().toList());
                } else {
                    if (i == 0) {
                        bucketIndexes[k].add(0);
                    } else {
                        bucketIndexes[k].add(bucketIndexes[k].get(i-1));
                    }
                }
            }
        }

        long totalSize = 0l;
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] == null || buckets[i].isEmpty()) {
                continue;
            }
            totalSize += new ZstdWrapper().encode(ByteTrans.intToByte(ArrayUtil.toIntPrimitive(buckets[i]))).length;
            totalSize += new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedVarByteWrapper().encode(ArrayUtil.toIntPrimitive(bucketIndexes[i])))).length;
        }
        System.out.println("压缩后大小：" + totalSize*1.0 / 1024 / 1024 + "MB");
    }

    public static HashMap<Integer, ArrayList<Double>> groupArray(double[] mzArray, double[] intensityArray) {
        HashMap<Integer, ArrayList<Double>> result = new HashMap<>();

        for (int i = 0; i < mzArray.length; i++) {
            int intPart = (int) (mzArray[i]);
            int groupKey = intPart;

            if (!result.containsKey(groupKey)) {
                result.put(groupKey, new ArrayList<>());
            }

            result.get(groupKey).add(intensityArray[i]);
        }

        return result;
    }

    public static TreeMap<Integer, ArrayList<Double>> groupArray(double[] mzArray) {
        TreeMap<Integer, ArrayList<Double>> result = new TreeMap<>();

        for (double value : mzArray) {
            int intPart = (int) (value);
            int groupKey = intPart;

            if (!result.containsKey(groupKey)) {
                result.put(groupKey, new ArrayList<>());
            }

            result.get(groupKey).add(value);
        }

        return result;
    }
}
