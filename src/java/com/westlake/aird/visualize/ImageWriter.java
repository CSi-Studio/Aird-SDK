/*
 * Copyright (c) 2020 Propro Studio
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.visualize;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Arrays;

/**
 * 将单帧质谱信息写成图片工具
 */
public class ImageWriter {
    // 静态代码块定义，会在程序开始运行时先被调用初始化
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 得保证先执行该语句，用于加载库，才能调用其他操作库的语句，
    }

    private int mzIntRange; // m/z 整数部分的范围
    private int mzDecRange; // m/z 小数部分保留的精度

    public ImageWriter(int mzIntRange, int mzDecRange) {
        this.mzIntRange = mzIntRange;
        this.mzDecRange = mzDecRange;
    }

    /**
     * 无参构造方法：默认 m/z 从0到2000，保留小数点后3位
     */
    public ImageWriter() {
        this(2000, 1000);
    }

    /**
     * 将单帧m/z信息写入图片
     *
     * @param fileName  要写入图片的路径
     * @param mz        m/z的数组
     * @param intensity intensity的数组
     */
    public void write(String fileName, float[] mz, float[] intensity) {
        Mat mat = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC4);
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int) Math.floor(mz[i]);
            int decPart = Math.round((mz[i] - intPart) * mzDecRange);
            if (decPart >= mzDecRange) {
                intPart++;
                decPart = 0;
            }
            int value = (int) intensity[i];
            byte[] bytes = new byte[]{
                    (byte) (value),
                    (byte) (value >> 8),
                    (byte) (value >> 16),
                    (byte) (value >> 24),
            };
            mat.put(intPart, decPart, bytes);
        }
        Imgcodecs.imwrite(fileName, mat);
    }

    private byte[] float2bytes(float f){
        byte[] ret = new byte[4];
        int exp = (int)Math.ceil(Math.log10(f));
        ret[0] = (byte)exp;
        int base = (int)(f * Math.pow(10, 7 - exp));
        ret[1] = (byte)(base >> 16);
        ret[2] = (byte)(base >> 8);
        ret[3] = (byte)(base);
        return ret;
    }


    public void write2(String fileName, float[] mz, float[] intensity) {
        Mat mat1 = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC3);
        Mat mat2 = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC1);

        for (int i = 0; i < mz.length; i++) {
            int intPart = (int) Math.floor(mz[i]);
            int decPart = Math.round((mz[i] - intPart) * mzDecRange);
            if (decPart >= mzDecRange) {
                intPart++;
                decPart = 0;
            }
            byte[] bytes = float2bytes(intensity[i]);
            mat1.put(intPart, decPart, Arrays.copyOfRange(bytes,1,4));
            mat2.put(intPart, decPart, bytes[0]);
        }
        Imgcodecs.imwrite(fileName + "_base.png", mat1);
//        Imgcodecs.imwrite(fileName + "_exp.png", mat2);
    }


}
