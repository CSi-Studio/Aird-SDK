package net.csibio.aird.sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.MzIntensityPairs;
import net.csibio.aird.parser.DIAParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TryForAirdV3 {

    public static void main(String[] args){
        long startX = System.currentTimeMillis();

        DIAParser parser = new DIAParser("/Users/lms/proteomics/HYE4_32_fix/HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
        AirdInfo info = parser.getAirdInfo();
        BlockIndex index = info.getIndexList().get(1);
        TreeMap<Float, MzIntensityPairs> map = parser.getSpectrums(index);

        AtomicInteger count = new AtomicInteger();
        map.forEach((key,value)->{
            count.addAndGet(value.getMzArray().length);
        });

        int[] leftMz = new int[count.get()];
        int[] rightMz = new int[count.get()];
        int[][] mzGroup = new int[count.get()][2];
        int realCount = 0;
        int precision = info.fetchCompressor(Compressor.TARGET_MZ).getPrecision();
        for (int i = 50; i < map.entrySet().size(); i++) {
            float[] mzArray = map.pollFirstEntry().getValue().getMzArray();
            for (int k = 0; k < mzArray.length; k++) {
                int left = (int)mzArray[k];
                mzGroup[realCount+k][0] = left;
                mzGroup[realCount+k][1] = (int)((mzArray[k] - left)*precision);
            }
            realCount+=mzArray.length;
            if (i>100){
                break;
            }
        }

        System.out.println("Real Count:"+realCount);
        int[][] usedMzGroup = Arrays.copyOfRange(mzGroup, 0, realCount);

        System.out.println(JSON.toJSONString(usedMzGroup));
        System.out.println("Time Cost:"+(System.currentTimeMillis() - startX));
    }
}
