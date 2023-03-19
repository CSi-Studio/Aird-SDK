package net.csibio.aird.test.matrix;

import com.sun.source.tree.Tree;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

import java.util.*;

public class AirdMatrixTestForDDA {

    static String indexPath = "D:\\AirdTest\\ComboComp\\File1.json";

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
        System.out.println(indexList.size() + "张谱图");
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
        System.out.println("质谱数据读取完毕,耗时"+(System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        TreeSet<Double> mzs = new TreeSet<>();
        for (Spectrum value : ms1Map.values()) {
            for (int i = 0; i < value.getMzs().length; i++) {
                mzs.add(value.getMzs()[i]);
            }
        }
        System.out.println("质荷比统计完成，总计有" + mzs.size() + "个不同的质荷比,耗时："+(System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        int rowSize = ms1Map.size();
        int firstMz = (int) (mzs.first() * mzPrecsion) - 1; //保存左开右毕
        int lastMz = (int) (mzs.last() * mzPrecsion);
        int range = lastMz - firstMz;
        int block = range / 10;
        for (int start = firstMz; start <= lastMz; start += block) {
            //初始化一个Map用于存放列元素
            TreeMap<Double, int[]> rowMap = new TreeMap<>();
            for (Double mz : mzs) {
                if (mz < start){
                    continue;
                }
                if (mz > start && mz <= start + block){
                    rowMap.put(mz, new int[rowSize]);
                }
                if (mz > start + block){
                    break;
                }
            }
            System.out.println(firstMz+"_"+(firstMz+block)+"矩阵构建完成");
            int spectrumId = 0;
            for (Spectrum spectrum : ms1Map.values()) {
                double[] currentMzs = spectrum.getMzs();
                double[] currentInts = spectrum.getInts();
                int iter = 0;
                for (Double mz : mzs) {
                    if (mz < start){
                        continue;
                    }
                    if (mz > start && mz <= start + block){
                        if (currentMzs[iter] == mz) {
                            rowMap.get(mz)[spectrumId] = (int) (currentInts[iter] * intPrecsion);
                            iter++;
                        } else {
                            rowMap.get(mz)[spectrumId] = 0;
                        }
                    }else if (mz > start+block){
                        break;
                    }
                }
                spectrumId++;
                System.out.println("已经处理光谱：" + spectrumId + "/" + ms1Map.size());
            }
            System.out.println("数据处理完毕");
        }




    }
}
