package com.westlake.aird.api;

import com.westlake.aird.bean.*;
import com.westlake.aird.enums.ResultCodeEnum;
import com.westlake.aird.exception.ScanException;
import com.westlake.aird.util.AirdScanUtil;
import com.westlake.aird.util.CompressUtil;
import com.westlake.aird.util.FileUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AirdParser {

    private File airdFile;
    private File indexFile;
    private AirdInfo airdInfo;
    private Compressor mzCompressor;
    private Compressor intCompressor;
    private int mzPrecision;

    public AirdParser(String indexFilePath) throws ScanException {
        this.indexFile = new File(indexFilePath);
        this.airdFile = new File(AirdScanUtil.getAirdFilePath(indexFilePath));
        airdInfo = AirdScanUtil.loadAirdInfo(indexFile);
        if (airdInfo == null) {
            throw new ScanException(ResultCodeEnum.AIRD_INDEX_FILE_PARSE_ERROR);
        }
        mzCompressor = CompressUtil.getMzCompressor(airdInfo.getCompressors());
        intCompressor = CompressUtil.getIntCompressor(airdInfo.getCompressors());
        mzPrecision = mzCompressor.getPrecision();
    }

    public AirdInfo getAirdInfo() {
        return airdInfo;
    }

    /**
     * the result key is rt,value is the spectrum
     * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息,原始谱图信息包含mz数组和intensity两个相同长度的数组
     *
     * @param index 需要解析的SWATH窗口
     * @return
     * @throws Exception
     */
    public TreeMap<Float, MzIntensityPairs> getSpectrums(BlockIndex index) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");
            TreeMap<Float, MzIntensityPairs> map = new TreeMap<Float, MzIntensityPairs>();
            List<Float> rts = index.getRts();

            raf.seek(index.getStartPtr());
            Long delta = index.getEndPtr() - index.getStartPtr();
            byte[] result = new byte[delta.intValue()];

            raf.read(result);
            List<Long> mzSizes = index.getMzs();
            List<Long> intensitySizes = index.getInts();

            int start = 0;
            for (int i = 0; i < mzSizes.size(); i++) {
                byte[] mz = ArrayUtils.subarray(result, start, start + mzSizes.get(i).intValue());
                start = start + mzSizes.get(i).intValue();
                byte[] intensity = ArrayUtils.subarray(result, start, start + intensitySizes.get(i).intValue());
                start = start + intensitySizes.get(i).intValue();
                try {

                    float[] intensityArray = null;
                    if (intCompressor.getMethod().contains(Compressor.METHOD_LOG10)) {
                        intensityArray = getLogedIntValues(intensity);
                    } else {
                        intensityArray = getIntValues(intensity);
                    }

                    MzIntensityPairs pairs = new MzIntensityPairs(getMzValues(mz), intensityArray);
                    map.put(rts.get(i), pairs);
                } catch (Exception e) {
                    System.out.println("index size error:" + i);
                }

            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.close(raf);
        }
        return null;
    }

    //根据特定BlockIndex取出对应TreeMap
    public TreeMap<Double, MzIntensityPairs> parseBlockValue(RandomAccessFile raf, BlockIndex blockIndex) throws Exception {
        TreeMap<Double, MzIntensityPairs> map = new TreeMap<>();
        List<Float> rts = blockIndex.getRts();

        raf.seek(blockIndex.getStartPtr());
        Long delta = blockIndex.getEndPtr() - blockIndex.getStartPtr();
        byte[] result = new byte[delta.intValue()];

        raf.read(result);
        List<Long> mzSizes = blockIndex.getMzs();
        List<Long> intensitySizes = blockIndex.getInts();

        int start = 0;
        for (int i = 0; i < mzSizes.size(); i++) {
            byte[] mz = ArrayUtils.subarray(result, start, start + mzSizes.get(i).intValue());
            start = start + mzSizes.get(i).intValue();
            byte[] intensity = ArrayUtils.subarray(result, start, start + intensitySizes.get(i).intValue());
            start = start + intensitySizes.get(i).intValue();
            try {
                MzIntensityPairs pairs = new MzIntensityPairs(getMzValues(mz), getIntValues(intensity));
                map.put(rts.get(i) / 60d, pairs);
            } catch (Exception e) {
                throw e;
            }
        }
        return map;
    }

    public List<MsCycle> parseToMsCycle() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(airdFile.getPath(), "r");
        List<MsCycle> cycleList = new ArrayList<>();
        List<BlockIndex> indexList = getAirdInfo().getIndexList();
        TreeMap<Double, MzIntensityPairs> ms1Map = parseBlockValue(raf, indexList.get(0));
        List<Integer> ms1ScanNumList = indexList.get(0).getNums();
        List<Double> rtList = new ArrayList<>(ms1Map.keySet());

        //将ms2 rt单位转换为分钟
        for (BlockIndex blockIndex : indexList) {
            List<Float> rts = blockIndex.getRts();
            for (int i = 0; i < rts.size(); i++) {
                rts.set(i, rts.get(i) / 60f);
            }
        }

        for (int i = 0; i < rtList.size(); i++) {
            MsCycle tempMsc = new MsCycle();
            //将ms1 rt单位转换为分钟
            tempMsc.setRt(rtList.get(i));
            tempMsc.setMs1Spectrum(ms1Map.get(rtList.get(i)));
            for (int tempBlockNum = 1; tempBlockNum < indexList.size(); tempBlockNum++) {
                BlockIndex tempBlockIndex = indexList.get(tempBlockNum);
                if (tempBlockIndex.getNum().equals(ms1ScanNumList.get(i))) {
                    tempMsc.setRangeList(tempBlockIndex.getRangeList());
                    tempMsc.setRts(tempBlockIndex.getRts());

                    TreeMap<Double, MzIntensityPairs> ms2Map = parseBlockValue(raf, tempBlockIndex);
                    List<MzIntensityPairs> ms2Spectrums = new ArrayList<>(ms2Map.values());
                    tempMsc.setMs2Spectrums(ms2Spectrums);
                    break;
                }
            }
            cycleList.add(tempMsc);
        }
        FileUtil.close(raf);
        return cycleList;
    }

    /**
     * 从aird文件中获取某一条记录
     * 从一个完整的Swath Block块中取出一条记录
     *
     * @param index
     * @param rt 获取某一个时刻原始谱图
     * @return
     */
    public MzIntensityPairs getSpectrum(BlockIndex index, float rt) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");
            List<Float> rts = index.getRts();
            int position = rts.indexOf(rt);

            long start = index.getStartPtr();

            for (int i = 0; i < position; i++) {
                start += index.getMzs().get(i);
                start += index.getInts().get(i);
            }

            raf.seek(start);
            byte[] reader = new byte[index.getMzs().get(position).intValue()];
            raf.read(reader);
            float[] mzArray = getMzValues(reader);
            start += index.getMzs().get(position).intValue();
            raf.seek(start);
            reader = new byte[index.getInts().get(position).intValue()];
            raf.read(reader);

            float[] intensityArray = null;
            if (intCompressor.getMethod().contains(Compressor.METHOD_LOG10)) {
                intensityArray = getLogedIntValues(reader);
            } else {
                intensityArray = getIntValues(reader);
            }

            return new MzIntensityPairs(mzArray, intensityArray);

        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.close(raf);
        }

        return null;
    }

    public MzIntensityPairs getSpectrumAsInteger(BlockIndex index, float rt) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(airdFile, "r");
            List<Float> rts = index.getRts();
            int position = rts.indexOf(rt);

            long start = index.getStartPtr();

            for (int i = 0; i < position; i++) {
                start += index.getMzs().get(i);
                start += index.getInts().get(i);
            }

            raf.seek(start);
            byte[] reader = new byte[index.getMzs().get(position).intValue()];
            raf.read(reader);
            int[] mzArray = getMzValuesAsInteger(reader);
            start += index.getMzs().get(position).intValue();
            raf.seek(start);
            reader = new byte[index.getInts().get(position).intValue()];
            raf.read(reader);

            float[] intensityArray = null;
            if (intCompressor.getMethod().contains(Compressor.METHOD_LOG10)) {
                intensityArray = getLogedIntValues(reader);
            } else {
                intensityArray = getIntValues(reader);
            }

            return new MzIntensityPairs(mzArray, intensityArray);

        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.close(raf);
        }

        return null;
    }

    /**
     * get mz values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN,精度为小数点后三位
     *
     * @param value
     * @return
     */
    private float[] getMzValues(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(mzCompressor.getByteOrder());

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }
        intValues = CompressUtil.fastPForDecoder(intValues);
        float[] floatValues = new float[intValues.length];
        for (int index = 0; index < intValues.length; index++) {
            floatValues[index] = (float) intValues[index] / mzPrecision;
        }
        byteBuffer.clear();
        return floatValues;
    }

    /**
     * get mz values only for aird file
     * 默认从Aird文件中读取,编码Order为LITTLE_ENDIAN
     *
     * @param value
     * @return
     */
    private int[] getMzValuesAsInteger(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(mzCompressor.getByteOrder());

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }
        intValues = CompressUtil.fastPForDecoder(intValues);
        byteBuffer.clear();
        return intValues;
    }

    /**
     * get mz values only for aird file
     *
     * @param value
     * @return
     */
    private float[] getIntValues(byte[] value) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(intCompressor.getByteOrder());

        FloatBuffer intensities = byteBuffer.asFloatBuffer();
        float[] intensityValues = new float[intensities.capacity()];
        for (int i = 0; i < intensities.capacity(); i++) {
            intensityValues[i] = intensities.get(i);
        }

        byteBuffer.clear();
        return intensityValues;
    }

    /**
     * get mz values only for aird file
     *
     * @param value
     * @return
     */
    private float[] getLogedIntValues(byte[] value) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(intCompressor.getByteOrder());

        FloatBuffer intensities = byteBuffer.asFloatBuffer();
        float[] intValues = new float[intensities.capacity()];
        for (int i = 0; i < intensities.capacity(); i++) {
            intValues[i] = (float) Math.pow(10, intensities.get(i));
        }

        byteBuffer.clear();
        return intValues;
    }
}
