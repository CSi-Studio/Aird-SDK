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
//    static String indexPath = "D:\\AirdMatrixTest\\Aird\\3dp-SearchEngine\\LIPPOS-1-A-B-SAM-21.json";
    static String indexPath = "D:\\AirdColumn\\Neiyansuo\\3dp\\LIPPOS-1-A-B-BLK-1.cjson";

    double[] targets = new double[]{967.97259,487.3524,753.61337,711.56642,864.63014,755.55625,647.51225,829.79845};

    @Test
    public void speedTest() throws Exception {
        long start = System.currentTimeMillis();
        ColumnParser parser = new ColumnParser(indexPath);
        for (double target : targets) {
            parser.getColumns(target-0.015, target+0.015, null,null,null);
        }
        System.out.println("Cost:"+(System.currentTimeMillis() - start));
    }
}
