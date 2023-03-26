package net.csibio.aird.util;

public class PrecisionUtil {

    public static double trunc(double origin, double dp){
        return Math.round(origin*dp)/dp;
    }
}
