package com.westlake.aird.visualize;

import com.westlake.aird.api.AirdParser;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import tanling.matplot_4j.d3d.base.pub.Range;
import tanling.matplot_4j.d3d.base.pub.TopBottomColorStyle;
import tanling.matplot_4j.d3d.facade.Function;
import tanling.matplot_4j.d3d.facade.MatPlot3DMgr;
import tanling.matplot_4j.d3d.base.pub.Point3D;
import tanling.matplot_4j.d3d.facade.Processor;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Plot3DTest {

    public void plotFunc3dDemo(){
        MatPlot3DMgr mgr = new MatPlot3DMgr();

        mgr.setDataInputType(MatPlot3DMgr.DATA_TYPE_FUNCTION3D);
        mgr.setShowType(MatPlot3DMgr.SHOW_TYPE_DOTS);//设置点阵列显示

        Function f = new Function() {

            public double f(double x, double y) {

                return Math.sin(y) * Math.cos(x) * 0.8;
            }

        };

        mgr.addData(f, new Range(-6, 6), new Range(-6, 6), 50, 50,new TopBottomColorStyle(Color.GREEN.darker().darker(),Color.YELLOW.brighter()));//自定义高低颜色，动态时每一帧高底色可以不同

        mgr.setScaleZ(2);
        mgr.setScaleX(1.3);
        mgr.setScaleY(1.3);

        mgr.setSeeta(0.78);
        mgr.setBeita(1.0);

        mgr.setTitle("Demo : 函数点阵   [  z =  0.8 * cos(x) * sin(y) ]");
        mgr.getProcessor().setCoordinateSysShowType(mgr.getProcessor().COORDINATE_SYS_ALWAYS_FURTHER);

        mgr.show();
    }


    public void plotDotsDemo(){
        MatPlot3DMgr mgr = new MatPlot3DMgr();

        mgr.setDataInputType(MatPlot3DMgr.DATA_TYPE_CURVE2DS);
        mgr.setScatterPerspectiveType(true);

        //*************************************************************//
        //Add your data here

        Point3D p1 = new Point3D(0,0,0);
        Point3D p2 = new Point3D(1,1,1);
        Point3D p3 = new Point3D(2,2,2);
        Point3D p4 = new Point3D(3,3,3);

        ArrayList<Point3D> points = new ArrayList<>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);

        mgr.addData("rt", points);
        //.................

        mgr.setScaleX(1);
        mgr.setScaleY(1);
        mgr.setScaleZ(1);

        mgr.setSeeta(0.4);
        mgr.setBeita(5);

        mgr.setTitle("Demo : MNIST 数据集机器学习分类问题数据 3维 空间分布");

        mgr.getProcessor();
        mgr.getProcessor().setCoordinateSysShowType(mgr.getProcessor().COORDINATE_SYS_ALWAYS_FURTHER);

        mgr.show();
    }

    public void plotDots(float[] mzArray, float[] intArray) {
        MatPlot3DMgr mgr = new MatPlot3DMgr();

        mgr.setDataInputType(MatPlot3DMgr.DATA_TYPE_DOTS);
        mgr.setShowType(MatPlot3DMgr.SHOW_TYPE_CURVE2DS);
        mgr.setScatterPerspectiveType(true);

        //*************************************************************//
        //Add your data here

        ArrayList<Point3D> points = new ArrayList<>();

        for (int i = 0; i < mzArray.length; i++) {
            double intPart = Math.floor(mzArray[i]);
            double decPart = Math.round((mzArray[i] - intPart) * 1000);
            if (decPart >= 1000) {
                intPart++;
                decPart = 0;
            }
            Point3D p = new Point3D(intPart, decPart, intArray[i]);
            points.add(p);

        }


        mgr.addData("intensity", points);
        //.................

        mgr.setScaleX(1.5);
        mgr.setScaleY(2.0);
        mgr.setScaleZ(0.1);

        mgr.setSeeta(0.4);
        mgr.setBeita(5.0);

        mgr.getProcessor();
        mgr.getProcessor().setCoordinateSysShowType(mgr.getProcessor().COORDINATE_SYS_ALWAYS_FURTHER);

        mgr.show();


    }


    public static void main(String[] args) {

        String fileName =
                "HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
//                "C20181205yix_HCC_DIA_N_38A.json";
//                "HYE124_TTOF5600_64var_lgillet_L150206_007.json";
//                "napedro_L120224_010_SW.json";
//                "C20181208yix_HCC_DIA_T_46A_with_zero_lossless.json";
//                "D20181207yix_HCC_SW_T_46A_with_zero_lossless.json";
//                "HYE110_TTOF6600_32fix_lgillet_I160308_001_with_zero_lossless.json";

        String imgDir = "D:\\Propro\\projet\\images\\" + fileName.split("\\.")[0];
        File outDir = new File(imgDir);
        if (!outDir.exists() && !outDir.isDirectory()) {
            outDir.mkdir();
        }
        System.out.println(fileName.split("\\\\")[0]);
        String path = "D:\\Propro\\projet\\data\\";
        File indexFile = new File(path + fileName);

        AirdParser airdParser = new AirdParser(indexFile.getAbsolutePath());
        List<SwathIndex> swathIndexList = airdParser.getAirdInfo().getIndexList();
        SwathIndex index = swathIndexList.get(10);
        List<Float> rts = index.getRts();
        MzIntensityPairs pairs = airdParser.getSpectrum(index, rts.get(1000));
        float[] mzArray = pairs.getMzArray();
        float[] intArray = pairs.getIntensityArray();

//        new Plot3DTest().plotDots(mzArray, intArray);
//        new Plot3DTest().plotDotsDemo();
        new Plot3DTest().plotFunc3dDemo();
    }
}
