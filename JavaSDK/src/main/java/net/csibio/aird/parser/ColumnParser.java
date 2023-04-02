package net.csibio.aird.parser;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.ColumnIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Column;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.compressor.ByteTrans;
import net.csibio.aird.compressor.bytecomp.ZstdWrapper;
import net.csibio.aird.compressor.intcomp.VarByteWrapper;
import net.csibio.aird.compressor.sortedintcomp.IntegratedVarByteWrapper;
import net.csibio.aird.enums.ResultCodeEnum;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.util.AirdScanUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
                int[] mzsAsInt = new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(mzsByte)));
                columnIndex.setMzs(mzsAsInt);
            }
            if (columnIndex.getRts() == null) {
                byte[] rtsByte = readByte(columnIndex.getStartRtListPtr(), columnIndex.getEndRtListPtr());
                int[] rtsAsInt = new IntegratedVarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(rtsByte)));
                columnIndex.setRts(rtsAsInt);
            }
            if (columnIndex.getSpectraIds() == null) {
                byte[] spectraIdBytes = readByte(columnIndex.getStartSpecrtaIdListPtr(), columnIndex.getEndSpecrtaIdListPtr());
                int[] spectraIds = new VarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(spectraIdBytes)));
                columnIndex.setSpectraIds(spectraIds);
            }
            if (columnIndex.getIntensities() == null) {
                byte[] intensityBytes = readByte(columnIndex.getStartIntensityListPtr(), columnIndex.getEndIntensityListPtr());
                int[] intensities = new VarByteWrapper().decode(ByteTrans.byteToInt(new ZstdWrapper().decode(intensityBytes)));
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

    public Xic getColumns(double mzStart, double mzEnd) {
        int start = (int) (mzStart * mzPrecision);
        int end = (int) (mzEnd * mzPrecision);
        
        return null;
    }
}
