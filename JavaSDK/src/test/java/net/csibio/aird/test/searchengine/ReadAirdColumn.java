package net.csibio.aird.test.searchengine;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.CompressedPairs;
import net.csibio.aird.bean.common.IntPair;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.ColumnParser;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.util.AirdMathUtil;
import net.csibio.aird.util.ArrayUtil;
import net.csibio.aird.util.PrecisionUtil;
import org.junit.Test;

import java.util.*;

public class ReadAirdColumn {

//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp\\DDA-Thermo-MTBLS733-SA1.json";
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\4dp\\DDA-Sciex-MTBLS733-SampleA_1.json";
    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp-SearchEngine\\LIPPOS-1-A-B-SAM-21.json";

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
        ColumnParser parser = new ColumnParser(indexPath);
        parser.getColumns(599.3102d, 599.3302d, null);
    }
}
