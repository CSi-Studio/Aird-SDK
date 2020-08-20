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

import com.westlake.aird.api.DIAParser;
import com.westlake.aird.bean.BlockIndex;
import com.westlake.aird.bean.MzIntensityPairs;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import java.util.List;

public class VideoParser {
    // 声明opencv静态库
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private int mzIntRange; // m/z 整数部分的范围
    private int mzDecRange; // m/z 小数部分保留的精度
    private final String[] partName = {"_L", "_ML", "_MH", "_H"};

    public VideoParser(int mzIntRange, int mzDecRange) {
        this.mzIntRange = mzIntRange;
        this.mzDecRange = mzDecRange;
    }

    public VideoParser() {
        this(2000, 1000);
    }

    private Mat[] write(float[] mz, float[] intensity){
        Mat[] mats = new Mat[4];
        for (int i = 0; i < 4; i++) {
            mats[i] = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC1);
        }
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int)Math.floor(mz[i]);
            int decPart = Math.round((mz[i]-intPart)*mzDecRange);
            if(decPart >= mzDecRange){
                intPart++;
                decPart = 0;
            }
            int value = (int)intensity[i];
            for (int part = 0; part < 4; part++) {
                mats[part].put(intPart, decPart, (byte)(value>>(8*part)));
            }
        }
        return mats;
    }

    public void writeToVideo(String fileName, DIAParser airdParser, BlockIndex index) {
        VideoWriter[] videoWriters = new VideoWriter[4];
        for (int i = 0; i < 4; i++) {
            videoWriters[i] = new VideoWriter();
            videoWriters[i].open(fileName + partName[i] + ".mp4",
                    VideoWriter.fourcc('H', 'F', 'Y', 'U'),
                    30,
                    new Size(mzDecRange, mzIntRange),
                    false);
        }

        List<Float> rts = index.getRts();
        for (int i = 0; i < rts.size(); i++) {
            MzIntensityPairs pairs = airdParser.getSpectrum(index, rts.get(i));
            float[] mzArray = pairs.getMzArray();
            float[] intArray = pairs.getIntensityArray();
            Mat[] mats = write(mzArray, intArray);
            for (int part = 0; part < 4; part++) {
                videoWriters[part].write(mats[part]);
            }
            System.out.println("frame stored " + i);
        }
        for (int part = 0; part < 4; part++) {
            videoWriters[part].release();
        }
    }


    private void read(Mat mat) {

    }

    public void readVideo(String fileName){
        VideoCapture[] videoCaptures = new VideoCapture[4];
        for (int part = 0; part < 4; part++) {
            videoCaptures[part] = new VideoCapture();
            videoCaptures[part].open(fileName + partName[part] + ".mp4");
        }
        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open(fileName);
        System.out.println(videoCapture.isOpened());
        Mat mat = new Mat();
        if(videoCapture.read(mat)){
            read(mat);
        }
        else{
            System.out.println("读取失败");
        }
        videoCapture.release();
    }

    public void slipWindowVideo(String fileName, DIAParser airdParser, BlockIndex index){
        VideoWriter videoWriter = new VideoWriter();
        videoWriter.open(fileName,
                VideoWriter.fourcc('H', 'F', 'Y', 'U'),
                30,
                new Size(mzDecRange , mzIntRange),
                false);
        List<Float> rts = index.getRts();
//        Mat mat = Mat.zeros(mzIntRange * 2, mzDecRange * 2, CvType.CV_8UC1);
//        for (int i = 0; i < mzDecRange; i++) {
//            mat.put(2000, i, 255);
//        }
//        for (int i = 0; i < mzIntRange; i++) {
//            mat.put(i, mzDecRange, 255);
//        }
        for (int i = 0; i < rts.size(); i++) {
            MzIntensityPairs pairs = airdParser.getSpectrum(index, rts.get(i));
            float[] mzArray = pairs.getMzArray();
            float[] intArray = pairs.getIntensityArray();
//            Mat mat = slip(mzArray, intArray, i);
            Mat mat = writeGray(mzArray, intArray);
//            for (int len = 0; len < mzArray.length; len++) {
//                int intPart = (int) Math.floor(mzArray[len]);
//                int decPart = Math.round((mzArray[len] - intPart) * mzDecRange);
//                if (decPart >= mzDecRange) {
//                    intPart++;
//                    decPart = 0;
//                }
//                float value = intArray[len] / 10;
//
//                int intIndex = (intPart + i) % (mzIntRange * 2);
//                int decIndex = (decPart + i * mzDecRange / mzIntRange) % (mzDecRange * 2);
//                if(mat.get(intIndex, decIndex)[0] < value)
//                    mat.put(intIndex, decIndex, (int)value);
//            }
            videoWriter.write(mat);
            System.out.println("frame done " + i);
            if (i == rts.size()/2)
                Imgcodecs.imwrite(fileName.split("\\.")[0] + ".png", mat);

        }


        videoWriter.release();
    }

    private Mat slip(float[] mz, float[] intensity, int step){
        Mat mat = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC1);
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int)Math.floor(mz[i]);
            int decPart = Math.round((mz[i]-intPart)*mzDecRange);
            if(decPart >= mzDecRange){
                intPart++;
                decPart = 0;
            }
            int value = (int)intensity[i];
            mat.put((intPart + step)% mzIntRange, (decPart + step) % mzDecRange, value);

        }
        return mat;
    }

    private  Mat writeGray(float[] mz, float[] intensity){
//        Mat mat = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC1);
        Mat mat = indexedMat();
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int)Math.floor(mz[i]);
            int decPart = Math.round((mz[i]-intPart) * mzDecRange);
            if(decPart >= mzDecRange){
                intPart++;
                decPart = 0;
            }
            int value = (int)(8*(Math.log(intensity[i])/Math.log(2)));
            mat.put(intPart, decPart, 255);
            }
        return mat;
    }

    private Mat indexedMat(){
        Mat mat = Mat.zeros(mzIntRange, mzDecRange, CvType.CV_8UC1);
        for (int i = 100; i < mzIntRange; i+=100) {
            for (int j = 0; j < 10; j++) {
                mat.put(i, j, 255);
            }
        }
        for (int i = 100; i < mzDecRange; i+=100) {
            for (int j = 0; j < 10; j++) {
                mat.put(j, i, 255);
            }
        }
        for (int i = 0; i < mzIntRange; i++) {
            mat.put(i, 0, 255);
        }
        for (int i = 0; i < mzDecRange; i++) {
            mat.put(0, i, 255);
        }
        return mat;
    }

}
