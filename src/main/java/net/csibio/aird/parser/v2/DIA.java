package net.csibio.aird.parser.v2;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.exception.ScanException;
import net.csibio.aird.parser.BaseParser;

import java.util.List;
import java.util.TreeMap;

public class DIA extends BaseParser {

    public DIA(String indexFilePath) throws ScanException {
        super(indexFilePath);
    }

    public DIA(String airdPath, Compressor mzCompressor, Compressor intCompressor, int mzPrecision) throws ScanException {
        super(airdPath, mzCompressor, intCompressor, mzPrecision, AirdType.DIA_SWATH.getName());
    }

    public TreeMap<Float, Spectrum> getSpectrums(BlockIndex index) {
        return getSpectrums(index.getStartPtr(), index.getEndPtr(), index.getRts(), index.getMzs(), index.getInts());
    }

    /**
     * 返回值是一个map,其中key为rt,value为这个rt对应点原始谱图信息
     * 特别需要注意的是,本函数在使用完raf对象以后并不会直接关闭该对象,需要使用者在使用完DIAParser对象以后手动关闭该对象
     * <p>
     * the result key is rt,value is the spectrum(mz-intensity pairs)
     * In particular, this function will not close the RAF object directly after using it.
     * Users need to close the object manually after using the diaparser object
     *
     * @param startPtr    起始指针位置 start point
     * @param endPtr      结束指针位置 end point
     * @param rtList      rt列表,包含所有的光谱产出时刻 the retention time list
     * @param mzSizeList  mz块的大小列表 the mz block size list
     * @param intSizeList intensity块的大小列表 the intensity block size list
     * @return 每一个时刻对应的光谱信息 the spectrum of the target retention time
     */
    public TreeMap<Float, Spectrum> getSpectrums(long startPtr, long endPtr, List<Float> rtList, List<Long> mzSizeList, List<Long> intSizeList) {

        try {
            TreeMap<Float, Spectrum> map = new TreeMap<>();
            raf.seek(startPtr);
            long delta = endPtr - startPtr;
            byte[] result = new byte[(int) delta];
            raf.read(result);
            assert rtList.size() == mzSizeList.size();
            assert mzSizeList.size() == intSizeList.size();

            int start = 0;
            for (int i = 0; i < rtList.size(); i++) {
                float[] intensityArray = null;
                float[] mzArray = getMzValues(result, start, mzSizeList.get(i).intValue());
                start = start + mzSizeList.get(i).intValue();
                if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
                    intensityArray = getLogedIntValues(result, start, intSizeList.get(i).intValue());
                } else {
                    intensityArray = getIntValues(result, start, intSizeList.get(i).intValue());
                }
                start = start + intSizeList.get(i).intValue();
                map.put(rtList.get(i), new Spectrum(mzArray, intensityArray));
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从aird文件中获取某一条记录
     * 查询条件: 1.起始坐标 2.全rt列表 3.mz块体积列表 4.intensity块大小列表 5.rt
     * <p>
     * Read a spectrum from aird with multiple query criteria.
     * Query Criteria: 1.Start Point 2.rt list 3.mz block size list 4.intensity block size list 5.rt
     *
     * @param startPtr    起始位置 the start point of the target spectrum
     * @param rtList      全部时刻列表 all the retention time list
     * @param mzSizeList  mz数组长度列表 mz size block list
     * @param intSizeList int数组长度列表 intensity size block list
     * @param rt          获取某一个时刻原始谱图 the retention time of the target spectrum
     * @return 某个时刻的光谱信息 the spectrum of the target retention time
     */
    public Spectrum getSpectrumByRt(long startPtr, List<Float> rtList, List<Long> mzSizeList, List<Long> intSizeList, float rt) {
        int position = rtList.indexOf(rt);
        return getSpectrumByIndex(startPtr, mzSizeList, intSizeList, position);
    }

    /**
     * 根据序列号查询光谱
     *
     * @param index 索引序列号
     * @return 该索引号对应的光谱信息
     */
    public Spectrum getSpectrum(int index) {
        List<BlockIndex> indexList = getAirdInfo().getIndexList();
        for (int i = 0; i < indexList.size(); i++) {
            BlockIndex blockIndex = indexList.get(i);
            if (blockIndex.getNums().contains(index)) {
                int targetIndex = blockIndex.getNums().indexOf(index);
                return getSpectrumByIndex(blockIndex, targetIndex);
            }
        }
        return null;
    }

    /**
     * 从一个完整的Swath Block块中取出一条记录
     * 查询条件: 1. block索引号 2. rt
     * <p>
     * Read a spectrum from aird with block index and target rt
     *
     * @param index block index
     * @param rt    retention time of the target spectrum
     * @return the target spectrum
     */
    public Spectrum getSpectrumByRt(BlockIndex index, float rt) {
        List<Float> rts = index.getRts();
        int position = rts.indexOf(rt);
        return getSpectrumByIndex(index, position);
    }

    /**
     * @param blockIndex 块索引
     * @param index      块内索引值
     * @return 对应光谱数据
     */
    public Spectrum getSpectrumByIndex(BlockIndex blockIndex, int index) {
        return getSpectrumByIndex(blockIndex.getStartPtr(), blockIndex.getMzs(), blockIndex.getInts(), index);
    }

    /**
     * 从aird文件中获取某一条记录
     * 查询条件: 1.起始坐标 2.mz块体积列表 3.intensity块大小列表 4.光谱在块中的索引位置
     * <p>
     * Read a spectrum from aird with multiple query criteria.
     * Query Criteria: 1.Start Point 2.mz block size list 3.intensity block size list  4.spectrum index in the block
     *
     * @param startPtr    起始位置 the start point of the target spectrum
     * @param mzSizeList  mz数组长度列表 mz size block list
     * @param intSizeList int数组长度列表 intensity size block list
     * @param index       光谱在block块中的索引位置 the spectrum index in the block
     * @return 某个时刻的光谱信息 the spectrum of the target retention time
     */
    public Spectrum getSpectrumByIndex(long startPtr, List<Long> mzSizeList, List<Long> intSizeList, int index) {
        try {
            long start = startPtr;

            for (int i = 0; i < index; i++) {
                start += mzSizeList.get(i);
                start += intSizeList.get(i);
            }

            raf.seek(start);
            byte[] reader = new byte[mzSizeList.get(index).intValue()];
            raf.read(reader);
            float[] mzArray = getMzValues(reader);
            start += mzSizeList.get(index).intValue();
            raf.seek(start);
            reader = new byte[intSizeList.get(index).intValue()];
            raf.read(reader);

            float[] intensityArray = null;
            if (intCompressor.getMethods().contains(Compressor.METHOD_LOG10)) {
                intensityArray = getLogedIntValues(reader);
            } else {
                intensityArray = getIntValues(reader);
            }

            return new Spectrum(mzArray, intensityArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
