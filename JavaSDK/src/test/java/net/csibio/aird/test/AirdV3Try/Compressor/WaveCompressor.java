package net.csibio.aird.test.AirdV3Try.Compressor;

import com.alibaba.fastjson2.JSON;
import jwave.JWave;
import jwave.Transform;
import jwave.TransformBuilder;
import jwave.transforms.AncientEgyptianDecomposition;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.Wavelet;
import jwave.transforms.wavelets.daubechies.Daubechies20;
import jwave.transforms.wavelets.haar.Haar1;

public class WaveCompressor {
    public static double[] encode(double[] mz) {
        // 创建小波变换实例
        Transform transform =new Transform(
                new AncientEgyptianDecomposition(
                        new FastWaveletTransform(
                                new Daubechies20( ) ) ) );
        System.out.println(JSON.toJSON(mz));
        double[] arrFreqOrHilb = transform.forward(mz);

        return arrFreqOrHilb;
    }


}
