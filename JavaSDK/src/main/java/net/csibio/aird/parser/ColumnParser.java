package net.csibio.aird.parser;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.ColumnIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Column;
import net.csibio.aird.bean.common.Columns;
import net.csibio.aird.bean.common.IntPair;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdMathUtil;
import net.csibio.aird.util.AirdScanUtil;
import org.ejml.data.*;
import org.ejml.ops.CommonOps_BDRM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class ColumnParser {

    /**
     * the aird file
     */
    public File airdFile;

    /**
     * the aird index file. JSON format
     */
    public File indexFile;

    /**
     * the airdInfo from the index file.
     */
    public AirdInfo airdInfo;

    /**
     * mz precision
     */
    public double mzPrecision;

    /**
     * intensity precision
     */
    public double intPrecision;

    /**
     * Random Access File reader
     */
    public RandomAccessFile raf;

    public ColumnParser(String indexPath) throws IOException {
        this.indexFile = new File(indexPath);
        airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
        if (airdInfo == null) {
            throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
        }

        this.airdFile = new File(AirdScanUtil.getAirdPathByIndexPath(indexPath));
        try {
            raf = new RandomAccessFile(airdFile, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
        }
        //获取mzPrecision
        for (Compressor compressor : airdInfo.getCompressors()) {
            if (compressor.getTarget().equals(Compressor.TARGET_MZ)) {
                mzPrecision = compressor.getPrecision();
            }
            if (compressor.getTarget().equals(Compressor.TARGET_INTENSITY)) {
                intPrecision = compressor.getPrecision();
            }
        }

        parseColumnIndex();
    }

    public void parseColumnIndex() throws IOException {
        List<ColumnIndex> indexList = airdInfo.getColumnIndexList();
        for (ColumnIndex columnIndex : indexList) {
            if (columnIndex.getMzs() == null) {
                byte[] mzsByte = readByte(columnIndex.getStartMzListPtr(), columnIndex.getEndMzListPtr());
                int[] mzsAsInt = decodeAsSortedInteger(mzsByte);
                columnIndex.setMzs(mzsAsInt);
            }
            if (columnIndex.getRts() == null) {
                byte[] rtsByte = readByte(columnIndex.getStartRtListPtr(), columnIndex.getEndRtListPtr());
                int[] rtsAsInt = decodeAsSortedInteger(rtsByte);
                columnIndex.setRts(rtsAsInt);
            }
            if (columnIndex.getSpectraIds() == null) {
                byte[] spectraIdBytes = readByte(columnIndex.getStartSpecrtaIdListPtr(), columnIndex.getEndSpecrtaIdListPtr());
                int[] spectraIds = decode(spectraIdBytes);
                columnIndex.setSpectraIds(spectraIds);
            }
            if (columnIndex.getIntensities() == null) {
                byte[] intensityBytes = readByte(columnIndex.getStartIntensityListPtr(), columnIndex.getEndIntensityListPtr());
                int[] intensities = decode(intensityBytes);
                columnIndex.setIntensities(intensities);
            }
        }
    }

    public byte[] readByte(long startPtr, long endPtr) throws IOException {
        int delta = (int) (endPtr - startPtr);
        raf.seek(startPtr);
        byte[] result = new byte[delta];
        raf.read(result);
        return result;
    }

    public byte[] readByte(long startPtr, int delta) throws IOException {
        raf.seek(startPtr);
        byte[] result = new byte[delta];
        raf.read(result);
        return result;
    }

    public Xic getColumns(double mzStart, double mzEnd, Double precursorMz) throws IOException {
        if (airdInfo.getColumnIndexList() == null || airdInfo.getColumnIndexList().size() == 0) {
            return null;
        }
        ColumnIndex index = null;
        if (precursorMz != null) {
            for (ColumnIndex columnIndex : airdInfo.getColumnIndexList()) {
                if (columnIndex.getRange() != null && columnIndex.getRange().getStart() <= precursorMz && columnIndex.getRange().getEnd() > precursorMz) {
                    index = columnIndex;
                }
            }
        } else {
            index = airdInfo.getColumnIndexList().get(0);
        }
        if (index == null) {
            return null;
        }

        int[] mzs = index.getMzs();
        int start = (int) (mzStart * mzPrecision);
        int end = (int) (mzEnd * mzPrecision);
        IntPair leftPair = AirdMathUtil.binarySearch(mzs, start);
        int leftIndex = leftPair.right();
        IntPair rightPair = AirdMathUtil.binarySearch(mzs, end);
        int rightIndex = rightPair.left();

        int[] spectraIdLengths = index.getSpectraIds();
        int[] intensityLengths = index.getIntensities();
        long startPtr = index.getStartPtr();
        for (int i = 0; i < leftIndex; i++) {
            startPtr += spectraIdLengths[i];
            startPtr += intensityLengths[i];
        }
        List<Map<Integer, Double>> columnMapList = new ArrayList<>();
//        DMatrixSparseCSC matrix = new DMatrixSparseCSC(index.getRts().length, rightIndex - leftIndex + 1);
//        double[] columnOne = new double[index.getRts().length];
//        DMatrixRMaj matrixColumn = new DMatrixRMaj(columnOne);

        for (int k = leftIndex; k <= rightIndex; k++) {
            byte[] spectraIdBytes = readByte(startPtr, spectraIdLengths[k]);
            startPtr += spectraIdLengths[k];
            byte[] intensityBytes = readByte(startPtr, intensityLengths[k]);
            startPtr += intensityLengths[k];
            int[] spectraIds = decodeAsSortedInteger(spectraIdBytes);
            int[] ints = decode(intensityBytes);
            double[] intensities = new double[ints.length];
            HashMap<Integer, Double> map = new HashMap<>();
            for (int i = 0; i < ints.length; i++) {
                double intensity = ints[i];
                if (intensity < 0) {
                    intensity = Math.pow(2, -intensity / 100000d);
                }
                intensities[i] = intensity / intPrecision;
//                matrix.set(spectraIds[i], k - leftIndex, intensity);
                map.put(spectraIds[i], intensity);
            }

            columnMapList.add(map);
        }

        Xic xic = new Xic();
        double[] intensities = new double[index.getRts().length];
        double[] rts = new double[index.getRts().length];
        boolean startCount = false;
        int iter = 0;
        for (int i = 0; i < index.getRts().length; i++) {
            double intensity = 0;
            for (int j = 0; j < columnMapList.size(); j++) {
                intensity += columnMapList.get(j).getOrDefault(i,0d);
            }
            if (intensity != 0){
                startCount = true;
            }

            if (startCount){
                intensities[iter] = intensity;
                rts[iter] = index.getRts()[i]/1000d;
                iter++;
            }
        }

        return new Xic(rts, intensities);
    }

    public int[] decodeAsSortedInteger(byte[] origin) {
        return new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(origin)));
    }

    public int[] decode(byte[] origin) {
        return new VarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(origin)));
    }
}
