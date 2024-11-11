package net.csibio.aird.test.AirdV3Try;

import com.alibaba.fastjson2.JSON;
import me.lemire.integercompression.BinaryPacking;
import me.lemire.integercompression.VariableByte;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.DDAPasefMs;
import net.csibio.aird.bean.Layers;
import net.csibio.aird.bean.common.MobiPoint;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.Delta;
import net.csibio.aird.compressor.Zigzag;
import net.csibio.aird.compressor.bytecomp.BrotliWrapper;
import net.csibio.aird.compressor.bytecomp.SnappyWrapper;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.BinPackingWrapper;
import net.csibio.aird.compressor.intcomp.DeltaZigzagVBWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.DeltaWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DDAPasefParser;
import net.csibio.aird.parser.DIAPasefParser;
import net.csibio.aird.util.ArrayUtil;
import net.csibio.aird.util.FileSizeUtil;
import net.csibio.aird.util.StackCompressUtil;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class AirdV3Try {

//    static String indexPath = "E:\\MzmineTest\\PXD033904_PASEF\\Aird\\20220302_tims1_nElute_8cm_DOl_Phospho_7min_rep1_Slot1-94_1_1811.json";
//    static String indexPath = "E:\\ComboCompTest\\Aird\\DDA-Sciex-MTBLS733-SampleA_1.json";

//    static String indexPath = "F:\\37-test-combination.json";
    static String indexPath = "F:\\43.json";

    static int MB = 1024 * 1024;
    static int KB = 1024;

    @Test
    public void testDIA() throws Exception {
        DIAPasefParser parser = new DIAPasefParser(indexPath);
        System.out.println("总计窗口："+parser.getAirdInfo().getIndexList().size()+"个");
        HashSet<Double> mzsSet = new HashSet<>();
        for (int k = 0; k < parser.getAirdInfo().getIndexList().size(); k++) {
            BlockIndex index = parser.getAirdInfo().getIndexList().get(k);
            List<Spectrum> spectra =  new ArrayList<>(parser.getSpectra(index).values());
            for (Spectrum spectrum : spectra) {
                mzsSet.addAll(Arrays.stream(spectrum.getMzs()).boxed().toList());
            }
        }
        System.out.println("总计差异mz"+mzsSet+"个");
        TreeSet<Double> mzsTreeSet = new TreeSet<Double>(mzsSet);
        HashMap<Double, Integer> mzMap = new HashMap<>();
        int tt = 0;
        for (Double d : mzsTreeSet) {
            mzMap.put(d, tt);
            tt++;
        }
        double[] mobi = parser.getMobiDict();
        HashMap<Double, Integer> dict = new HashMap<>();
        for (int d = 0; d < mobi.length; d++) {
            dict.put(mobi[d], d);
        }

        for (int k = 0; k < parser.getAirdInfo().getIndexList().size(); k++) {
            BlockIndex index = parser.getAirdInfo().getIndexList().get(k);
            long compressedMzs = 0;
            long compressedInts = 0;
            long compressedMobilities = 0;
            for (int t = 0; t < index.getMzs().size(); t++) {
                compressedMzs += index.getMzs().get(t);
                compressedInts += index.getInts().get(t);
                compressedMobilities += index.getMobilities().get(t);
            }

            System.out.println("总计质谱图" + index.getRts().size());
            System.out.println("Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
            System.out.println("Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
            System.out.println("Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
            System.out.println("Aird压缩后总大小" + FileSizeUtil.getSizeLabel(compressedMzs + compressedInts + compressedMobilities));

            List<Spectrum> spectra =  new ArrayList<>(parser.getSpectra(index).values());
            compressedMzs = 0;
            compressedInts = 0;
            compressedMobilities = 0;

            for (Spectrum spectrum : spectra) {
                double[] mzsD = spectrum.getMzs();
                double[] intsD = spectrum.getInts();
                double[] mobiD = spectrum.getMobilities();
                int[] mzsI = ByteTrans.doubleToInt(mzsD, mzMap);
                int[] insI = ByteTrans.doubleToInt(intsD, parser.getIntCompressor().getPrecision());
                int[] mobiI = ByteTrans.mobiToInt(mobiD, dict);
//            List<MobiPoint> points = new ArrayList<>();
                MobiPoint[] points = new MobiPoint[mzsI.length];

                for (int i = 0; i < mzsI.length; i++) {
                    points[i] = new MobiPoint(mzsI[i], insI[i], mobiI[i]);
                }
                Arrays.sort(points, Comparator
                    .comparingInt(MobiPoint::mz)
                    .thenComparingInt(MobiPoint::mobi));
                int[] newMzs = new int[mzsI.length];

                int[] newInts = new int[insI.length];
                int[] newMobis = new int[mobiI.length];
                for (int i = 0; i < points.length; i++) {
                    newMzs[i] = points[i].mz();
                    newInts[i] = points[i].intensity();
                    newMobis[i] = points[i].mobi();
                }

                compressedMzs += new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedVarByteWrapper().encode(newMzs))).length;
                compressedInts += new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(newInts))).length;
                compressedMobilities += new ZstdWrapper().encode(ByteTrans.intToByte(new DeltaZigzagVBWrapper().encode(newMobis))).length;
            }

            System.out.println("不同的mz有"+mzsSet.size()+"个");
            System.out.println("新Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
            System.out.println("新Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
            System.out.println("新Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
            System.out.println("新Aird压缩后总大小" + FileSizeUtil.getSizeLabel(compressedMzs + compressedInts + compressedMobilities));
        }
    }

    @Test
    public void test0() throws Exception {
        DDAPasefParser parser = new DDAPasefParser(indexPath);
        double rtStart = 0;
        double rtEnd = 4000;
        long startTime = System.currentTimeMillis();
        List<DDAPasefMs> allSpectra = parser.getSpectraByRtRange(rtStart, rtEnd, false);
        System.out.println("读取耗时：" + (System.currentTimeMillis() - startTime));
        BlockIndex ms1Index = parser.getMs1Index();
        Double[] rts = new Double[ms1Index.getRts().size()];
        ms1Index.getRts().toArray(rts);

        int start = Arrays.binarySearch(rts, rtStart);
        if (start < 0) {
            start = -start - 1;
        }
        int end = Arrays.binarySearch(rts, rtEnd);
        if (end < 0) {
            end = -end - 2;
        }

        long compressedMzs = 0;
        long compressedInts = 0;
        long compressedMobilities = 0;

        for (int i = start; i <= end; i++) {
            compressedMzs += ms1Index.getMzs().get(i);
            compressedInts += ms1Index.getInts().get(i);
            compressedMobilities += ms1Index.getMobilities().get(i);
        }

        System.out.println("总计质谱图" + allSpectra.size());
        System.out.println("Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
        System.out.println("Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
        System.out.println("Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
        System.out.println("Aird压缩后总大小" + FileSizeUtil.getSizeLabel(compressedMzs + compressedInts + compressedMobilities));

        compressedMzs = 0;
        compressedInts = 0;
        compressedMobilities = 0;
        long compressedMobilities1 = 0;
        double[] mobi = parser.getMobiDict();
        HashMap<Double, Integer> dict = new HashMap<>();
        for (int i = 0; i < mobi.length; i++) {
            dict.put(mobi[i], i);
        }
        HashSet mzsSet = new HashSet<>();
        for (DDAPasefMs ddaPasefMs : allSpectra) {
            Spectrum spectrum = ddaPasefMs.getSpectrum();
            double[] mzsD = spectrum.getMzs();
            double[] intsD = spectrum.getInts();
            double[] mobiD = spectrum.getMobilities();
            int[] mzsI = ByteTrans.doubleToInt(mzsD, parser.getMzCompressor().getPrecision());
            int[] insI = ByteTrans.doubleToInt(intsD, parser.getIntCompressor().getPrecision());
            int[] mobiI = ByteTrans.mobiToInt(mobiD, dict);
//            List<MobiPoint> points = new ArrayList<>();
            MobiPoint[] points = new MobiPoint[mzsI.length];
            for (int i = 0; i < mzsI.length; i++) {
                points[i] = new MobiPoint(mzsI[i], insI[i], mobiI[i]);
                mzsSet.add(mzsI[i]);
            }
            Arrays.sort(points, Comparator
                            .comparingInt(MobiPoint::mz)
                            .thenComparingInt(MobiPoint::mobi));
            int[] newMzs = new int[mzsI.length];
            int[] newInts = new int[insI.length];
            int[] newMobis = new int[mobiI.length];
            for (int i = 0; i < points.length; i++) {
                newMzs[i] = points[i].mz();
                newInts[i] = points[i].intensity();
                newMobis[i] = points[i].mobi();
            }

            compressedMzs += new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedVarByteWrapper().encode(newMzs))).length;
            compressedInts += new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(newInts))).length;
            compressedMobilities += new ZstdWrapper().encode(ByteTrans.intToByte(new DeltaZigzagVBWrapper().encode(newMobis))).length;
        }

        System.out.println("mzs" + mzsSet.size()+"个");
        System.out.println("新Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
        System.out.println("新Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
        System.out.println("新Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
        System.out.println("新Aird压缩后总大小" + FileSizeUtil.getSizeLabel(compressedMzs + compressedInts + compressedMobilities));
    }

    @Test
    public void testSort() throws Exception {
        int[] a = {1,2,2,2,3,4};
        int[] b = {100,200,400,300,500,900};
        int[] c = {1,4,3,2,5,7};

    }

    public void sort(int[] mzs, int[] ints, int[] mobis){
        int startValue = mzs[0];
        int startIndex = 0;

        for (int i = 1; i < mzs.length; i++) {
            if (mzs[i] == startValue){
                continue;
            }
            startValue = mzs[i];
            startIndex = i;
        }
    }

    @Test
    public void test1() throws Exception {
        DDAPasefParser parser = new DDAPasefParser(indexPath);
        double rtStart = 0;
        double rtEnd = 4000;
        long startTime = System.currentTimeMillis();
        List<DDAPasefMs> allSpectra = parser.getSpectraByRtRange(rtStart, rtEnd, false);
        System.out.println("读取耗时：" + (System.currentTimeMillis() - startTime));
        BlockIndex ms1Index = parser.getMs1Index();
        Double[] rts = new Double[ms1Index.getRts().size()];
        ms1Index.getRts().toArray(rts);

        int start = Arrays.binarySearch(rts, rtStart);
        if (start < 0) {
            start = -start - 1;
        }
        int end = Arrays.binarySearch(rts, rtEnd);
        if (end < 0) {
            end = -end - 2;
        }

        long compressedMzs = 0;
        long compressedInts = 0;
        long compressedMobilities = 0;
        for (int i = start; i <= end; i++) {
            compressedMzs += ms1Index.getMzs().get(i);
            compressedInts += ms1Index.getInts().get(i);
            compressedMobilities += ms1Index.getMobilities().get(i);
        }

        System.out.println("总计质谱图" + allSpectra.size());
        System.out.println("Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
        System.out.println("Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
        System.out.println("Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
        System.out.println("Aird压缩后总大小" + FileSizeUtil.getSizeLabel(compressedMzs + compressedInts + compressedMobilities));

        compressedMzs = 0;
        compressedInts = 0;
        compressedMobilities = 0;
        long compressedTags = 0;
        long compressedSteps = 0;
        double[] mobi = parser.getMobiDict();
        HashMap<Double, Integer> dict = new HashMap<>();
        for (int i = 0; i < mobi.length; i++) {
            dict.put(mobi[i], i);
        }
        for (DDAPasefMs ddaPasefMs : allSpectra) {
            Spectrum spectrum = ddaPasefMs.getSpectrum();
            double[] mzsD = spectrum.getMzs();
            double[] intsD = spectrum.getInts();
            double[] mobiD = spectrum.getMobilities();
            int[] mzsI = ByteTrans.doubleToInt(mzsD, parser.getMzCompressor().getPrecision());
            int[] insI = ByteTrans.doubleToInt(intsD, parser.getIntCompressor().getPrecision());
            int[] mobiI = ByteTrans.mobiToInt(mobiD, dict);
            List<MobiPoint> points = new ArrayList<>();
            for (int i = 0; i < mzsI.length; i++) {
                points.add(new MobiPoint(mzsI[i], insI[i], mobiI[i]));
            }

            Map<Integer, List<MobiPoint>> pointMap = points.stream().collect(
                    Collectors.groupingBy(MobiPoint::mobi,
                            Collectors.collectingAndThen(Collectors.toList(),
                                    list -> list.stream().sorted(Comparator.comparing(MobiPoint::mz)).collect(Collectors.toList()))));

            int[] newMobi = new int[pointMap.size()];
            int[] newMzs = new int[mzsD.length];
            int[] newInts = new int[mzsD.length];
            int[] steps = new int[pointMap.size()];
            List<int[]> mzsList = new ArrayList<>();
            int index = 0;
            int iter = 0;
            for (int key : pointMap.keySet()) {
                newMobi[index] = key;
                steps[index] = pointMap.get(key).size();
                List<MobiPoint> pointList = pointMap.get(key);
                int[] childMzs = new int[pointList.size()];
                int[] childInts = new int[pointList.size()];
                for (int i = 0; i < pointList.size(); i++) {
                    childMzs[i] = pointList.get(i).mz();
                    childInts[i] = pointList.get(i).intensity();
                }
                mzsList.add(childMzs);
                int[] childMzsB = new DeltaWrapper().encode(childMzs);

//                System.arraycopy(childMzsB, 0, newMzs, iter, childMzsB.length);
                System.arraycopy(childInts, 0, newInts, iter, childInts.length);
                iter+= childMzsB.length;
                index++;
            }
            int arrNum = 256;
            int groupNum = (mzsList.size()-1)/arrNum + 1;
            int fromIndex = 0;
            List<Layers> layersList = new ArrayList<>();
            for (int i = 0; i < groupNum - 1; i++) {
                List<int[]> arrGroup = mzsList.subList(fromIndex, fromIndex + arrNum);
                Layers layer = StackCompressUtil.stackEncode(arrGroup, true);
                layersList.add(layer);
                compressedMzs += layer.getMzArray().length;
                compressedTags += layer.getTagArray().length;
                fromIndex += arrNum;
            }
            //处理余数
            List<int[]> arrGroup = mzsList.subList(fromIndex, mzsList.size());
            Layers stackRemainder = StackCompressUtil.stackEncode(arrGroup, false);
            layersList.add(stackRemainder);
            compressedMzs += stackRemainder.getMzArray().length;
            compressedTags += stackRemainder.getTagArray().length;


//            compressedMzs += new ZstdWrapper().encode(ByteTrans.intToByte(new DeltaZigzagVBWrapper().encode(newMzs))).length;
            compressedInts += new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(newInts))).length;
            compressedMobilities += new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedVarByteWrapper().encode(newMobi))).length;
            compressedSteps += new ZstdWrapper().encode(ByteTrans.intToByte(new VarByteWrapper().encode(steps))).length;
        }

        System.out.println("新Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
        System.out.println("新Aird压缩后tag大小" + FileSizeUtil.getSizeLabel(compressedTags));
        System.out.println("新Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
        System.out.println("新Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
        System.out.println("新Aird压缩后Steps大小" + FileSizeUtil.getSizeLabel(compressedSteps));
        System.out.println("新Aird压缩后总大小" + FileSizeUtil.getSizeLabel(compressedMzs + compressedTags + compressedInts + compressedMobilities + compressedSteps));

    }

    @Test
    public void test2() throws Exception {
        DDAPasefParser parser = new DDAPasefParser(indexPath);
        double rtStart = 2000;
        double rtEnd = 4000;
        long startTime = System.currentTimeMillis();
        List<DDAPasefMs> allSpectra = parser.getSpectraByRtRange(rtStart, rtEnd, false);
        System.out.println("读取耗时：" + (System.currentTimeMillis() - startTime));
        BlockIndex ms1Index = parser.getMs1Index();
        Double[] rts = new Double[ms1Index.getRts().size()];
        ms1Index.getRts().toArray(rts);

        int start = Arrays.binarySearch(rts, rtStart);
        if (start < 0) {
            start = -start - 1;
        }
        int end = Arrays.binarySearch(rts, rtEnd);
        if (end < 0) {
            end = -end - 2;
        }

        long compressedMzs = 0;
        long compressedInts = 0;
        long compressedMobilities = 0;
        for (int i = start; i <= end; i++) {
            compressedMzs += ms1Index.getMzs().get(i);
            compressedInts += ms1Index.getInts().get(i);
            compressedMobilities += ms1Index.getMobilities().get(i);
        }

        System.out.println("总计质谱图" + allSpectra.size());
        System.out.println("Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
        System.out.println("Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
        System.out.println("Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));

        compressedMzs = 0;
        compressedInts = 0;
        compressedMobilities = 0;
        double[] mobi = parser.getMobiDict();
        HashMap<Double, Integer> dict = new HashMap<>();
        for (int i = 0; i < mobi.length; i++) {
            dict.put(mobi[i], i);
        }
        for (DDAPasefMs ddaPasefMs : allSpectra) {
            Spectrum spectrum = ddaPasefMs.getSpectrum();
            System.out.println(JSON.toJSONString(spectrum.getMzs()));
            int[] mobiInts = ByteTrans.mobiToInt(spectrum.getMobilities(), dict);
            System.out.println(JSON.toJSONString(mobiInts));
            int[] newMobiInts = new DeltaZigzagVBWrapper().encode(mobiInts);

            compressedMobilities += new ZstdWrapper().encode(ByteTrans.intToByte(newMobiInts)).length;
        }

        System.out.println("新Aird压缩后mz大小" + FileSizeUtil.getSizeLabel(compressedMzs));
        System.out.println("新Aird压缩后Intensity大小" + FileSizeUtil.getSizeLabel(compressedInts));
        System.out.println("新Aird压缩后Mobility大小" + FileSizeUtil.getSizeLabel(compressedMobilities));
    }

    @Test
    public void test3() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        long start = System.currentTimeMillis();
        List<DDAMs> allSpectra = parser.getSpectraByRtRange(0, 1000, false);
        System.out.println(allSpectra.size());
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }

    @Test
    public void test() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        List<DDAMs> ms1List = parser.readAllToMemory();

        int compact = 8;
        int originMzSize = 0;
        int originIntSize = 0;
        int compactMzSize = 0;
        int compactIntSize = 0;

        int[] compactMzArray = new int[0];
        double[] compactIntArray = new double[0];

        ArrayList<Integer> mzIndex = new ArrayList<>();
        ArrayList<Double> mzRates = new ArrayList<>();
        ArrayList<Integer> intIndex = new ArrayList<>();
        ArrayList<Double> intRates = new ArrayList<>();

        ArrayList<Integer> indexList = new ArrayList<>();
        ArrayList<Integer> pointList = new ArrayList<>();
        for (int i = 0; i < ms1List.size(); i++) {
            indexList.add(i);
            pointList.add(ms1List.get(i).getSpectrum().getMzs().length);
        }
        System.out.println(JSON.toJSON(indexList));
        System.out.println(JSON.toJSON(pointList));
        int k = 0;
        System.out.println("共有质谱图" + ms1List.size() + "张");
        for (int i = 0; i < ms1List.size(); i++) {
            if (i > ms1List.size() - 1) {
                break;
            }

            if (i != 0 && i % compact == 0) {
                compactMzSize = new ZstdWrapper().encode(ByteTrans.intToByte(compactMzArray)).length;
                compactIntSize = new ZstdWrapper().encode(ByteTrans.doubleToByte(compactIntArray)).length;
                double mzRate = (originMzSize - compactMzSize) * 1.0 / originMzSize;
                double intRate = (originIntSize - compactIntSize) * 1.0 / originIntSize;
                System.out.println("mz压缩增益：" + mzRate);
                System.out.println("int压缩增益：" + intRate);
                k++;
                mzIndex.add(k);
                intIndex.add(k);
                mzRates.add(mzRate);
                intRates.add(intRate);
                compactMzArray = new int[0];
                compactIntArray = new double[0];
                originMzSize = 0;
                originIntSize = 0;
            }
            double[] mzArray = ms1List.get(i).getSpectrum().getMzs();
            double[] intensityArray = ms1List.get(i).getSpectrum().getInts();
            byte[] byteMz = compressMz(mzArray);
            byte[] byteInt = compressInt(intensityArray);
            originMzSize += byteMz.length;
            originIntSize += byteInt.length;
            compactMzArray = ArrayUtil.mergeArrays(compactMzArray, bpMz(mzArray));
            compactIntArray = ArrayUtil.mergeArrays(compactIntArray, mzArray);
        }

//        compactMzSize = new ZstdWrapper().encode(ByteTrans.intToByte(compactMzArray)).length;
//        compactIntSize = compressInt(compactIntArray).length;
//        double mzRate = (originMzSize - compactMzSize) * 1.0 / originMzSize;
//        double intRate = (originIntSize - compactIntSize) * 1.0 / originIntSize;
//        System.out.println("mz压缩增益：" + mzRate);
//        System.out.println("int压缩增益：" + intRate);
//        k++;
//        mzIndex.add(k);
//        intIndex.add(k);
//        mzRates.add(mzRate);
//        intRates.add(intRate);
//        compactMzArray = new int[0];
//        compactIntArray = new double[0];
//        originMzSize = 0;
//        originIntSize = 0;

        System.out.println(JSON.toJSON(mzIndex));
        System.out.println(JSON.toJSON(mzRates));
        System.out.println(mzRates.stream().mapToDouble(Double::doubleValue).average());
        System.out.println(JSON.toJSON(intIndex));
        System.out.println(JSON.toJSON(intRates));


    }

    public int[] bpMz(double[] array) {
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int) Math.round(array[i] * 100000);
        }
        return new DeltaWrapper().encode(intArray);
    }

    public byte[] compressMz(double[] array) {
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int) Math.round(array[i] * 100000);
        }
        return new ZstdWrapper().encode(ByteTrans.intToByte(new IntegratedBinPackingWrapper().encode(intArray)));
    }

    public byte[] compressInt(double[] array) {
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = (int) Math.round(array[i]);
        }
        return new ZstdWrapper().encode(ByteTrans.intToByte(new BinPackingWrapper().encode(intArray)));
    }
}
