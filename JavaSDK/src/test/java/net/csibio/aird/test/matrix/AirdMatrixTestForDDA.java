package net.csibio.aird.test.matrix;

import com.sun.source.tree.Tree;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.*;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.Empty;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.util.AirdMathUtil;
import net.csibio.aird.util.ArrayUtil;
import net.csibio.aird.util.PrecisionUtil;
import org.junit.Test;

import java.util.*;

public class AirdMatrixTestForDDA {

    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp\\DDA-Thermo-MTBLS733-SA1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\4dp\\DDA-Sciex-MTBLS733-SampleA_1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\4dp\\DDA-Agilent-PXD004712-Set 3_F1.json";

    static List<Double> targets = new ArrayList<>();


    public void random() {
        for (int i = 0; i < 10; i++) {
            double random = new Random().nextDouble() * 16; //0-16之间的数字
            random += 4; //4-20之间的数字
            random *= 100; //400-2000之间的浮点数字
            targets.add(random);
        }
    }

    @Test
    public void speedTest() throws Exception {
        random();
        long startTotal = System.currentTimeMillis();
        int simulatorFiles = 10;
        long readTime = 0;
        for (int count = 0; count < simulatorFiles; count++) {
            DDAParser parser = new DDAParser(indexPath);
            long startRead = System.currentTimeMillis();
            TreeMap<Double, Spectrum> ms1Map = parser.getMs1SpectraMap();
            readTime += (System.currentTimeMillis() - startRead);

            for (int i = 0; i < targets.size(); i++) {
                double target = targets.get(i);
                double targetStart = target - 0.015;
                double targetEnd = target + 0.015;
                double[] xic = new double[ms1Map.size()];
                Collection<Spectrum> ms1List = ms1Map.values();
                int iter = 0;
                for (Spectrum spectrum : ms1List) {
                    double result = Extractor.accumulation(spectrum, targetStart, targetEnd);
                    xic[iter] = result;
                    iter++;
                }
            }
            parser.close();
        }

        System.out.println("模拟搜索" + simulatorFiles + "个文件中搜索" + targets.size()
                + "个靶标的时间为：" + (System.currentTimeMillis() - startTotal) / 1000d + "秒");
        System.out.println("文件读取解码时间：" + readTime/ 1000d +"秒");
    }

    @Test
    public void HowManyDiffMz() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        List<BlockIndex> indexList = airdInfo.getIndexList();
        TreeMap<Double, Spectrum> ms1Map = parser.getMs1SpectraMap();
        HashSet<Double> mzs = new HashSet<>();
        HashSet<Double> ints = new HashSet<>();
        for (Spectrum value : ms1Map.values()) {
            for (int i = 0; i < value.getMzs().length; i++) {
                mzs.add(value.getMzs()[i]);
                ints.add(value.getInts()[i]);
            }
        }
        System.out.println("不同的质荷比：" + mzs.size());
        System.out.println("不同的强度值：" + ints.size());
    }

    @Test
    public void precisionControl() throws Exception {
        long startTime = System.currentTimeMillis();
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        int mzPrecsion = 0;
        int intPrecsion = 0;
        for (Compressor compressor : airdInfo.getCompressors()) {
            if (compressor.getTarget().equals(Compressor.TARGET_MZ)) {
                mzPrecsion = compressor.getPrecision();
            } else if (compressor.getTarget().equals(Compressor.TARGET_INTENSITY)) {
                intPrecsion = compressor.getPrecision();
            }
        }
        TreeMap<Double, Spectrum> ms1Map = parser.getMs1SpectraMap();
        List<BlockIndex> indexList = airdInfo.getIndexList();
        System.out.println("质谱数据读取完毕,耗时" + (System.currentTimeMillis() - startTime));
        System.out.println(indexList.get(0).getNums().size() + "张谱图,MS1块大小为:" + (indexList.get(0).getEndPtr() - indexList.get(0).getStartPtr()) / 1024 / 1024 + "MB");
        startTime = System.currentTimeMillis();
        HashSet<Double> mzs2dpSet = new HashSet<>();
        HashSet<Double> mzs3dpSet = new HashSet<>();
        HashSet<Double> mzs4dpSet = new HashSet<>();
        for (Spectrum value : ms1Map.values()) {
            for (int i = 0; i < value.getMzs().length; i++) {
                mzs2dpSet.add(PrecisionUtil.trunc(value.getMzs()[i], 100));
                mzs3dpSet.add(PrecisionUtil.trunc(value.getMzs()[i], 1000));
                mzs4dpSet.add(PrecisionUtil.trunc(value.getMzs()[i], 10000));
            }
        }
        System.out.println("2dp下有效mz有" + mzs2dpSet.size() + "个");
        System.out.println("3dp下有效mz有" + mzs3dpSet.size() + "个");
        System.out.println("4dp下有效mz有" + mzs4dpSet.size() + "个");
    }

    @Test
    public void trans() throws Exception {
        long startTime = System.currentTimeMillis();
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        int mzPrecsion = 0;
        int intPrecsion = 0;
        for (Compressor compressor : airdInfo.getCompressors()) {
            if (compressor.getTarget().equals(Compressor.TARGET_MZ)) {
                mzPrecsion = compressor.getPrecision();
            } else if (compressor.getTarget().equals(Compressor.TARGET_INTENSITY)) {
                intPrecsion = compressor.getPrecision();
            }
        }
        TreeMap<Double, Spectrum> ms1Map = parser.getMs1SpectraMap();
        List<BlockIndex> indexList = airdInfo.getIndexList();
        System.out.println("质谱数据读取完毕,耗时" + (System.currentTimeMillis() - startTime));
        System.out.println(indexList.get(0).getNums().size() + "张谱图,MS1块大小为:" + (indexList.get(0).getEndPtr() - indexList.get(0).getStartPtr()) / 1024 / 1024 + "MB");
        startTime = System.currentTimeMillis();
        HashSet<Double> mzsSet = new HashSet<>();
        for (Spectrum value : ms1Map.values()) {
            for (int i = 0; i < value.getMzs().length; i++) {
                mzsSet.add(value.getMzs()[i]);
            }
        }
        List<Double> mzs = new ArrayList<>(mzsSet);
        Collections.sort(mzs);

        System.out.println("质荷比统计完成，总计有" + mzs.size() + "个不同的质荷比,耗时：" + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        double firstMz = mzs.get(0);
        double lastMz = mzs.get(mzs.size() - 1);
        System.out.println("最小值为：" + firstMz + "-最大值为：" + lastMz);
        Collection<Spectrum> ms1List = ms1Map.values();
        HashMap<Integer, Integer> ptrMap = new HashMap<>();
        for (int i = 0; i < ms1Map.size(); i++) {
            ptrMap.put(i, 0);
        }
        System.out.println("宽度为" + ms1Map.size());

        int totalIntensityNum = 0;
        for (Spectrum spectrum : ms1Map.values()) {
            totalIntensityNum += spectrum.getInts().length;
        }
        System.out.println("总计包含有效点数：" + totalIntensityNum);
        long totalSize = 0;
        int step = 1;
        List<Spectrum> spectra = new ArrayList<>(ms1Map.values());
        long totalPoint = 0;
        TreeMap<Double, CompressedPairs> treeRow = new TreeMap<>();
        for (Double mz : mzs) {
            //初始化一个Map用于存放列元素
            List<Integer> spectrumIdList = new ArrayList<>();
            List<Integer> intensityList = new ArrayList<>();
            int spectrumId = 0;
            step++;
            if (step % 100000 == 0) {
                System.out.println("进度：" + step * 100 / mzs.size() + "%");
            }
            for (int i = 0; i < spectra.size(); i++) {
                Spectrum spectrum = spectra.get(i);
                double[] currentMzs = spectrum.getMzs();
                double[] currentInts = spectrum.getInts();
                int iter = ptrMap.get(spectrumId);
                boolean effect = false;
                int intensity = 0;
                while (iter < currentMzs.length && currentMzs[iter] == mz) {
                    effect = true;
                    intensity += (int) currentInts[iter];
                    iter++;
                }
                if (effect) {
                    spectrumIdList.add(spectrumId);
                    intensityList.add(intensity);
                    ptrMap.put(spectrumId, iter);
                }
                spectrumId++;
            }
            int[] spectrumIds = ArrayUtil.toIntPrimitive(spectrumIdList);
            int[] intIds = ArrayUtil.toIntPrimitive(intensityList);
            totalPoint += intIds.length;
            byte[] compressedSpectrumIds = new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedVarByteWrapper().encode(spectrumIds)));
            byte[] compressedInts = new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(intIds)));
            treeRow.put(mz, new CompressedPairs(compressedSpectrumIds, compressedInts));
            totalSize += (compressedSpectrumIds.length + compressedInts.length);
        }
        System.out.println("全部数据处理完毕，总耗时：" + (System.currentTimeMillis() - startTime));
        System.out.println("有效点数" + totalPoint + "个");
        System.out.println("总体积为：" + totalSize / 1024 / 1024 + "MB");

        //开始测试读取性能
        random();
        long readTime = System.currentTimeMillis();
        int simulatorFiles = 10;

        for (int count = 0; count < simulatorFiles; count++) {
            List<Double> mzsToSearch = new ArrayList<>(treeRow.keySet());
            List<Double> rtList = new ArrayList<>(ms1Map.keySet());
            targets.parallelStream().forEach(target->{
                double targetStart = target - 0.015;
                double targetEnd = target + 0.015;
                IntPair leftPair = AirdMathUtil.binarySearch(mzsToSearch, targetStart);
                IntPair rightPair = AirdMathUtil.binarySearch(mzsToSearch, targetEnd);
                List<Double> targetRange = mzs.subList(leftPair.right(), rightPair.right());
                List<HashMap<Integer, Integer>> mapList = new ArrayList<>();
                for (Double mz : targetRange) {
                    HashMap<Integer, Double> map = new HashMap<>();
                    CompressedPairs pairs = treeRow.get(mz);
                    int[] spectraIds = new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(pairs.left())));
                    int[] ints = new VarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(pairs.right())));
                    mapList.add(ArrayUtil.toMap(spectraIds, ints));
                }
                double[] intensities = new double[rtList.size()];
                for (int k = 0; k < rtList.size(); k++) {
                    double intensity = 0;
                    for (HashMap<Integer, Integer> map : mapList) {
                        if (map.containsKey(k)){
                            intensity+=map.get(k);
                        }
                    }
                    intensities[k] = intensity;
                }
            });
            parser.close();
        }
        System.out.println("读取时间为："+(System.currentTimeMillis() - readTime));

    }
}
