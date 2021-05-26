package net.csibio.aird.test;

import com.alibaba.fastjson.JSON;
import net.csibio.aird.bean.*;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class TryForAirdV3 {

    @Test
    public void leftRightStore() throws Exception {
        long startX = System.currentTimeMillis();
//        DIAParser parser = new DIAParser("/Users/lms/proteomics/HYE4_32_fix/HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
        DIAParser parser = new DIAParser("/Users/lms/proteomics/SGS4/napedro_L120224_010_SW.json");
        AirdInfo info = parser.getAirdInfo();
        int precision = info.fetchCompressor(Compressor.TARGET_MZ).getPrecision();
        int offset = 10000;
        System.out.println("Precision:"+precision);
        for (int i = 0; i < info.getIndexList().size(); i++) {
            BlockIndex index = info.getIndexList().get(i);
            TreeMap<Float, MzIntensityPairs> map = parser.getSpectrums(index);
            long totalMzSize = 0;
            long totalNewSize = 0;
            for (int k = 0; k < map.entrySet().size(); k++) {
                MzIntensityPairs pairs = map.pollFirstEntry().getValue();
                totalMzSize += index.getMzs().get(k);
                float[] mzArray = pairs.getMzArray();
                int[] leftArray = new int[mzArray.length];
                short[] rightArray = new short[mzArray.length];
                for (int j = 0; j < mzArray.length; j++) {
                    int temp = (int) (mzArray[j] * precision);
                    int left = temp / offset;
                    leftArray[j] = left;
                    rightArray[j] = (short) (temp - left * offset);
                }
                byte[] s1 = CompressUtil.ZDPDEncoder(leftArray);
                byte[] s2 = CompressUtil.transToByte(rightArray);
                totalNewSize += (s1.length + s2.length);
            }
            System.out.println("Index"+i+"-" + totalNewSize*1.0/totalMzSize);
        }

        System.out.println("Time Cost:" + (System.currentTimeMillis() - startX));
    }

    @Test
    public void testForDDA() throws Exception {
        long startX = System.currentTimeMillis();

        DDAParser parser = new DDAParser("/Users/lms/metabolomics/DIAN 14-19VW_Human_Serum/14-19VW_NEGa_SET1/QXA01DNNEG20190723_DIAN1419VWHUMANSERUM_HUMAN_SERUM1_03.json");
        AirdInfo info = parser.getAirdInfo();
        List<MsCycle> cycleList = parser.parseToMsCycle();

        AtomicInteger count = new AtomicInteger();
        for (int j = 0; j < cycleList.size(); j++) {
            MsCycle cycle = cycleList.get(j);
            MzIntensityPairs pairs = cycle.getMs1Spectrum();
            count.getAndAdd(pairs.getMzArray().length);
        }

        int[][] mzGroup = new int[count.get()][2];
        int realCount = 0;
        int precision = info.fetchCompressor(Compressor.TARGET_MZ).getPrecision();

        for (int j = 0; j < cycleList.size(); j++) {
            MsCycle cycle = cycleList.get(j);
            MzIntensityPairs pairs = cycle.getMs1Spectrum();
            float[] mzArray = pairs.getMzArray();
            float[] intArray = pairs.getIntensityArray();
            for (int k = 0; k < mzArray.length; k++) {
                if (intArray[k] < 10000) {
                    continue;
                }
                int left = (int) mzArray[k];
                mzGroup[realCount][0] = left;
                mzGroup[realCount][1] = (int) ((mzArray[k] - left) * precision);
                realCount++;
            }
            if (j > 100) {
                break;
            }
        }
        System.out.println("Real Count:" + realCount);
        int[][] usedMzGroup = Arrays.copyOfRange(mzGroup, 0, realCount);

        System.out.println(JSON.toJSONString(usedMzGroup));
        System.out.println("Time Cost:" + (System.currentTimeMillis() - startX));
    }

    @Test
    public void testForDIA() {
        long startX = System.currentTimeMillis();

        DIAParser parser = new DIAParser("/Users/lms/proteomics/HYE4_32_fix/HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        DIAParser parser = new DIAParser("/Users/lms/proteomics/SGS4/napedro_L120224_010_SW.json");
        AirdInfo info = parser.getAirdInfo();
        BlockIndex index = info.getIndexList().get(1);

        int[][] mzGroup = new int[999999][2];
        int realCount = 0;
        int precision = info.fetchCompressor(Compressor.TARGET_MZ).getPrecision();
        TreeMap<Float, MzIntensityPairs> map = parser.getSpectrums(index);
        System.out.println("Real Count:" + realCount);
        for (int k = 0; k < 100; k++) {
            map.pollFirstEntry();
        }
        for (int i = 0; i < map.entrySet().size(); i++) {
            MzIntensityPairs pairs = map.pollFirstEntry().getValue();
            float[] mzArray = pairs.getMzArray();
            float[] intArray = pairs.getIntensityArray();
            for (int k = 0; k < mzArray.length; k++) {
                if (intArray[k] < 100) {
                    continue;
                }
                mzArray[k] = mzArray[k]*10;
                int left = (int) (mzArray[k]);
                mzGroup[realCount][0] = left;
                mzGroup[realCount][1] = (int) ((mzArray[k] - left) * precision/10);
                realCount++;
            }
        }

        System.out.println("Real Count:" + realCount);
        int[][] usedMzGroup = Arrays.copyOfRange(mzGroup, 0, realCount);

        System.out.println(JSON.toJSONString(usedMzGroup));
        System.out.println("Time Cost:" + (System.currentTimeMillis() - startX));
    }
}
