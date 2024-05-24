package net.csibio.aird.util;

import java.util.Arrays;
import java.util.List;
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
}
