package com.westlake.aird;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import com.westlake.aird.util.CompressUtil;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class XzCompress {

    public static void main(String[] args) {
//        File indexFile = new File("E:\\data\\HYE124_5600_64_Var\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
//        File indexFile = new File("D:\\Propro\\projet\\data\\HYE110_TTOF6600_32fix_lgillet_I160308_001.json");
//        File indexFile = new File("D:\\Propro\\projet\\data\\C20181205yix_HCC_DIA_N_38A.json");
        File indexFile = new File("D:\\Propro\\projet\\data\\HYE124_TTOF5600_64var_lgillet_L150206_007.json");
//        File indexFile = new File("E:\\data\\SGSNew\\napedro_L120224_010_SW.json");
//        File indexFile = new File("E:\\metabolomics\\宣武医院 10-19 raw data\\NEG-Convert\\QXA01DNNEG20190627_DIAN1019VWHUMAN_HUMAN_PLASMA1_01.json");
        AtomicInteger rtCount = new AtomicInteger(0);

        AtomicInteger iter = new AtomicInteger(1);
        System.out.println(indexFile.getAbsolutePath());
        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
        swathIndexList.forEach(index -> {
            System.out.println("正在扫描:" + iter + "/" + swathIndexList.size());
            index.getRts().parallelStream().forEach(rt -> {
                //intensity -> zlib     mz -> fastpfor
                MzIntensityPairs pairs = airdParser.getSpectrum(index, rt);
                float[] intensity = pairs.getIntensityArray();
                byte[] compressed = xzCompress(transToByte(intensity), 1);
                float[] decompressed = transToFloat(xzDeCompress(compressed));
                System.out.println(String.format("%d -> %d", intensity.length, decompressed.length));

            });
            rtCount.addAndGet(index.getRts().size());
            iter.addAndGet(1);

        });
    }

    public static byte[] transToByte(float[] target) {
        FloatBuffer fbTarget = FloatBuffer.wrap(target);
        ByteBuffer bbTarget = ByteBuffer.allocate(fbTarget.capacity() * 4);
        bbTarget.asFloatBuffer().put(fbTarget);
        return bbTarget.array();
    }

    public static float[] transToFloat(byte[] target){
        ByteBuffer byteBuffer = ByteBuffer.wrap(target);
        FloatBuffer floats = byteBuffer.asFloatBuffer();
        float[] floatValues = new float[floats.capacity()];
        for (int i = 0; i < floats.capacity(); i++) {
            floatValues[i] = floats.get(i);
        }

        byteBuffer.clear();
        return floatValues;
    }

    public static byte[] xzCompress(byte[] target, int level)  {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            XZOutputStream xzOutputStream = new XZOutputStream(bos, new LZMA2Options(level));
            xzOutputStream.write(target);
        }catch (IOException e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public static byte[] xzDeCompress(byte[] target){
        ByteArrayInputStream bis = new ByteArrayInputStream(target);
        byte[] ret = null;
        try{
            XZInputStream xzInputStream = new XZInputStream(bis);
            ret = xzInputStream.readAllBytes();
        }catch (IOException e){
            e.printStackTrace();
        }

        return ret;
    }

}
