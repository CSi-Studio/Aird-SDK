using System;
using System.Collections.Generic;
using System.IO;
using AirdSDK.Beans;
using AirdSDK.Beans.Common;
using AirdSDK.Compressor;
using AirdSDK.Enums;
using AirdSDK.Exception;
using AirdSDK.Utils;
using CSharpFastPFOR.Port;

namespace AirdSDK.Parser;

public class ColumnParser
{
    /**
     * the aird file
     */
    public FileInfo airdFile;

    /**
     * the column index file. JSON format,with cjson
     */
    public FileInfo indexFile;

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
     * File reader
     */
    public FileStream fs;

    public ColumnParser(string indexPath)
    {
        indexFile = new FileInfo(indexPath);
        columnInfo = AirdScanUtil.loadColumnInfo(indexFile);
        if (columnInfo == null) throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);

        airdFile = new FileInfo(AirdScanUtil.getAirdPathByColumnIndexPath(indexPath));
        fs = File.OpenRead(airdFile.FullName);

        //获取mzPrecision
        mzPrecision = columnInfo.mzPrecision;
        intPrecision = columnInfo.intPrecision;
        parseColumnIndex();
    }


    public void parseColumnIndex()
    {
        List<ColumnIndex> indexList = columnInfo.indexList;
        foreach (var columnIndex in indexList)
        {
            if (columnIndex.mzs == null)
            {
                byte[] mzsByte = readByte(columnIndex.startMzListPtr, columnIndex.endMzListPtr);
                int[] mzsAsInt = decodeAsSortedInteger(mzsByte);
                columnIndex.mzs = mzsAsInt;
            }

            if (columnIndex.rts == null)
            {
                byte[] rtsByte = readByte(columnIndex.startRtListPtr, columnIndex.endRtListPtr);
                int[] rtsAsInt = decodeAsSortedInteger(rtsByte);
                columnIndex.rts = rtsAsInt;
            }

            if (columnIndex.spectraIds == null)
            {
                byte[] spectraIdBytes = readByte(columnIndex.startSpecrtaIdListPtr, columnIndex.endSpecrtaIdListPtr);
                int[] spectraIds = decode(spectraIdBytes);
                columnIndex.spectraIds = spectraIds;
            }

            if (columnIndex.intensities == null)
            {
                byte[] intensityBytes = readByte(columnIndex.startIntensityListPtr, columnIndex.endIntensityListPtr);
                int[] intensities = decode(intensityBytes);
                columnIndex.intensities = intensities;
            }
        }
    }

    public byte[] readByte(long startPtr, long endPtr)
    {
        int delta = (int)(endPtr - startPtr);
        fs.Seek(startPtr, SeekOrigin.Begin);
        byte[] result = new byte[delta];
        fs.Read(result, 0, delta);
        return result;
    }

    public byte[] readByte(long startPtr, int delta)
    {
        fs.Seek(startPtr, SeekOrigin.Begin);
        byte[] result = new byte[delta];
        fs.Read(result, 0, delta);
        return result;
    }

    public Xic calcXicByMz(double mz, double mzWindow)
    {
        return calcXic(mz - mzWindow, mz + mzWindow, null, null, null);
    }

    public Xic calcXicByWindow(double mz, double mzWindow, double? rt, double? rtWindow, double? precursorMz)
    {
        double? rtStart = null;
        double? rtEnd = null;
        if (rt != null && rtWindow != null)
        {
            rtStart = rt - rtWindow;
            rtEnd = rt + rtWindow;
        }
        return calcXic(mz - mzWindow, mz + mzWindow, rtStart, rtEnd, precursorMz);
    }

    public Xic calcXic(double mzStart, double mzEnd, double? rtStart, double? rtEnd, double? precursorMz)
    {
        if (columnInfo.indexList == null || columnInfo.indexList.Count == 0)
        {
            return null;
        }

        ColumnIndex index = null;
        if (precursorMz != null)
        {
            foreach (var columnIndex in columnInfo.indexList)
            {
                if (columnIndex.range != null && columnIndex.range.start <= precursorMz &&
                    columnIndex.range.end > precursorMz)
                {
                    index = columnIndex;
                }
            }
        }
        else
        {
            index = columnInfo.indexList[0];
        }

        if (index == null)
        {
            return null;
        }

        int[] mzs = index.mzs;
        int start = (int)(mzStart * mzPrecision);
        int end = (int)(mzEnd * mzPrecision);
        IntPair leftMzPair = AirdMathUtil.binarySearch(mzs, start);
        int leftMzIndex = leftMzPair.right();
        IntPair rightMzPair = AirdMathUtil.binarySearch(mzs, end);
        int rightMzIndex = rightMzPair.left();

        int leftRtIndex = 0;
        int rightRtIndex = index.rts.Length - 1;
        if (rtStart != null)
        {
            IntPair leftRtPair = AirdMathUtil.binarySearch(index.rts, (int)(rtStart * 1000));
            leftRtIndex = leftRtPair.right();
        }

        if (rtEnd != null)
        {
            IntPair rightRtPair = AirdMathUtil.binarySearch(index.rts, (int)(rtEnd * 1000));
            rightRtIndex = rightRtPair.left();
        }

        int[] spectraIdLengths = index.spectraIds;
        int[] intensityLengths = index.intensities;
        long startPtr = index.startPtr;
        for (int i = 0; i < leftMzIndex; i++)
        {
            startPtr += spectraIdLengths[i];
            startPtr += intensityLengths[i];
        }

        List<Dictionary<int, double>> columnMapList = new List<Dictionary<int, double>>();

        for (int k = leftMzIndex; k <= rightMzIndex; k++)
        {
            byte[] spectraIdBytes = readByte(startPtr, spectraIdLengths[k]);
            startPtr += spectraIdLengths[k];
            byte[] intensityBytes = readByte(startPtr, intensityLengths[k]);
            startPtr += intensityLengths[k];
            int[] spectraIds = decodeAsSortedInteger(spectraIdBytes);
            int[] ints = decode(intensityBytes);
            Dictionary<int, double> map = new Dictionary<int, double>();
            //解码intensity
            for (int t = 0; t < spectraIds.Length; t++)
            {
                if (spectraIds[t] >= leftRtIndex && spectraIds[t] <= rightRtIndex)
                {
                    double intensity = ints[t];
                    if (intensity < 0)
                    {
                        intensity = Math.Pow(2, -intensity / 100000d);
                    }

                    map[spectraIds[t]] = intensity / intPrecision;
                }
            }

            columnMapList.Add(map);
        }

        int rtRange = rightRtIndex - leftRtIndex + 1;
        double[] intensities = new double[rtRange];
        double[] rts = new double[rtRange];
        int iteration = 0;
        for (int i = leftRtIndex; i <= rightRtIndex; i++)
        {
            double intensity = 0;
            for (int j = 0; j < columnMapList.Count; j++)
            {
                if (columnMapList[j].ContainsKey(i))
                {
                    intensity += columnMapList[j][i];
                }
            }

            intensities[iteration] = intensity;
            rts[iteration] = index.rts[i] / 1000d;
            iteration++;
        }
        
        return new Xic(rts, intensities);
    }

    public int[] decodeAsSortedInteger(byte[] origin)
    {
        if (origin.Length > 16)
        {
            return new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(origin)));
        }
        else
        {
            return ByteTrans.byteToInt(origin);
        }
    }

    public int[] decode(byte[] origin)
    {
        if (origin.Length > 16)
        {
            return new VarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(origin)));
        }
        else
        {
            return ByteTrans.byteToInt(origin);
        }
    }
}