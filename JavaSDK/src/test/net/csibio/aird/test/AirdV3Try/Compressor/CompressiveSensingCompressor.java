package net.csibio.aird.test.AirdV3Try.Compressor;

import net.csibio.aird.bean.DDAMs;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class CompressiveSensingCompressor {
    public static RealMatrix encodeSpectrumList(List<DDAMs> spectrumList) {
        int numSpectra = spectrumList.size();
        int numFeatures = spectrumList.get(0).getSpectrum().getMzs().length;

        // 构建测量矩阵
        RealMatrix measurementMatrix = new Array2DRowRealMatrix(numSpectra, numFeatures);
        for (int i = 0; i < numSpectra; i++) {
            for (int j = 0; j < numFeatures; j++) {
                measurementMatrix.setEntry(i, j, spectrumList.get(i).getSpectrum().getInts()[j]);
            }
        }

        // 使用压缩感知编码
        SingularValueDecomposition svd = new SingularValueDecomposition(measurementMatrix);
        RealMatrix encodedData = svd.getU().getSubMatrix(0, numSpectra - 1, 0, numFeatures / 4 - 1);

        return encodedData;
    }

    public static byte[] compressRealMatrix(RealMatrix matrix) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DeflaterOutputStream dos = new DeflaterOutputStream(baos, new Deflater(Deflater.BEST_COMPRESSION))) {
            // 写入矩阵的行列数
            writeInt(dos, matrix.getRowDimension());
            writeInt(dos, matrix.getColumnDimension());

            // 写入矩阵元素
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                for (int j = 0; j < matrix.getColumnDimension(); j++) {
                    double value = matrix.getEntry(i, j);
                    if (Math.abs(value) > 1e-6) { // 过滤掉接近于0的元素
                        writeInt(dos, i);
                        writeInt(dos, j);
                        writeDouble(dos, value);
                    }
                }
            }

            dos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error compressing RealMatrix", e);
        }
    }

    private static void writeInt(DeflaterOutputStream dos, int value) throws IOException {
        dos.write(value);
    }

    private static void writeDouble(DeflaterOutputStream dos, double value) throws IOException {
//        dos.write(value);
    }

    private static int readInt(DataInputStream dis) throws IOException {
        return dis.readInt();
    }

}
