package com.westlake.aird.visualize;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;

import java.util.List;

/**
 * 将单帧质谱信息写成图片工具
 */
public class ImageWriter {
    // 声明opencv静态库
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
        this(2000,1000);
    }

    /**
     * 将单帧m/z信息写入图片
     * @param fileName 要写入图片的路径
     * @param mz m/z的数组
     * @param intensity intensity的数组
     */
    public void write(String fileName, float[] mz, float[] intensity){
        Mat mat = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC4);
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int)Math.floor(mz[i]);
            int decPart = Math.round((mz[i]-intPart)*mzDecRange);
            if(decPart >= mzDecRange){
                intPart++;
                decPart = 0;
            }
            int value = (int)intensity[i];
            byte[] bytes = new byte[]{
                    (byte)(value),
                    (byte)(value>>8),
                    (byte)(value>>16),
                    (byte)(value>>24),
            };
            mat.put(intPart, decPart, bytes);
        }
        Imgcodecs.imwrite(fileName, mat);
    }

}
