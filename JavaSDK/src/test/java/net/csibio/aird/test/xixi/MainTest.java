package net.csibio.aird.test.xixi;

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

public class MainTest {

    static String indexPath = "D:\\Repo\\DDA_Control_r1.json";
    static int MB = 1024 * 1024;
    static int KB = 1024;

    @Test
    public void testV1() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        List<DDAMs> msList = parser.readAllToMemory();
        System.out.println(msList.size());
        Spectrum spectrum = msList.get(0).getSpectrum();
        
    }
}
