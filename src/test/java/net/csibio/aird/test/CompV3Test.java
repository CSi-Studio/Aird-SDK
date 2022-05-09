package net.csibio.aird.test;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteCompressor;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.XDPD;
import net.csibio.aird.enums.ByteCompType;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static net.csibio.aird.enums.ByteCompType.Zlib;

public class CompV3Test {

    static String indexPath = "D:\\Aird_Test\\SA1_6_with_zero.json";
    static int MB = 1024 * 1024;
    static int KB = 1024;

    @Test
    public void testV1() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        Compressor mzCompressor = airdInfo.fetchCompressor(Compressor.TARGET_MZ);
        double mzPrecision = mzCompressor.getPrecision();
        int maxDelta = (int) (0.001 * mzPrecision);
        List<DDAMs> ms1List = parser.readAllToMemory();
        HashMap<Integer, AtomicLong> accumu = new HashMap<Integer, AtomicLong>();
        HashMap<Integer, AtomicLong> accumu2 = new HashMap<Integer, AtomicLong>();
        for (DDAMs ms1 : ms1List) {
            Spectrum<double[]> spectrum = ms1.getSpectrum();
            double[] mzs = spectrum.getMzs();
            for (int i = 1; i < mzs.length; i++) {
                Integer delta = (int) (mzs[i] * mzPrecision) - (int) (mzs[i - 1] * mzPrecision);
                if (accumu.get(delta) == null) {
                    accumu.put(delta, new AtomicLong(1));
                } else {
                    accumu.get(delta).getAndAdd(1);
                }

                if (delta < maxDelta) {
                    if (accumu2.get(delta) == null) {
                        accumu2.put(delta, new AtomicLong(1));
                    } else {
                        accumu2.get(delta).getAndAdd(1);
                    }
                }
            }
            int a = 1;
        }
        System.out.println("Accum:" + accumu.size());
        System.out.println("Accum2:" + accumu2.size());
        List newList = accumu2.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().get()))
                .toList();
        System.out.println(newList.size());
    }

    @Test
    public void testNewAlgorithm() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        Compressor mzCompressor = airdInfo.fetchCompressor(Compressor.TARGET_MZ);
        double mzPrecision = mzCompressor.getPrecision();
        List<DDAMs> ms1List = parser.readAllToMemory();
        AtomicLong totalMzs = new AtomicLong(0);
        AtomicLong totalOri = new AtomicLong(0);
        AtomicLong totalZdpd = new AtomicLong(0);
        AtomicLong totalNew = new AtomicLong(0);
        AtomicLong totalFirst = new AtomicLong(0);
        AtomicLong totalSecond = new AtomicLong(0);
        ms1List.parallelStream().forEach(ms1 -> {
            Spectrum<double[]> spectrum = ms1.getSpectrum();
            double[] mzs = spectrum.getMzs();

            totalMzs.getAndAdd(mzs.length);
//      System.out.println("原始大小:" + mzs.length * 4 / KB + " bytes");
            totalOri.getAndAdd(mzs.length * 4L);
            int first = 0;
            int second = 0;
            int[] firsts = new int[mzs.length];
            int[] seconds = new int[mzs.length];
            for (int i = 0; i < mzs.length; i++) {
                double mz = mzs[i];
                mz = mz * 1000000;
                first = (int) mz;
                second = (int) (1 * (mz - first));
                firsts[i] = first;
                seconds[i] = second;
            }
            byte[] firstBytes = XDPD.encode(firsts);
//      RoaringBitmap rr = RoaringBitmap.bitmapOf(seconds);
            byte[] secondBytes = new ByteCompressor(Zlib).encode(ByteTrans.intToByte(seconds));
//      System.out.println("压缩后大小:" + (firstBytes.length + secondBytes.length) / KB + " bytes");
            totalFirst.getAndAdd(firstBytes.length);
            totalSecond.getAndAdd(secondBytes.length);
            totalNew.getAndAdd(firstBytes.length + secondBytes.length);
            totalZdpd.getAndAdd(XDPD.encode(spectrum.getMzs(), mzPrecision, Zlib).length);
        });

        System.out.println("total:" + totalMzs.get());
        System.out.println("avg:" + totalMzs.get() / airdInfo.getTotalCount());
        System.out.println("totalSize:" + totalOri.get() / MB);
        System.out.println("totalZDPDSize:" + totalZdpd.get() / MB);
        System.out.println("totalFirstSize:" + totalFirst.get() / MB);
        System.out.println("totalSecondSize:" + totalSecond.get() / MB);
        System.out.println("totalNewSize:" + totalNew.get() / MB);
    }

    @Test
    public void test() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        Compressor mzCompressor = airdInfo.fetchCompressor(Compressor.TARGET_MZ);
        double mzPrecision = mzCompressor.getPrecision();
        Compressor intCompressor = airdInfo.fetchCompressor(Compressor.TARGET_INTENSITY);
        double intPrecision = intCompressor.getPrecision();
        AtomicLong mzOri = new AtomicLong(0);
        AtomicLong intOri = new AtomicLong(0);
        AtomicLong process = new AtomicLong(0);
//    List<CompressorType> byteTypes = Arrays.asList(Zlib, CompressorType.Gzip, CompressorType.LZMA2, CompressorType.Sna);
        List<ByteCompType> byteTypes = Arrays.asList(Zlib);
        ConcurrentHashMap<String, AtomicLong> mzMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, AtomicLong> intMap = new ConcurrentHashMap<>();
        for (ByteCompType value : byteTypes) {
            mzMap.put(value.name(), new AtomicLong(0));
            intMap.put(value.name(), new AtomicLong(0));
        }

        mzMap.put("ZDPD", new AtomicLong(0));

        List<DDAMs> ms1List = parser.readAllToMemory();
        System.out.println("开始压缩数据,总计光谱:" + airdInfo.getTotalCount());
        ms1List.parallelStream().forEach(ms1 -> {
            Spectrum<double[]> spectrum = ms1.getSpectrum();
            byte[] mzBytes = ByteTrans.floatToByte(ByteTrans.doubleToFloat(spectrum.getMzs()));
            byte[] intBytes = ByteTrans.floatToByte(spectrum.getInts());

            mzOri.getAndAdd(mzBytes.length);
            intOri.getAndAdd(intBytes.length);
            byteTypes.parallelStream().forEach(value -> {
                ByteCompressor compressor = new ByteCompressor(value);
                byte[] mzCompBytes = compressor.encode(mzBytes);
                mzMap.get(value.name()).getAndAdd(mzCompBytes.length);
                byte[] intCompBytes = compressor.encode(intBytes);
                intMap.get(value.name()).getAndAdd(intCompBytes.length);
            });

            mzMap.get("ZDPD").getAndAdd(XDPD.encode(spectrum.getMzs(), mzPrecision, Zlib).length);
            process.getAndIncrement();
            if (process.get() % 100 == 0) {
                System.out.println("当前进度:" + process.get());
            }
        });

        StringBuilder algorithms = new StringBuilder("    Ori/");
        for (ByteCompType value : byteTypes) {
            algorithms.append(value.name()).append("/");
        }
        algorithms.append("ZDPD/");
        System.out.println(algorithms);
        StringBuilder mzs = new StringBuilder("mz: " + mzOri.get() / MB + "/");
        StringBuilder ints = new StringBuilder("int:" + intOri.get() / MB + "/");
        for (ByteCompType value : byteTypes) {
            mzs.append(mzMap.get(value.name()).get() / MB).append(" /");
            ints.append(intMap.get(value.name()).get() / MB).append(" /");
        }
        mzs.append(mzMap.get("ZDPD").get() / MB).append(" /");
        System.out.println(mzs);
        System.out.println(ints);


    }

}
