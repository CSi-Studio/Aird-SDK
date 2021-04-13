package net.csibio.aird;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.util.CompressUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import static net.csibio.aird.util.CompressUtil.fastPforEncoder;

public class testZlibDecoder {
    public static void main(String[] args) throws IOException, DataFormatException {
        String indexFilePath = "/Users/jinyinwang/Documents/stackTestData/DIA/HYE110_TTOF6600_32fix_lgillet_I160308_001.json";
        DIAParser DIAParser = new DIAParser(indexFilePath);
        List<BlockIndex> swathIndexList = DIAParser.getAirdInfo().getIndexList();
        int[] mz = DIAParser.getSpectrumAsInteger(swathIndexList.get(1), swathIndexList.get(1).getRts().get(1)).getMz();
        byte[] comArr = CompressUtil.transToByte(CompressUtil.fastPforEncoder(mz));
//        int layers = 256;
//        byte[] index = new byte[8000 * layers + (int) (Math.random() * 100)];
//
//        for (int j = 0; j < index.length; j++) {
//            index[j] = (byte) (Math.random() * layers);
//        }
//        byte[] comArr = CompressUtil.zlibEncoder(index);

        long t1 = System.currentTimeMillis();
//        CompressUtil.zlibDecoder(comArr);
        int[] decArr = CompressUtil.fastPforDecoder(CompressUtil.transToInteger(comArr));
        System.out.println((System.currentTimeMillis() - t1));
        boolean a = Arrays.equals(mz, decArr);
        System.out.println(a);
    }
}
