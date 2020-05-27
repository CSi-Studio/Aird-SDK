package com.westlake.aird;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        int dimensions = 10000;
        double epsilon = 1E-6;
        double[][] a = initializeA(dimensions);
        double[] b = initializeB(dimensions);
        double[] x = initializeX(dimensions);
        double[] ax = matrixTimesVector(a, x);
        double[] r = vectorPlusVector(b, vectorTimesNum(ax, -1d));
        double[] p = Arrays.copyOf(r, r.length);
        double rho0 = innerProduct(r, r);
        int loopCount = 0;
        while (rho0 > epsilon) {
            //x
            double[] ap = matrixTimesVector(a, p);
            double alpha = rho0 / innerProduct(p, ap);
            x = vectorPlusVector(x, vectorTimesNum(p, alpha));

            //r
            r = vectorPlusVector(r, vectorTimesNum(ap, -alpha));
            double rho1 = innerProduct(r, r);

            double bk = rho1 / rho0;
            p = vectorPlusVector(r, vectorTimesNum(p, bk));
            rho0 = rho1;
            loopCount++;
            System.out.println(rho0 + " " + x[0]);
        }
        System.out.println("loops:" + loopCount);
        System.out.println("acc:" + rho0);
        System.out.println("x:" + Arrays.toString(x));
    }

    private static double[][] initializeA(int dimensions) {
        double[][] matrixA = new double[dimensions][dimensions];
        for (int i = 0; i < dimensions; i++) {
            for (int j = i; j < dimensions; j++) {
                double random = Math.random();
                matrixA[i][j] = random;
                matrixA[j][i] = random;
            }
        }
        return matrixA;
    }

    private static double[] initializeB(int dimensions) {
        double[] vectorB = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            vectorB[i] = Math.random();
        }
        return vectorB;
    }

    private static double[] initializeX(int dimensions) {
        double[] x = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            x[i] = 1;
        }
        return x;
    }

    private static double innerProduct(double[] vector1, double[] vector2) {
        double innerProduct = 0d;
        for (int i = 0; i < vector1.length; i++) {
            innerProduct += vector1[i] * vector2[i];
        }
        return innerProduct;
    }

    private static double[] matrixTimesVector(double[][] matrix, double[] vector) {
        double[] resultVector = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            resultVector[i] = innerProduct(matrix[i], vector);
        }
        return resultVector;
    }

    private static double[] vectorPlusVector(double[] vector1, double[] vector2) {
        double[] vector = new double[vector1.length];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector1[i] + vector2[i];
        }
        return vector;
    }

    private static double[] vectorTimesNum(double[] vector, double num) {
        double[] vectorResult = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            vectorResult[i] = vector[i] * num;
        }
        return vectorResult;
    }
}
