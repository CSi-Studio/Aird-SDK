//package net.csibio.aird.test.AirdV3Try.Compressor;
//
//import org.apache.commons.math3.linear.RealMatrix;
//import org.apache.commons.math3.linear.SparseRealMatrix;
//
//public class SparseMatrixCompressor {
//    public static Byte[] encodeRealMatrix(RealMatrix matrix) {
//        SparseRealMatrix sparseMatrix = encodeSparseMatrix(matrix);
//        return convertSparseMatrixToByteArray(sparseMatrix);
//    }
//
//    private static SparseRealMatrix encodeSparseMatrix(RealMatrix matrix) {
//        int numRows = matrix.getRowDimension();
//        int numCols = matrix.getColumnDimension();
//
//        // 创建稀疏矩阵
//        SparseRealMatrix sparseMatrix = new SparseRealMatrix(numRows, numCols);
//
//        // 填充稀疏矩阵
//        for (int i = 0; i < numRows; i++) {
//            for (int j = 0; j < numCols; j++) {
//                double value = matrix.getEntry(i, j);
//                if (value != 0) {
//                    sparseMatrix.setEntry(i, j, value);
//                }
//            }
//        }
//
//        return sparseMatrix;
//    }
//
//    private static Byte[] convertSparseMatrixToByteArray(SparseRealMatrix sparseMatrix) {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             DataOutputStream dos = new DataOutputStream(baos)) {
//            // 写入稀疏矩阵的行列数
//            dos.writeInt(sparseMatrix.getRowDimension());
//            dos.writeInt(sparseMatrix.getColumnDimension());
//
//            // 写入非零元素的坐标和值
//            for (int i = 0; i < sparseMatrix.getRowDimension(); i++) {
//                for (int j = 0; j < sparseMatrix.getColumnDimension(); j++) {
//                    if (sparseMatrix.getEntry(i, j) != 0) {
//                        dos.writeInt(i);
//                        dos.writeInt(j);
//                        dos.writeDouble(sparseMatrix.getEntry(i, j));
//                    }
//                }
//            }
//
//            return baos.toByteArray();
//        } catch (IOException e) {
//            throw new RuntimeException("Error converting sparse matrix to byte array", e);
//        }
//    }
//
//    public static RealMatrix retrieveRealMatrix(Byte[] data) {
//        SparseRealMatrix sparseMatrix = readSparseMatrixFromByteArray(data);
//        return sparseMatrix;
//    }
//
//}