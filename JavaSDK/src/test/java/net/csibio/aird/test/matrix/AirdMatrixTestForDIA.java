package net.csibio.aird.test.matrix;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.eic.Extractor;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import org.junit.Test;

import java.util.*;

public class AirdMatrixTestForDIA {

    static String indexPath = "D:\\AirdTest\\ComboComp\\File6.json";

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
    public void HowManyDiffMz() throws Exception {
        DIAParser parser = new DIAParser(indexPath);
        AirdInfo airdInfo = parser.getAirdInfo();
        List<BlockIndex> indexList = airdInfo.getIndexList();

        for (BlockIndex blockIndex : indexList) {
            HashSet<Double> mzs = new HashSet<>();
            HashSet<Double> ints = new HashSet<>();
            TreeMap<Double, Spectrum> map = parser.getSpectra(blockIndex);
            for (Spectrum value : map.values()) {
                for (int i = 0; i < value.getMzs().length; i++) {
                    mzs.add(value.getMzs()[i]);
                    ints.add(value.getInts()[i]);
                }
            }
            if (blockIndex.getLevel() == 2) {
                System.out.println("Window:" + blockIndex.getWindowRange().getMz());
            } else {
                System.out.println("Window:Level1");
            }
            System.out.println("不同的质荷比：" + mzs.size());
            System.out.println("不同的强度值：" + ints.size());
        }


    }
}
