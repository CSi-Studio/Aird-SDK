package net.csibio.aird.util;

import java.util.HashMap;

public class DataUtil {

    /**
     * 从Aird中读取的Intensity数据采用了一种特殊的转换规则，需要通过本函数转换
     * @param originIntensities
     * @param intPrecision
     * @return
     */
    public static double[] parseIntensity(int[] originIntensities, double intPrecision){
        double[] intensities = new double[originIntensities.length];
        for (int i = 0; i < originIntensities.length; i++) {
            double intensity = originIntensities[i];
            if (intensity < 0) {
                intensity = Math.pow(2, -intensity / 100000d);
            }
            intensities[i] = intensity / intPrecision;
        }
        return intensities;
    }

    public static double fetchIntensity(int origin, double intPrecision){
        double intensity = origin;
        if (intensity < 0) {
            intensity = Math.pow(2, -intensity / 100000d);
        }
        return intensity / intPrecision;
    }
}
