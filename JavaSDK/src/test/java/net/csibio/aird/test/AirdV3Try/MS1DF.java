package net.csibio.aird.test.AirdV3Try;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.DDAPasefMs;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.BinPackingWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.DeltaWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DDAPasefParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MS1DF {

    static String indexPath = "E:\\msfile_converted\\Aird2Ex\\9.json";
    //    static String indexPath = "E:\\msfile_converted\\DDA-Thermo-MTBLS733-SA1.json";

    static int MB = 1024 * 1024;
    static int KB = 1024;

    @Test
    public void test() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        List<DDAMs> ms1List = parser.readAllToMemory();

        BlockIndex index = parser.getAirdInfo().getIndexList().get(0);
        System.out.println("Aird中ms1块-mz大小：" + index.getMzs().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");
        System.out.println("Aird中ms1块-intensity大小：" + index.getInts().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");


        long totalSize = 0l;


        int comMzSize = 0;
        int comIntSize = 0;


        ArrayList<Integer> mzIndex = new ArrayList<>();
        ArrayList<Double> mzRates = new ArrayList<>();
        ArrayList<Integer> intIndex = new ArrayList<>();
        ArrayList<Double> intRates = new ArrayList<>();

        ArrayList<Integer> lastMzIndex = new ArrayList<>();
        ArrayList<Integer> lastIntIndex = new ArrayList<>();

        ArrayList<Integer> indexList = new ArrayList<>();
        ArrayList<Integer> pointList = new ArrayList<>();

        for (int i = 0; i < ms1List.size(); i++) {
            indexList.add(i);
            pointList.add(ms1List.get(i).getSpectrum().getMzs().length);
        }
        System.out.println(JSON.toJSON(indexList));
        System.out.println(JSON.toJSON(pointList));
        int k = 0;
        System.out.println("共有质谱图"+ms1List.size()+"张");
        double[] lastMzArray = ms1List.get(0).getSpectrum().getMzs();
        int[] lastIntArray = convertInt(ms1List.get(0).getSpectrum().getInts());
        for (int i = 0; i < ms1List.size(); i++) {
//            double[] curMz = ms1List.get(i).getSpectrum().getMzs();
//            double[] curInt = ms1List.get(i).getSpectrum().getInts();
//            double[] mzArray = ms1List.get(i).getSpectrum().getMzs();
//            double[] intensityArray = ms1List.get(i).getSpectrum().getInts();
//
//            changeOrder(curMz,curInt,mzArray,intensityArray);


            double[] curMz = ms1List.get(i).getSpectrum().getMzs();
            double[] curInt = ms1List.get(i).getSpectrum().getInts();
            int[] curInt10 = convertInt(curInt);
            double[] mzArray = ms1List.get(i).getSpectrum().getMzs();
            int[] intensityArray = convertInt(ms1List.get(i).getSpectrum().getInts());

            //计算差值
            if (i>0 && i % 2 != 0) {
                dfCalculate(lastMzArray,lastIntArray,curMz,curInt10,mzArray,intensityArray);
            }
            byte[] byteMz = compressMz(mzArray);
            byte[] byteInt = compressInt(intensityArray);
            comMzSize += byteMz.length;
            comIntSize += byteInt.length;
            lastIntArray = curInt10;
            lastMzArray = curMz;
        }
        totalSize = comMzSize + comIntSize;
        System.out.println("压缩后int大小：" + comIntSize / 1024 / 1024 + "MB");
        System.out.println("压缩后大小：" + totalSize / 1024 / 1024 + "MB");

    }

    private int[] convertInt(double[] curInt) {
        int[] curInt10 =  new int[curInt.length];
        for (int i = 0; i <curInt.length; i++) {
            curInt10[i] = (int)(curInt[i] * 10);
        }
        return curInt10;
    }

    public boolean compareSeesee(double[] curInt, double[] intensityArray) {
        // 首先检查数组长度是否相等
        if (curInt.length != intensityArray.length) {
            return false;
        }

        // 逐个元素进行比较
        for (int i = 0; i < curInt.length; i++) {
            // 使用 Double.compare() 方法比较两个double值是否相等
            // 这种方式可以处理 NaN 和 +0.0 与 -0.0 的情况
            if (Double.compare(curInt[i], intensityArray[i]) != 0) {
                return false;
            }
        }

        // 如果所有元素都相等,返回 true
        return true;
    }

    public void dfCalculate(double[] lastMz, int[] lastInt, double[] curMz, int[] curInt, double[] mzResult, int[] intResult) {
        int findedNum = 0;
        // 遍历当前时间点的质谱数据
        for (int i = 0; i < curMz.length; i++) {
            double currentMz = curMz[i];
            int currentInt = curInt[i];
            int index = binarySearch(lastMz, currentMz);

            // 如果找到,则计算强度差值
            if (index >= 0) {
                intResult[i] = currentInt - lastInt[index];
                findedNum ++;
            }
            // 如果未找到,则保持当前强度不变
            else {
                intResult[i] = currentInt;
            }
            mzResult[i] = currentMz;
        }
//        if (findedNum*100.0/curMz.length > 20) {
//            System.out.println("命中数量：" + findedNum + ", 命中率是：" + findedNum + "/" + curMz.length + "=" + findedNum * 100.0 / curMz.length);
//            System.out.println("before int" + JSON.toJSON(Arrays.copyOfRange(curInt,0,1000)));
//            System.out.println("after int" + JSON.toJSON(Arrays.copyOfRange(intResult,0,1000)));
//        }
    }


    public void deDfCalculate(double[] lastMz, double[] lastInt, double[] curMz, double[] curInt, double[] mzResult, double[] intResult) {
        // 遍历当前时间点的质谱数据
        for (int i = 0; i < curMz.length; i++) {
            double currentMz = curMz[i];
            double currentInt = curInt[i];
            int index = binarySearch(lastMz, currentMz);

            // 如果找到,则计算强度差值
            if (index >= 0) {
                intResult[i] = lastInt[index] + currentInt - lastInt[index];
            }
            // 如果未找到,则保持当前强度不变
            else {
                intResult[i] = currentInt;
            }
            mzResult[i] = currentMz;
        }
    }

    private int binarySearch(double[] array, double target) {
        int left = 0;
        int right = array.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (array[mid] == target) {
                return mid;
            } else if (array[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public void  changeOrder(double[] mz,double[] intensity,double[] mzResult,double[] intResult ) {
        // 创建一个索引数组,用于记录原始数组的位置
        Integer[] indices = new Integer[mz.length];

        // 初始化索引数组
        for (int i = 0; i < mz.length; i++) {
            indices[i] = i;
        }

        // 使用 Java 8 的 Stream API 对索引数组进行排序
        // 排序规则为按照 Int 数组的值升序排序
        Arrays.sort(indices, Comparator.comparingDouble(i -> intensity[i]));

        // 根据排序后的索引,重新填充 mzResult 和 intResult 数组
        for (int i = 0; i < mz.length; i++) {
            int index = indices[i];
            mzResult[i] = mz[index];
            intResult[i] = intensity[index];
        }
    }

    public int[] bpMz(double[] array){
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int)Math.round(array[i]*100000);
        }
        return new DeltaWrapper().encode(intArray);
    }

    public byte[] compressMz(double[] array){
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int)Math.round(array[i]*100000);
        }
        return new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedVarByteWrapper().encode(intArray)));
    }

    public byte[] compressInt(double[] array){
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int)Math.round(array[i]);
        }
        return new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedBinPackingWrapper().encode(intArray)));
    }

    public byte[] compressInt(int[] array){
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int)Math.round(array[i]);
        }
        return new ZstdWrapper().encode(ByteTrans.intToByte(new BinPackingWrapper().encode(intArray)));
    }

//    public double[] compressInt(byte[] array){
//        double[] doubleArray = new double[array.length];
//        for (int i = 0; i < array.length; i++) {
//            doubleArray[i] = (int)Math.round(array[i]);
//        }
//        return new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(doubleArray)));
//    }
}
