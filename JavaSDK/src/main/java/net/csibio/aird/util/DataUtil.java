package net.csibio.aird.util;

import java.util.HashMap;

public class DataUtil {

    public static double[] getIntensity(int[] originIntensities, double intPrecision){
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
}
