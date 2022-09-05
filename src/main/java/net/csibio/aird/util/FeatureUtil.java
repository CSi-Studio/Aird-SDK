/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Feature Util for common string to map functions
 */
public class FeatureUtil {

    /**
     * sp-;
     */
    public static final String SP = ";";

    /**
     * ssp-:
     */
    public static final String SSP = ":";

    /**
     * 通过Map转换成String
     *
     * @param attrs features map to convert to string
     * @return the converted string
     */
    public static <T> String toString(Map<String, T> attrs) {
        StringBuilder sb = new StringBuilder();
        if (null != attrs && !attrs.isEmpty()) {
            Set<Map.Entry<String, T>> entrySet = attrs.entrySet();
            for (Map.Entry<String, T> entry : entrySet) {
                String key = entry.getKey();
                String val = entry.getValue().toString();
                sb.append(key).append(SSP).append(val).append(SP);
            }
        }
        return sb.toString();
    }

    /**
     * joint string with split string
     *
     * @param left  string A
     * @param right string B
     * @return the joint string
     */
    public static String toString(Double left, Double right) {
        return left.toString() + SP + right.toString();
    }

    /**
     * string to split to doubles
     *
     * @param range the string to be split
     * @return the split double array as Pair
     */
    public static Pair<Double, Double> toDoublePair(String range) {
        String[] arr = range.split(SP);
        Double left = Double.parseDouble(arr[0]);
        Double right = Double.parseDouble(arr[1]);
        return Pair.of(left, right);
    }

    /**
     * 通过字符串解析成attributes
     *
     * @param str (格式比如为: "k:v;k:v;k:v")
     * @return the string-double feature map
     */
    public static Map<String, Double> toDoubleMap(String str) {
        Map<String, Double> attrs = new HashMap<>();
        if (str != null) {
            String[] arr = str.split(SP);
            for (String kv : arr) {
                String[] ar = kv.split(SSP);
                if (ar.length == 2) {
                    String k = ar[0];
                    Double v = Double.parseDouble(ar[1]);
                    attrs.put(k, v);
                }
            }
        }
        return attrs;
    }


    /**
     * 通过字符串解析成attributes
     *
     * @param str (格式比如为: "k:v;k:v;k:v")
     * @return the string-float features map
     */
    public static final HashMap<String, Float> toFloatMap(String str) {
        HashMap<String, Float> attrs = new HashMap<>();
        if (str != null) {
            String[] arr = str.split(SP);
            for (String kv : arr) {
                String[] ar = kv.split(SSP);
                if (ar.length == 2) {
                    String k = ar[0];
                    Float v = Float.parseFloat(ar[1]);
                    attrs.put(k, v);
                }
            }
        }
        return attrs;
    }
}
