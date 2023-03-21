package net.csibio.aird.test.matrix;

import com.sun.source.tree.Tree;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.util.ArrayUtil;
import org.junit.Test;

import java.util.*;

public class AirdMatrixTestForDDA {

//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\DDA-Thermo-MTBLS733-SA1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\DDA-Sciex-MTBLS733-SampleA_1.json";
    static String indexPath = "D:\\AirdMatrixTest\\Aird\\DDA-Agilent-PXD004712-Set 3_F1.json";

    static List<Double> targets = new ArrayList<>();


    public void random() {
        for (int i = 0; i < 100; i++) {
            double random = new Random().nextDouble() * 16; //0-16之间的数字
            random += 4; //4-20之间的数字
            random *= 100; //400-2000之间的浮点数字
            targets.add(random);
        }
    }

    @Test
    public void speedTest() throws Exception {
        random();
        DDAParser parser = new DDAParser(indexPath);

        AirdInfo airdInfo = parser.getAirdInfo();
        List<BlockIndex> indexList = airdInfo.getIndexList();
        System.out.println(indexList.get(0).getNums().size() + "张谱图,MS1块大小为:" + (indexList.get(0).getEndPtr() - indexList.get(0).getStartPtr()) / 1024 / 1024 + "MB");
        long start = System.currentTimeMillis();
        TreeMap<Double, Spectrum> ms1Map = parser.getMs1SpectraMap();
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
        System.out.println("搜索时间：" + (System.currentTimeMillis() - start));
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
        for (Double mz : mzs) {
            //初始化一个Map用于存放列元素
            List<Integer> spectrumIdList = new ArrayList<>();
            List<Integer> intensityList = new ArrayList<>();
            int spectrumId = 0;
            step++;
            if (step % 100000 == 0) {
                System.out.println("进度：" + step * 100 / mzs.size() + "%");
            }
            for (Spectrum spectrum : ms1Map.values()) {
                double[] currentMzs = spectrum.getMzs();
                double[] currentInts = spectrum.getInts();
                int iter = ptrMap.get(spectrumId);

                if (iter < currentMzs.length && currentMzs[iter] == mz) { //使用while防止出现连续的mz
                    spectrumIdList.add(spectrumId);
                    intensityList.add((int) currentInts[iter]);
                    iter++;
                    ptrMap.put(spectrumId, iter);
                }
                spectrumId++;
            }
            int[] spectrumIds = ArrayUtil.toIntPrimitive(spectrumIdList);
            int[] intIds = ArrayUtil.toIntPrimitive(intensityList);
            byte[] compressedSpectrumIds = new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(spectrumIds)));
            byte[] compressedInts = new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(intIds)));
            totalSize += (compressedSpectrumIds.length+compressedInts.length);
        }
        System.out.println("全部数据处理完毕，总耗时：" + (System.currentTimeMillis() - startTime));
        System.out.println("总体积为：" + totalSize / 1024 / 1024 + "MB");

    }
}
