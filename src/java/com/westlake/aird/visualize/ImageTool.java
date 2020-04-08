package com.westlake.aird.visualize;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

public class ImageTool {
    private static int mzIntRange = 2000;
    private static int mzDecRange = 1000;

    public static void scanToImage(String fileName, float[] mz, float[] intensity) {

        int[][] img = new int[mzIntRange][mzDecRange];
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int)Math.floor(mz[i]);
            int decPart = Math.round((mz[i]-intPart)*1000);
            if(decPart >= 1000){
                intPart ++;
                decPart = 0;
            }
            img[intPart][decPart] = Float.floatToIntBits(intensity[i]);
        }

        BufferedImage outImage = new BufferedImage(mzIntRange, mzDecRange, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < mzIntRange; i++) {
            for (int j = 0; j < mzDecRange; j++) {

                outImage.setRGB(i, j, img[i][j]);
            }
        }

        File file = new File(fileName);
        try {

            ImageIO.write(outImage, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float[][] imageToScan(String fileName){
        float[][] data = new float[2][];
        LinkedList<Float> mzList = new LinkedList<>();
        LinkedList<Float> intList = new LinkedList<>();
        File file = new File(fileName);
        try {
            BufferedImage inputImage = ImageIO.read(file);
            for (int i = 0; i < mzIntRange; i++) {
                for (int j = 0; j < mzDecRange; j++) {

                    int argb = inputImage.getRGB(i, j);
                    if(0 != argb){
                        mzList.add(i + j /1000f);
                        intList.add(Float.intBitsToFloat(argb));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        data[0] = new float[mzList.size()];
        data[1] = new float[intList.size()];

        for (int i = 0; i < mzList.size(); i++) {
            data[0][i] = mzList.get(i);
            data[1][i] = intList.get(i);
        }

        return data;
    }


    public static int intensityToRgb(float intensity){
//        double[] gamma = new double[]{0.43, 1.44, 0.12};
//        double[] gamma = new double[]{1, 1, 1};
        double[] gamma = new double[]{0.58, 1.31, 0.31};
        double x = Math.pow(intensity , 1.0/3);
        int r = (int)(255*Math.pow(x/255, gamma[0]));
        int g = (int)(255*Math.pow(x/255, gamma[1]));
        int b = (int)(255*Math.pow(x/255, gamma[2]));

        return  (r << 16) + (g << 8) + b;
    }

    public static int intensityToArgb(float intensity){
        return Float.floatToRawIntBits(intensity);
    }


}
