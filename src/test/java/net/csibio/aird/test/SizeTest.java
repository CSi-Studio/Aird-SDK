package net.csibio.aird.test;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteCompressor;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.enums.ByteCompType;
import net.csibio.aird.compressor.XDPD;
import net.csibio.aird.parser.DIAParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SizeTest {

    String DIA_INDEX_FILE_PATH = "D:\\proteomics\\Project\\HYE_124_64var-6600\\HYE124_TTOF6600_64var_lgillet_I150211_008.json";
    //  String DIA_INDEX_FILE_PATH = "D:\\proteomics\\Project\\HYE_110_64Var_Full\\HYE110_TTOF6600_64var_lgillet_I160305_001_with_zero.json";
    //  String DIA_INDEX_FILE_PATH = "D:\\proteomics\\C20181208yix_HCC_DIA_T_46A_with_zero.json";
    int MB = 1024 * 1024;
    int KB = 1024;

    @Test
    public void testForDIA() throws Exception {
        DIAParser diaParser = new DIAParser(DIA_INDEX_FILE_PATH);
        AirdInfo airdInfo = diaParser.getAirdInfo();
        Compressor mzCompressor = airdInfo.fetchCompressor(Compressor.TARGET_MZ);
        int precision = mzCompressor.getPrecision();
        Compressor intCompressor = airdInfo.fetchCompressor(Compressor.TARGET_INTENSITY);
        List<BlockIndex> indexList = diaParser.getAirdInfo().getIndexList();

        AtomicLong mzOri = new AtomicLong(0);
        AtomicLong intOri = new AtomicLong(0);
        AtomicLong spectrumSize = new AtomicLong(0);
        List<ByteCompType> byteTypes = Arrays.asList(ByteCompType.Zlib,
                ByteCompType.Snappy, ByteCompType.Brotli);
        ConcurrentHashMap<String, AtomicLong> mzMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, AtomicLong> intMap = new ConcurrentHashMap<>();
        for (ByteCompType value : byteTypes) {
            mzMap.put(value.name(), new AtomicLong(0));
            intMap.put(value.name(), new AtomicLong(0));
        }
        mzMap.put("ZDPD", new AtomicLong(0));
        mzMap.put("BDPD", new AtomicLong(0));
        mzMap.put("SDPD", new AtomicLong(0));

        for (int k = 17; k < 18; k++) {
            BlockIndex index = indexList.get(k);
            long start = System.currentTimeMillis();
            TreeMap<Float, Spectrum<double[]>> spectrumMap = diaParser.getSpectra(index);
            System.out.println("Parse耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
            List<Spectrum<double[]>> spectrumList = spectrumMap.values().stream().toList();
            spectrumSize.getAndAdd(spectrumList.size());
            System.out.println("当前block中spectrum数目:" + spectrumList.size());
            spectrumList.parallelStream().forEach(spectrum -> {

                byte[] mzBytes = ByteTrans.floatToByte(ByteTrans.doubleToFloat(spectrum.getMzs()));
                byte[] intBytes = ByteTrans.floatToByte(spectrum.getInts());

                mzOri.getAndAdd(mzBytes.length);
                intOri.getAndAdd(intBytes.length);
                for (ByteCompType value : byteTypes) {
                    ByteCompressor compressor = new ByteCompressor(value);
                    byte[] mzCompBytes = compressor.encode(mzBytes);
                    mzMap.get(value.name()).getAndAdd(mzCompBytes.length);
                    byte[] intCompBytes = compressor.encode(intBytes);
                    intMap.get(value.name()).getAndAdd(intCompBytes.length);
                }

                mzMap.get("ZDPD").getAndAdd(
                        XDPD.encode(spectrum.getMzs(), precision, ByteCompType.Zlib).length);
                mzMap.get("BDPD").getAndAdd(
                        XDPD.encode(spectrum.getMzs(), precision, ByteCompType.Brotli).length);
                mzMap.get("SDPD").getAndAdd(
                        XDPD.encode(spectrum.getMzs(), precision, ByteCompType.Snappy).length);
            });
            System.out.println("当前处理:" + k + "/" + indexList.size());
        }

        StringBuilder algorithms = new StringBuilder("|Type|Ori|");
        for (ByteCompType value : byteTypes) {
            algorithms.append(value.name()).append("|");
        }
        algorithms.append("ZDPD|").append("BDPD|").append("SDPD|");
        System.out.println(algorithms);

        System.out.println("|----------|--------|----------|-------------------|");
        StringBuilder mzs = new StringBuilder("|mz| " + mzOri.get() / MB + "|");
        StringBuilder ints = new StringBuilder("|int|" + intOri.get() / MB + "|");
        for (ByteCompType value : byteTypes) {
            mzs.append(mzMap.get(value.name()).get() / MB).append(" |");
            ints.append(intMap.get(value.name()).get() / MB).append(" |");
        }
        mzs.append(mzMap.get("ZDPD").get() / MB).append(" |");
        mzs.append(mzMap.get("BDPD").get() / MB).append(" |");
        mzs.append(mzMap.get("SDPD").get() / MB).append(" |");
        System.out.println(mzs);
        System.out.println(ints);
    }
}
