package com.westlake.aird.api;

import com.westlake.aird.bean.AirdInfo;
import com.westlake.aird.bean.Compressor;
import com.westlake.aird.bean.MzIntensityPairs;
import com.westlake.aird.bean.SwathIndex;
import com.westlake.aird.enums.ResultCodeEnum;
import com.westlake.aird.exception.ScanException;
import com.westlake.aird.util.AirdScanUtil;
import com.westlake.aird.util.CompressUtil;
import com.westlake.aird.util.FileUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.TreeMap;

public class AirdParser {

    private File airdFile;
    private File airdIndexFile;
    private AirdInfo airdInfo;
    private Compressor mzCompressor;
    private Compressor intCompressor;
    private int mzPrecision;

    public AirdParser(String airdIndexFilePath) throws ScanException{
        this.airdIndexFile = new File(airdIndexFilePath);
        this.airdFile = new File(AirdScanUtil.getAirdFilePath(airdIndexFilePath));
        airdInfo = AirdScanUtil.loadAirdInfo(airdIndexFile);
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
    public TreeMap<Float, MzIntensityPairs> getSpectrums(SwathIndex index) {
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

                    Float[] intensityArray = null;
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

    /**
     * 从aird文件中获取某一条记录
     * 从一个完整的Swath Block块中取出一条记录
     *
     * @param index
     * @param rt    获取某一个时刻原始谱图
     * @return
     */
    public MzIntensityPairs getSpectrum(SwathIndex index, float rt) {
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
            Float[] mzArray = getMzValues(reader);
            start += index.getMzs().get(position).intValue();
            raf.seek(start);
            reader = new byte[index.getInts().get(position).intValue()];
            raf.read(reader);

            Float[] intensityArray = null;
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
    private Float[] getMzValues(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
        byteBuffer.order(mzCompressor.getByteOrder());

        IntBuffer ints = byteBuffer.asIntBuffer();
        int[] intValues = new int[ints.capacity()];
        for (int i = 0; i < ints.capacity(); i++) {
            intValues[i] = ints.get(i);
        }
        intValues = CompressUtil.fastPForDecoder(intValues);
        Float[] floatValues = new Float[intValues.length];
        for (int index = 0; index < intValues.length; index++) {
            floatValues[index] = (float) intValues[index] / mzPrecision;
        }
        byteBuffer.clear();
        return floatValues;
    }

    /**
     * get mz values only for aird file
     *
     * @param value
     * @return
     */
    private Float[] getIntValues(byte[] value) throws Exception {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
            byteBuffer.order(intCompressor.getByteOrder());

            FloatBuffer intensities = byteBuffer.asFloatBuffer();
            Float[] intValues = new Float[intensities.capacity()];
            for (int i = 0; i < intensities.capacity(); i++) {
                intValues[i] = intensities.get(i);
            }

            byteBuffer.clear();
            return intValues;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * get mz values only for aird file
     *
     * @param value
     * @return
     */
    private Float[] getLogedIntValues(byte[] value) throws Exception {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(CompressUtil.zlibDecoder(value));
            byteBuffer.order(intCompressor.getByteOrder());

            FloatBuffer intensities = byteBuffer.asFloatBuffer();
            Float[] intValues = new Float[intensities.capacity()];
            for (int i = 0; i < intensities.capacity(); i++) {
                intValues[i] = (float) Math.pow(10, intensities.get(i));
            }

            byteBuffer.clear();
            return intValues;
        } catch (Exception e) {
            throw e;
        }

    }
}
