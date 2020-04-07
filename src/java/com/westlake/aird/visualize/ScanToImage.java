package com.westlake.aird.visualize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScanToImage {

    public static void transform(String fileName, float[] mz, float[] intensity) {
        int mzIntRange = 2000;
        int mzDecRange = 1000;
        int[][] img = new int[mzIntRange][mzDecRange];
        for (int i = 0; i < mz.length; i++) {
            int intPart = (int)Math.floor(mz[i]);
            int decPart = (int)((mz[i]-intPart)*1000);

//            img[intPart][decPart] = intensityToRgb(intensity[i]);
            img[intPart][decPart] = (int)Math.ceil(intensity[i]);
        }

        BufferedImage outImage = new BufferedImage(mzIntRange, mzDecRange, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < mzIntRange; i++) {
            for (int j = 0; j < mzDecRange; j++) {

                outImage.setRGB(i, j, img[i][j]);
            }
        }

        File file = new File(fileName);
        try {
            ImageIO.write(outImage, "jpg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void intensityToRgbTest() {
        String dir = "C:\\Users\\zhang\\Documents\\Propro\\projet\\images";
        String fileName = "test-0,58-1,31-0,31.jpg";

        int width = 2000;
        int height = 1000;

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for(int i= 0 ; i < width ; i++){
            for(int j = 0 ; j < height; j++){

                int rgb = intensityToRgb((float) (i/2000.0*Math.pow(255, 3)));
                grayImage.setRGB(i, j, rgb);
            }
        }

        File file = new File(dir + "\\" + fileName);
        try {
            ImageIO.write(grayImage, "jpg", file);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        intensityToRgbTest();
    }
}
