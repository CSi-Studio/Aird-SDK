package com.westlake.aird;

import org.nd4j.jita.conf.CudaEnvironment;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.inverse.InvertMatrix;

public class Test2 {

    public static void main(String args[]) {
        CudaEnvironment.getInstance().getConfiguration().allowMultiGPU(true);
        int N = 10000;
        INDArray origin = Nd4j.rand(N, N);
        System.out.println("生成完毕");
        INDArray inverse = InvertMatrix.invert(origin, false);
        System.out.println("求逆完毕");
        INDArray unit = origin.mmul(inverse);
        System.out.println("unit" + unit);

    }
}
