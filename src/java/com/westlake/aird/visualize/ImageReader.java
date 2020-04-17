package com.westlake.aird.visualize;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.LinkedList;
import java.util.List;

/**
 * 读取图片中质谱信息的工具
 */
public class ImageReader {
    // 静态代码块定义，会在程序开始运行时先被调用初始化
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 得保证先执行该语句，用于加载库，才能调用其他操作库的语句，
    }
    private float[] mzArray; // m/z的数组
    private float[] intensityArray; // intensity的数组
    private int mzIntRange; // m/z 整数部分的范围
    private int mzDecRange; // m/z 小数部分保留的精度

    public ImageReader(int mzIntRange, int mzDecRange) {
        this.mzIntRange = mzIntRange;
        this.mzDecRange = mzDecRange;
    }

    /**
     * 无参构造方法：默认 m/z 从0到2000，保留小数点后3位
     */
    public ImageReader() {
        this(2000, 1000);
    }

    /**
     * 读取图片，将m/z信息存入mzArray，将intensity信息存入intensityArray
     * @param fileName
     */
    public void read(String fileName) {
        Mat mat = Imgcodecs.imread(fileName, Imgcodecs.IMREAD_LOAD_GDAL);
        List<Float> mzList = new LinkedList<>();
        List<Float> intList = new LinkedList<>();
        for (int i = 0; i < mzIntRange; i++) {
            for (int j = 0; j < mzDecRange; j++) {
                double[] values = mat.get(i, j);
                if(values[0]!=0 || values[1] !=0 || values[2] !=0 || values[3] !=0){
                    mzList.add(i + j /1000f);
                    int intensity = ((int) values[0]) +
                            ((int) values[1] << 8) +
                            ((int) values[2] << 16) +
                            ((int) values[3] << 24);
                    intList.add((float)intensity);
                }
            }
        }
        mzArray = new float[mzList.size()];
        intensityArray = new float[intList.size()];
        for (int i = 0; i < mzList.size(); i++) {
            mzArray[i] = mzList.get(i);
            intensityArray[i] = intList.get(i);
        }
    }
    /**
     * 外部获取mzArray方法
     * @return m/z数组
     */
    public float[] getMzArray() {
        return mzArray;
    }

    /**
     * 外部获取intensityArray方法
     * @return intensity数组
     */
    public float[] getIntensityArray() {
        return intensityArray;
    }

    private float bytes2float(byte[] bytes){
        int exp = bytes[0];
        int base = ((bytes[1] & 0xff) << 16) + ((bytes[2] &0xff) << 8) + (bytes[3] & 0xff);
        return (float)(base*Math.pow(10,exp - 7));
    }
}
