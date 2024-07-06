package net.csibio.aird.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CsvUtil {

    public static <T> String toCsv(List<T> list) {
        // 假设所有对象都是同一个类的实例，我们取第一个对象来获取列标题
        T firstItem = list.get(0);
        // 反射获取所有属性名
        List<String> headers = Arrays.stream(firstItem.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList());

        // 开始构建CSV字符串
        StringBuilder csvContent = new StringBuilder();
        // 添加列标题
        csvContent.append(String.join(",", headers)).append("\n");

        // 遍历对象列表，添加每一行数据
        for (T item : list) {
            List<String> values = Arrays.stream(item.getClass().getDeclaredFields())
                    .filter(field -> !field.isAccessible())
                    .peek(field -> field.setAccessible(true))
                    .map(field -> {
                        try {
                            if (field.get(item) == null) {
                                return "";
                            } else {
                                return field.get(item).toString();
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            // 添加属性值
            csvContent.append(String.join(",", values)).append("\n");
        }

        return csvContent.toString();
    }

    public static List<Map<String, Object>> readCSV(String filePath) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine(); // 读取标题行（如果有）
            String[] keys = line.split(",");
            // 读取其余行
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(","); // 假设使用逗号分隔
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < data.length; i++) {
                    map.put(keys[i], data[i]);
                }
                dataList.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close(); // 关闭BufferedReader
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }
}
