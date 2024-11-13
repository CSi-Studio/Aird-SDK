package net.csibio.aird.test.airdslice;

import net.csibio.aird.parser.BaseParser;
import net.csibio.aird.parser.ColumnParser;
import net.csibio.aird.parser.DDAParser;
import org.junit.Test;

import java.io.IOException;

public class ReadProtoIndex {

  public static String cindexPath = "D:\\Aird2.0\\32.cindex";
  public static String cjsonIndexPath2 = "D:\\Aird2.0\\32.cjson";
  public static String jsonPath = "D:\\Aird2.0\\33.json";
  public static String indexPath = "D:\\Aird2.0\\33.index";
  @Test
  public void readProtoIndex() throws Exception {
    ColumnParser columnParser = new ColumnParser(cindexPath);
    ColumnParser columnParser2 = new ColumnParser(cjsonIndexPath2);
    long start = System.currentTimeMillis();
    BaseParser parser = new DDAParser(jsonPath);
    System.out.println("JSON Parse 时间："+(System.currentTimeMillis() - start));
    start = System.currentTimeMillis();
    BaseParser parser2 = new DDAParser(indexPath);
    System.out.println("Protobuf Parse 时间："+(System.currentTimeMillis() - start));
  }
}
