package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class RawFileInfo {

    String fileName;
    String filePath;
    Long fileSize;
    String extension;
    boolean isFile;
    boolean isFolder;
}
