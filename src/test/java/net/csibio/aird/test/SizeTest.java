package net.csibio.aird.test;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.parser.v2.DIAParser;
import net.csibio.aird.util.CompressUtil;
import org.junit.Test;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class SizeTest {

    String DIA_INDEX_FILE_PATH = "D:\\proteomics\\Project\\HYE_110_64var\\HYE110_TTOF6600_64var_lgillet_I160305_001.json";
    int MB = 1024 * 1024;

    @Test
    public void testForDIA() {
        DIAParser diaParser = new DIAParser(DIA_INDEX_FILE_PATH);
        AirdInfo airdInfo = diaParser.getAirdInfo();
        Compressor mzCompressor = airdInfo.fetchCompressor(Compressor.TARGET_MZ);
        Compressor intCompressor = airdInfo.fetchCompressor(Compressor.TARGET_INTENSITY);
        List<BlockIndex> indexList = diaParser.getAirdInfo().getIndexList();


        AtomicLong mzOri = new AtomicLong(0);
        AtomicLong mzZip = new AtomicLong(0);
        AtomicLong mzZdpd = new AtomicLong(0);

        AtomicLong intOri = new AtomicLong(0);
        AtomicLong intZip = new AtomicLong(0);

        for (int k = 31; k < 32; k++) {
            BlockIndex index = indexList.get(k);
            TreeMap<Float, Spectrum> spectrumMap = diaParser.getSpectrums(index);
            List<Spectrum> spectrumList = spectrumMap.values().stream().toList();
            spectrumList.forEach(spectrum -> {
                mzOri.getAndAdd(spectrum.mzs().length * 4L);
                intOri.getAndAdd(spectrum.ints().length * 4L);

                mzZip.getAndAdd(CompressUtil.transToByte(spectrum.mzs()).length);
                intZip.getAndAdd(CompressUtil.transToByte(spectrum.ints()).length);

                mzZdpd.getAndAdd(CompressUtil.ZDPDEncoder(spectrum.mzs(), mzCompressor).length);
            });
            System.out.println("当前处理:" + k + "/" + indexList.size());
        }


        System.out.println("m/z:Ori/Zip/ZDPD-" + mzOri.get() / MB + "/" + mzZip.get() / MB + "/" + mzZdpd.get() / MB);
        System.out.println("int:Ori/Zip/ZDPD-" + intOri.get() / MB + "/" + intZip.get() / MB);
    }
}
