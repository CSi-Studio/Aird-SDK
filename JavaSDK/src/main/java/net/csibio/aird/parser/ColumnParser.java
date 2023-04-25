package net.csibio.aird.parser;

import net.csibio.aird.bean.ColumnIndex;
import net.csibio.aird.bean.ColumnInfo;
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
     * the column index file. JSON format,with cjson
     */
    public File indexFile;

    /**
     * the airdInfo from the index file.
     */
    public ColumnInfo columnInfo;

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
        columnInfo = AirdScanUtil.loadColumnInfo(indexFile);
        if (columnInfo == null) {
            throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
        }

        this.airdFile = new File(AirdScanUtil.getAirdPathByColumnIndexPath(indexPath));
        try {
            raf = new RandomAccessFile(airdFile, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ScanException(ResultCodeEnum.AIRD_FILE_PARSE_ERROR);
        }
        //获取mzPrecision
        mzPrecision = columnInfo.getMzPrecision();
        intPrecision = columnInfo.getIntPrecision();
        parseColumnIndex();
    }

    public void parseColumnIndex() throws IOException {
        List<ColumnIndex> indexList = columnInfo.getIndexList();
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

    public Xic calcXicByMz(Double mz, Double mzWindow) throws IOException {
        return calcXic(mz - mzWindow, mz + mzWindow, null, null, null);
    }

    public Xic calcXicByWindow(Double mz, Double mzWindow, Double rt, Double rtWindow, Double precursorMz) throws IOException {
        return calcXic(mz - mzWindow, mz + mzWindow, rt - rtWindow, rt + rtWindow, precursorMz);
    }

    public Xic calcXic(Double mzStart, Double mzEnd, Double rtStart, Double rtEnd, Double precursorMz) throws IOException {
        if (columnInfo.getIndexList() == null || columnInfo.getIndexList().size() == 0) {
            return null;
        }
        ColumnIndex index = null;
        if (precursorMz != null) {
            for (ColumnIndex columnIndex : columnInfo.getIndexList()) {
                if (columnIndex.getRange() != null && columnIndex.getRange().getStart() <= precursorMz && columnIndex.getRange().getEnd() > precursorMz) {
                    index = columnIndex;
                }
            }
        } else {
            index = columnInfo.getIndexList().get(0);
        }
        if (index == null) {
            return null;
        }

        int[] mzs = index.getMzs();
        int start = (int) (mzStart * mzPrecision);
        int end = (int) (mzEnd * mzPrecision);
        IntPair leftMzPair = AirdMathUtil.binarySearch(mzs, start);
        int leftMzIndex = leftMzPair.right();
        IntPair rightMzPair = AirdMathUtil.binarySearch(mzs, end);
        int rightMzIndex = rightMzPair.left();

        int leftRtIndex = 0;
        int rightRtIndex = index.getRts().length - 1;
        if (rtStart != null) {
            IntPair leftRtPair = AirdMathUtil.binarySearch(index.getRts(), (int) (rtStart * 1000));
            leftRtIndex = leftRtPair.right();
        }

        if (rtEnd != null) {
            IntPair rightRtPair = AirdMathUtil.binarySearch(index.getRts(), (int) (rtEnd * 1000));
            rightRtIndex = rightRtPair.left();
        }

        int[] spectraIdLengths = index.getSpectraIds();
        int[] intensityLengths = index.getIntensities();
        long startPtr = index.getStartPtr();
        for (int i = 0; i < leftMzIndex; i++) {
            startPtr += spectraIdLengths[i];
            startPtr += intensityLengths[i];
        }
        List<Map<Integer, Double>> columnMapList = new ArrayList<>();

        for (int k = leftMzIndex; k <= rightMzIndex; k++) {
            byte[] spectraIdBytes = readByte(startPtr, spectraIdLengths[k]);
            startPtr += spectraIdLengths[k];
            byte[] intensityBytes = readByte(startPtr, intensityLengths[k]);
            startPtr += intensityLengths[k];
            int[] spectraIds = decodeAsSortedInteger(spectraIdBytes);
            int[] ints = decode(intensityBytes);
            HashMap<Integer, Double> map = new HashMap<>();
            //解码intensity
            for (int t = 0; t < spectraIds.length; t++) {
                if (spectraIds[t] >= leftRtIndex && spectraIds[t] <= rightRtIndex) {
                    double intensity = ints[t];
                    if (intensity < 0) {
                        intensity = Math.pow(2, -intensity / 100000d);
                    }
                    map.put(spectraIds[t], intensity / intPrecision);
                }
            }

            columnMapList.add(map);
        }

        int rtRange = rightRtIndex - leftRtIndex + 1;
        double[] intensities = new double[rtRange];
        double[] rts = new double[rtRange];
        int iteration = 0;
        for (int i = leftRtIndex; i <= rightRtIndex; i++) {
            double intensity = 0;
            for (int j = 0; j < columnMapList.size(); j++) {
                if (columnMapList.get(j).containsKey(i)){
                    intensity += columnMapList.get(j).get(i);
                }
            }
            intensities[iteration] = intensity;
            rts[iteration] = index.getRts()[i] / 1000d;
            iteration++;
        }
        return new Xic(rts, intensities);
    }

    public int[] decodeAsSortedInteger(byte[] origin) {
        if (origin.length > 16) {
            return new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(origin)));
        } else {
            return ByteTrans.byteToInt(origin);
        }
    }

    public int[] decode(byte[] origin) {
        if (origin.length > 16) {
            return new VarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(origin)));
        } else {
            return ByteTrans.byteToInt(origin);
        }
    }
}
