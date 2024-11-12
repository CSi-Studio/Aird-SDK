package net.csibio.aird.test.AirdV3Try;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.BinPackingWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.test.AirdV3Try.Compressor.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AirdV3Try3 {
    static String indexPath = "E:\\msfile_converted\\Aird2Ex\\DDA-Sciex-MTBLS733-SampleA_3.json";

    @Test
    public void test() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        List<DDAMs> ms1List = parser.readAllToMemory();

        ArrayList[] buckets = new ArrayList[3000];

        BlockIndex index = parser.getAirdInfo().getIndexList().get(0);
        System.out.println("Aird中ms1块-mz大小：" + index.getMzs().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");
        System.out.println("Aird中ms1块-intensity大小：" + index.getInts().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");

        System.out.println();
//        System.out.println(JSON.toJSON(index.getInts().subList(0,1000)));


        long totalSize = 0l;
        for (int i = 0; i < ms1List.size(); i++) {
//            int[] intArray = convertDoubleToInt(ms1List.get(i).getSpectrum().getInts());
            double[] encodedInts = WaveCompressor.encode(ms1List.get(i).getSpectrum().getInts());
//            int[] encodedInts2 = AdaptiveIntegerCompressor.encode(encodedInts);
//            int[] encodedInts3 = QuadraticPredictioinIntegerCompressor.encode(intArray);
        int[] intArray = convertDoubleToInt(encodedInts);


            System.out.println(JSON.toJSON(encodedInts));
            totalSize += new ZstdWrapper().encode(ByteTrans.intToByte(new BinPackingWrapper().encode(intArray))).length;
        }
        System.out.println("压缩后大小：" + totalSize / 1024 / 1024 + "MB");
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

    public static HashMap<Integer, ArrayList<Double>> groupArray(double[] mzArray) {
        HashMap<Integer, ArrayList<Double>> result = new HashMap<>();

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

    public static int[] convertDoubleToInt(double[] doubleArray) {
        int[] intArray = new int[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            intArray[i] = (int) (doubleArray[i] * 10);
        }
        return intArray;
    }

    public ArrayList<Integer> bpMz(ArrayList<Double> array, int precision) {
        int[] intArray = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            intArray[i] = (int) Math.round(array.get(i) * precision);
        }
        ArrayList<Integer> mzs = new ArrayList<>();
        for (int i = 0; i < intArray.length; i++) {
            mzs.add(intArray[i]);
        }

        return mzs;
    }
}
