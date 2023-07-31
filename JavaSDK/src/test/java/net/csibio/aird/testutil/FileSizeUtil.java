package net.csibio.aird.testutil;

import net.csibio.aird.bean.common.RawFileInfo;
import net.csibio.aird.util.FileUtil;

import java.io.File;
import java.util.HashMap;

public class FileSizeUtil {

    public static HashMap<String, Long> sumSizes(String folder) {
        File root = new File(folder);
        HashMap<String, Long> sizeMap = new HashMap<>();
        File[] files = root.listFiles();
        if (files == null) {
            return sizeMap;
        }
        for (File file : files) {
            RawFileInfo raw = FileUtil.buildRawInfo(file.getPath());
            if (raw != null) {
                Long size = sizeMap.get(raw.getFileName());
                if (size == null) {
                    sizeMap.put(raw.getFileName(), raw.getFileSize());
                } else {
                    sizeMap.put(raw.getFileName(), raw.getFileSize() + size);
                }
            }
        }
        return sizeMap;
    }
}
