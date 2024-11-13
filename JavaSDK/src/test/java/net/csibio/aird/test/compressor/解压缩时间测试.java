package net.csibio.aird.test.compressor;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAPasefMs;
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.parser.DDAPasefParser;
import net.csibio.aird.parser.DIAParser;
import net.csibio.aird.parser.DIAPasefParser;
import net.csibio.aird.util.AirdScanUtil;
import org.junit.Test;

import java.io.File;
import java.util.List;


public class 解压缩时间测试 {

    @Test
    public void testComboComp() throws Exception {
        String path = "D:\\AirdTest\\ComboComp";
        List<File> files = AirdScanUtil.scanIndexFiles(path);
        for (File file : files) {
            if (file.getName().equals("File1.json") || file.getName().equals("File2.json") || file.getName().equals("File3.json") || file.getName().equals("File4.json") || file.getName().equals("File5.json") || file.getName().equals("File9.json")) {
                long start = System.currentTimeMillis();
                DDAParser parser = new DDAParser(file.getAbsolutePath());
                parser.readAllToMemory();
                System.out.println(file.getName() + ":" + (System.currentTimeMillis() - start));
            }

            if (file.getName().equals("File6.json") || file.getName().equals("File7.json") || file.getName().equals("File8.json")) {
                long start = System.currentTimeMillis();
                DIAParser parser = new DIAParser(file.getAbsolutePath());
                for (BlockIndex blockIndex : parser.airdInfo.getIndexList()) {
                    parser.getSpectra(blockIndex);
                }
                System.out.println(file.getName() + ":" + (System.currentTimeMillis() - start));
            }


        }
    }

    @Test
    public void testZDPD1() throws Exception {
        String path = "D:\\AirdTest\\ComboComp";
        List<File> files = AirdScanUtil.scanIndexFiles(path);
        for (File file : files) {
            if (file.getName().equals("File10.json")) {
                long start = System.currentTimeMillis();
                DDAPasefParser parser = new DDAPasefParser(file.getAbsolutePath());
                parser.readAllToMemory(true);
                System.out.println(file.getName() + ":" + (System.currentTimeMillis() - start));
            }
        }
    }

    @Test
    public void testDIAPasef() throws Exception {
//        String path = "D:\\AirdTest\\ComboComp";
        String path = "D:\\AirdTest\\ComboComp";
        List<File> files = AirdScanUtil.scanIndexFiles(path);
        for (File file : files) {
            if (file.getName().equals("File11.json")) {
                long start = System.currentTimeMillis();
                DIAPasefParser parser = new DIAPasefParser(file.getAbsolutePath());
                for (BlockIndex blockIndex : parser.airdInfo.getIndexList()) {
                    parser.getSpectra(blockIndex);
                }
                System.out.println(file.getName() + ":" + (System.currentTimeMillis() - start));
            }
        }
    }

    @Test
    public void testDDAPasef() throws Exception {
        String path = "D:\\AirdTest";
        List<File> files = AirdScanUtil.scanIndexFiles(path);
        for (File file : files) {
            if (file.getName().equals("File10.json")) {
                long start = System.currentTimeMillis();
                DDAPasefParser parser = new DDAPasefParser(file.getAbsolutePath());
                List<DDAPasefMs> msList = parser.readAllToMemory(true);
                System.out.println(file.getName() + ":" + (System.currentTimeMillis() - start));
            }
        }
    }
}
