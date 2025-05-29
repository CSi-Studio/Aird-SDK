package net.csibio.aird.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jdk.incubator.vector.*;
import net.csibio.aird.bean.ColumnIndex;
import net.csibio.aird.bean.ColumnInfo;
import net.csibio.aird.bean.common.IntPair;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdMathUtil;
import net.csibio.aird.util.AirdScanUtil;

public class ColumnParser {

    /**
     * the aird file
     */
    public File airdFile;

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
        columnInfo = AirdScanUtil.loadColumnInfo(indexPath);
        if (columnInfo == null) {
            throw new ScanException(ResultCodeEnum.AIRD_COLUMN_INDEX_FILE_PARSE_ERROR);
        }
        this.airdFile = new File(AirdScanUtil.getAirdPathByIndexPath(indexPath));
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
        for (ColumnIndex columnIndex: indexList) {
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
                byte[] spectraIdBytes = readByte(columnIndex.getStartSpectraIdListPtr(), columnIndex.getEndSpectraIdListPtr());
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

    public Xic calcXicSIMDByMz(Double mz, Double mzWindow) throws IOException{
        return calcXicSIMD(mz - mzWindow, mz + mzWindow, null, null, null);
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
            for (ColumnIndex columnIndex: columnInfo.getIndexList()) {
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
        int anchorIndex = leftMzIndex / 100000;
        long startPtr = index.getAnchors()[anchorIndex];
        for (int i = anchorIndex * 100000; i < leftMzIndex; i++) {
            startPtr += spectraIdLengths[i];
            startPtr += intensityLengths[i];
        }
        List<Map<Integer, Double>> columnMapList = new ArrayList<>();
        TreeMap<Integer, Double> map = new TreeMap<>();  //key为spectraId，value为intensity
        for (int k = leftMzIndex; k <= rightMzIndex; k++) {
            byte[] spectraIdBytes = readByte(startPtr, spectraIdLengths[k]);
            startPtr += spectraIdLengths[k];
            byte[] intensityBytes = readByte(startPtr, intensityLengths[k]);
            startPtr += intensityLengths[k];
            int[] spectraIds = fastDecodeAsSortedInteger(spectraIdBytes);
            int[] ints = fastDecode(intensityBytes);
            for (int t = 0; t < spectraIds.length; t++) {
                int spectraId = spectraIds[t];
                if (spectraId >= leftRtIndex && spectraId <= rightRtIndex) {
                    double intensity = ints[t];
                    if (intensity < 0) {
                        intensity = Math.pow(2, -intensity / 100000d);
                    }
                    map.merge(spectraId, intensity / intPrecision, Double::sum);
                }
            }

            columnMapList.add(map);
        }
        int rtRange = rightRtIndex - leftRtIndex + 1;
        double[] intensities = new double[rtRange];
        double[] rts = new double[rtRange];
        int iteration = 0;
        for (int i = leftRtIndex; i <= rightRtIndex; i++) {
            intensities[iteration] = map.getOrDefault(i, 0d);
            rts[iteration] = index.getRts()[i] / 1000d;
            iteration++;
        }

        return new Xic(rts, intensities);
    }
    public Xic calcXicSIMD(Double mzStart, Double mzEnd, Double rtStart, Double rtEnd, Double precursorMz) throws IOException {
        if (columnInfo.getIndexList() == null || columnInfo.getIndexList().size() == 0) {
            return null;
        }
        ColumnIndex index = null;
        if (precursorMz != null) {
            for (ColumnIndex columnIndex: columnInfo.getIndexList()) {
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
        int anchorIndex = leftMzIndex / 100000;

        VectorSpecies<Integer> species = IntVector.SPECIES_PREFERRED;
        int vectorLength = species.length();

        long startPtr = index.getAnchors()[anchorIndex]; // Renamed startPtr to currentPtr to match existing code context

        int startIndex = anchorIndex * 100000;
        int endIndex = leftMzIndex;

        // SIMD part for accumulating lengths
        int i = startIndex;
        if (endIndex - startIndex >= vectorLength) { // Only use SIMD if there's at least one full vector to process
            // Create a zero vector for accumulation within a vector register if needed, but here we sum then add to scalar
            // Or, more simply, sum pairs and add to currentPtr iteratively for each vector

            for (; i <= endIndex - vectorLength; i += vectorLength) {
                IntVector idLengthsVec = IntVector.fromArray(species, spectraIdLengths, i);
                IntVector intensityLengthsVec = IntVector.fromArray(species, intensityLengths, i);

                // Add corresponding lengths together: idLength + intensityLength for each lane
                IntVector sumVec = idLengthsVec.add(intensityLengthsVec);

                // Sum all elements in sumVec (horizontal sum) and add to currentPtr
                // Vector API does not have a direct horizontal sum that returns a scalar easily for all types.
                // We can sum it lane by lane, or use reduceLanes.
                startPtr += sumVec.reduceLanes(VectorOperators.ADD);
            }
        }

        // Scalar part for remaining elements
        for (; i < endIndex; i++) {
            startPtr += spectraIdLengths[i];
            startPtr += intensityLengths[i];
        }

        List<Map<Integer, Double>> columnMapList = new ArrayList<>();
        TreeMap<Integer, Double> map = new TreeMap<>();  //key为spectraId，value为intensity

        // 获取SIMD向量规格
        VectorSpecies<Integer> intSpecies = IntVector.SPECIES_PREFERRED;
        VectorSpecies<Double> doubleSpecies = DoubleVector.SPECIES_PREFERRED;
        int intVectorLength = intSpecies.length();
        int doubleVectorLength = doubleSpecies.length();
        // 向量化处理 - 确保向量有足够的元素供应
        int maxVectorLength = Math.max(intVectorLength, doubleVectorLength);

        for (int k = leftMzIndex; k <= rightMzIndex; k++) {
            byte[] spectraIdBytes = readByte(startPtr, spectraIdLengths[k]);
            startPtr += spectraIdLengths[k];
            byte[] intensityBytes = readByte(startPtr, intensityLengths[k]);
            startPtr += intensityLengths[k];
            int[] spectraIds = fastDecodeAsSortedInteger(spectraIdBytes);
            int[] ints = fastDecode(intensityBytes);

            // SIMD优化的内层循环
            int t = 0;
            int dataLength = spectraIds.length;

            // 添加空数组检查，防止IndexOutOfBoundsException
            if (dataLength == 0) {
                columnMapList.add(map);
                continue;
            }


            // 只有当数据量足够时才使用SIMD处理
            if (dataLength >= maxVectorLength) {
                for (; t <= dataLength - maxVectorLength; t += maxVectorLength) {
                // 加载spectraId向量
                IntVector spectraIdVec = IntVector.fromArray(intSpecies, spectraIds, t);

                // 创建边界检查掩码
                VectorMask<Integer> leftMask = spectraIdVec.compare(VectorOperators.GE, leftRtIndex);
                VectorMask<Integer> rightMask = spectraIdVec.compare(VectorOperators.LE, rightRtIndex);
                VectorMask<Integer> rtMask = leftMask.and(rightMask);

                // 如果没有任何元素在范围内，跳过这个向量
                if (!rtMask.anyTrue()) {
                    continue;
                }

                // 准备强度数据进行向量化处理
                double[] tempIntensities = new double[doubleVectorLength];
                for (int lane = 0; lane < doubleVectorLength && t + lane < dataLength; lane++) {
                    tempIntensities[lane] = (double) ints[t + lane];
                }

                // 加载强度向量
                DoubleVector intensityVec = DoubleVector.fromArray(doubleSpecies, tempIntensities, 0);

                // 处理负值强度（指数转换）
                VectorMask<Double> negativeMask = intensityVec.compare(VectorOperators.LT, 0.0);
                if (negativeMask.anyTrue()) {
                    // 对负值进行指数转换: Math.pow(2, -intensity / 100000d)
                    DoubleVector negativeIntensities = intensityVec.div(-100000.0);
                    // 使用lanewise操作进行指数计算
                    DoubleVector expResult = DoubleVector.zero(doubleSpecies);
                    for (int lane = 0; lane < doubleVectorLength && t + lane < dataLength; lane++) {
                        if (negativeMask.laneIsSet(lane)) {
                            double expValue = Math.pow(2, negativeIntensities.lane(lane));
                            expResult = expResult.withLane(lane, expValue);
                        }
                    }
                    intensityVec = intensityVec.blend(expResult, negativeMask);
                }

                // 除以精度
                intensityVec = intensityVec.div(intPrecision);

                // 逐个处理向量中的元素（因为需要更新Map）
                for (int lane = 0; lane < doubleVectorLength && t + lane < dataLength; lane++) {
                    if (rtMask.laneIsSet(lane)) {
                        int spectraId = spectraIds[t + lane];
                        double intensity = intensityVec.lane(lane);
                        map.merge(spectraId, intensity, Double::sum);
                    }
                }
            }
                }

            // 处理剩余的标量元素
            for (; t < dataLength; t++) {
                int spectraId = spectraIds[t];
                if (spectraId >= leftRtIndex && spectraId <= rightRtIndex) {
                    double intensity = ints[t];
                    if (intensity < 0) {
                        intensity = Math.pow(2, -intensity / 100000d);
                    }
                    map.merge(spectraId, intensity / intPrecision, Double::sum);
                }
            }

            columnMapList.add(map);
        }
        int rtRange = rightRtIndex - leftRtIndex + 1;
        double[] intensities = new double[rtRange];
        double[] rts = new double[rtRange];
        int iteration = 0;
        for (int j = leftRtIndex; j <= rightRtIndex; j++) {
            intensities[iteration] = map.getOrDefault(j, 0d);
            rts[iteration] = index.getRts()[j] / 1000d;
            iteration++;
        }

        return new Xic(rts, intensities);
    }
    public int[] decodeAsSortedInteger(byte[] origin) {
        return new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(origin));
    }

    public int[] fastDecodeAsSortedInteger(byte[] origin) {
        return new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(origin));
    }

    public int[] decode(byte[] origin) {
        return new VarByteWrapper().decode(ByteTrans.byteToInt(origin));
    }

    public int[] fastDecode(byte[] origin) {
        return new VarByteWrapper().decode(ByteTrans.byteToInt(origin));
    }
}
