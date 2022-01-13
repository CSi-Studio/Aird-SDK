package net.csibio.aird.test.compare;

import com.csvreader.CsvWriter;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class CsvUtil {

  public static void main(String[] args) {
    String rawFile = "C:\\Users\\admin\\Desktop\\ADAP_raw.csv";
    LinkedList<Peak> rawData = CsvUtil.readPeaks(rawFile);
    String filePath = "C:\\Users\\admin\\Desktop\\test.csv";
    String[] headers = {"rt", "mz", "area"};
    writeCSV(rawData, filePath, headers);
  }

  public static LinkedList<Peak> readPeaks(String filePath) {
    CSVReader reader = null;
    LinkedList<Peak> peaks = new LinkedList<>();
    try {
      reader = new CSVReader(new FileReader(filePath));
      String[] line;
      while ((line = reader.readNext()) != null) {
        Peak temp = new Peak();
        temp.rt = Float.parseFloat(line[8]);
        temp.mz = Float.parseFloat(line[11]);
        temp.area = Float.parseFloat(line[15]);
        peaks.add(temp);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return peaks;
  }

  public static <T> void writeCSV(Collection<T> dataset, String csvFilePath, String[] csvHeaders) {
    try {
      // 定义路径，分隔符，编码
      CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
      // 写表头
      csvWriter.writeRecord(csvHeaders);
      // 写内容
      //遍历集合
      Iterator<T> it = dataset.iterator();
      while (it.hasNext()) {
        T t = (T) it.next();
        //获取类属性
        Field[] fields = t.getClass().getDeclaredFields();
        String[] csvContent = new String[fields.length];
        for (short i = 0; i < fields.length; i++) {

          Field field = fields[i];
          String fieldName = field.getName();
          String getMethodName = "get"
              + fieldName.substring(0, 1).toUpperCase()
              + fieldName.substring(1);
          try {
            Class tCls = t.getClass();
            Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
            Object value = getMethod.invoke(t, new Object[]{});
            if (value == null) {
              continue;
            }
            //取值并赋给数组
            String textvalue = value.toString();
            csvContent[i] = textvalue;
            //System.out.println("fieldname="+fieldName+"||getMethodname="+getMethodName+"||textvalue="+textvalue);
          } catch (Exception e) {
            e.getStackTrace();
          }
        }
        //迭代插入记录
        csvWriter.writeRecord(csvContent);
      }
      csvWriter.close();
      System.out.println("<--------CSV文件写入成功-------->");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
