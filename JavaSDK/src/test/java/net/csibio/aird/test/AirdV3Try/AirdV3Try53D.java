package net.csibio.aird.test.AirdV3Try;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.test.AirdV3Try.Compressor.WaveCompressor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AirdV3Try53D {
    static String indexPath = "E:\\msfile_converted\\DDA-Sciex-MTBLS733-SampleA_3.json";

    @Test
    public void test() throws Exception {
        DDAParser parser = new DDAParser(indexPath);
        List<DDAMs> ms1List = parser.readAllToMemory();

        ArrayList[] buckets = new ArrayList[3000];

        BlockIndex index = parser.getAirdInfo().getIndexList().get(0);
        System.out.println("Aird中ms1块-mz大小：" + index.getMzs().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");
        System.out.println("Aird中ms1块-intensity大小：" + index.getInts().stream().mapToInt(Integer::intValue).sum() / 1024 / 1024 + "MB");

        System.out.println();
//        System.out.println(JSON.toJSON(index.getInts().subList(0,1000)));


        long totalSize = 0l;
        for (int i = 0; i < ms1List.size(); i++) {
//            int[] intArray = convertDoubleToInt(ms1List.get(i).getSpectrum().getInts());
            double[] encodedInts = WaveCompressor.encode(ms1List.get(i).getSpectrum().getInts());
//            int[] encodedInts2 = AdaptiveIntegerCompressor.encode(encodedInts);
//            int[] encodedInts3 = QuadraticPredictioinIntegerCompressor.encode(intArray);


            System.out.println(JSON.toJSON(encodedInts));
//            totalSize += CompressiveSensingCompressor.encodeSpectrumList(ms1List);
        }
        System.out.println("压缩后大小：" + totalSize / 1024 / 1024 + "MB");
    }

}
