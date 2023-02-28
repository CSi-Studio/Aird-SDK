package net.csibio.aird.test;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdDictTrainer;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.SnappyWrapper;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.BinPackingWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedBinPackingWrapper;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ZstdDictTest {

    public static String path = "D:\\ComboCompTest\\Aird\\";
    public static String[] files = new String[]{
            path+"DDA-Agilent-PXD004712-Set 3_F1.json",
            path+"DDA-Agilent-PXD004712-Set 3_F2.json",
            path+"DDA-Sciex-MTBLS733-SampleA_3.json",
            path+"DDA-Sciex-MTBLS733-SampleA_2.json",
            path+"DDA-Sciex-MTBLS733-SampleA_1.json",
            path+"DDA-Sciex-MTBLS733-SampleA_1.json",
            path+"DDA-Thermo-MTBLS733-SA1.json"
    };
    @Test
    public void TrainDictForMz() throws Exception {
        IntegratedBinPackingWrapper ibp = new IntegratedBinPackingWrapper();
        ZstdWrapper zstdWrapper = new ZstdWrapper();
        SnappyWrapper snappyWrapper = new SnappyWrapper();
        ZstdDictCompress zstdDictCompress = null;
        byte[] dict = null;
        for (String file : files) {
            long finalLength = 0;
            long newFinalLength = 0;
            DDAParser parser = new DDAParser(file);
            List<DDAMs> msList = parser.readAllToMemory();
            ZstdDictTrainer trainer = new ZstdDictTrainer(100000000, 10000000);
            for (int i = 0; i < msList.size(); i++) {
                DDAMs ms = msList.get(i);
                Spectrum spectrum = ms.getSpectrum();
                double[] mzs = spectrum.getMzs();
                int[] intMzs = ByteTrans.doubleToInt(mzs, 100000);
                byte[] bytes = ByteTrans.intToByte(ibp.encode(intMzs));
                trainer.addSample(bytes);
                byte[] encodeBytes = zstdWrapper.encode(bytes);
                finalLength+=encodeBytes.length;
            }

            System.out.println("最终压缩大小为："+finalLength/1000d/1000d+"MB");
//            if (zstdDictCompress == null){
                dict = trainer.trainSamples();
                System.out.println("Dict Size："+dict.length/1000d/1000d+"MB");
                zstdDictCompress = new ZstdDictCompress(dict, Zstd.defaultCompressionLevel());
//            }

            for (int i = 0; i < msList.size(); i++) {
                DDAMs ms = msList.get(i);
                Spectrum spectrum = ms.getSpectrum();
                double[] mzs = spectrum.getMzs();
                int[] intMzs = ByteTrans.doubleToInt(mzs, 100000);
                byte[] bytes = ByteTrans.intToByte(ibp.encode(intMzs));
                byte[] oldBytes = zstdWrapper.encode(bytes);
                byte[] compBytes = new byte[bytes.length*10];
                long offset = Zstd.compress(compBytes, bytes, zstdDictCompress);
                byte[] finalBytes = Arrays.copyOfRange(compBytes, 0, (int)offset);
                long originSize = Zstd.decompressedSize(finalBytes,0, finalBytes.length);
                byte[] recover1 = new byte[(int)originSize];
                Zstd.decompress(recover1, finalBytes, dict);
//                System.out.println("length using dict:"+finalBytes.length);
                newFinalLength += offset;
            }
            System.out.println("使用字典后最终压缩大小为："+newFinalLength/1000d/1000d+"MB，压缩率提升"+ (finalLength-newFinalLength)*100d/finalLength+"%");
//            byte[] compressed = ComboComp.encode(new IntegratedBinPackingWrapper(), new ZstdWrapper(), mzs, 100000);
//            int[] decompMzs = ComboComp.decode(new IntegratedBinPackingWrapper(), new ZstdWrapper(), compressed);
//            System.out.println(decompMzs.length);
            System.out.println();
        }
    }

    @Test
    public void TrainDictForIntensity() throws Exception {
        BinPackingWrapper bp = new BinPackingWrapper();
        ZstdWrapper zstdWrapper = new ZstdWrapper();
        ZstdDictCompress zstdDictCompress = null;
        byte[] dict = null;
        for (String file : files) {
            long finalLength = 0;
            long newFinalLength = 0;
            DDAParser parser = new DDAParser(file);
            List<DDAMs> msList = parser.readAllToMemory();
            ZstdDictTrainer trainer = new ZstdDictTrainer(100000000, 10000000);
            for (int i = 0; i < msList.size(); i++) {
                DDAMs ms = msList.get(i);
                Spectrum spectrum = ms.getSpectrum();
                double[] ints = spectrum.getInts();
                int[] intInts = ByteTrans.doubleToInt(ints, 1);
                byte[] bytes = ByteTrans.intToByte(bp.encode(intInts));
                trainer.addSample(bytes);
                byte[] encodeBytes = zstdWrapper.encode(bytes);
                finalLength+=encodeBytes.length;
            }

            System.out.println("最终压缩大小为："+finalLength/1000d/1000d+"MB");
//            if (zstdDictCompress == null){
            dict = trainer.trainSamples();
            System.out.println("Dict Size："+dict.length/1000d/1000d+"MB");
            zstdDictCompress = new ZstdDictCompress(dict, Zstd.defaultCompressionLevel());
//            }

            for (int i = 0; i < msList.size(); i++) {
                DDAMs ms = msList.get(i);
                Spectrum spectrum = ms.getSpectrum();
                double[] ints = spectrum.getInts();
                int[] intInts = ByteTrans.doubleToInt(ints, 1);
                byte[] bytes = ByteTrans.intToByte(bp.encode(intInts));
                byte[] oldBytes = zstdWrapper.encode(bytes);
                byte[] compBytes = new byte[bytes.length*10];
                long offset = Zstd.compress(compBytes, bytes, zstdDictCompress);
                byte[] finalBytes = Arrays.copyOfRange(compBytes, 0, (int)offset);
                long originSize = Zstd.decompressedSize(finalBytes,0, finalBytes.length);
                byte[] recover1 = new byte[(int)originSize];
                Zstd.decompress(recover1, finalBytes, dict);
//                System.out.println("length using dict:"+finalBytes.length);
                newFinalLength += offset;
            }
            System.out.println("使用字典后最终压缩大小为："+newFinalLength/1000d/1000d+"MB，压缩率提升"+ (finalLength-newFinalLength)*100d/finalLength+"%");
//            byte[] compressed = ComboComp.encode(new IntegratedBinPackingWrapper(), new ZstdWrapper(), mzs, 100000);
//            int[] decompMzs = ComboComp.decode(new IntegratedBinPackingWrapper(), new ZstdWrapper(), compressed);
//            System.out.println(decompMzs.length);
            System.out.println();
        }
    }
}
