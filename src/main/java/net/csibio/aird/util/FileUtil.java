/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by James Lu MiaoShan Time: 2018-08-28 21:45
 */
public class FileUtil {

  /**
   * read all the string in the target file
   *
   * @param file 文件对象
   * @return content in the file
   */
  public static String readFile(File file) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      int fileLength = fis.available();
      byte[] bytes = new byte[fileLength];
      fis.read(bytes);
      return new String(bytes, 0, fileLength);
    } catch (Exception e) {
      e.printStackTrace();
      close(fis);
      return null;
    }
  }

  /**
   * read file content by filePath
   *
   * @param filePath 文件路径
   * @return content in the file
   */
  public static String readFile(String filePath) {
    File file = new File(filePath);
    return readFile(file);
  }

  /**
   * read string content from resources
   *
   * @param filePath filePath in resources
   * @return file content
   * @throws IOException File IO Exception
   */
  public static String readFileFromSource(String filePath) throws IOException {
    File file = new File(FileUtil.class.getClassLoader().getResource(filePath).getPath());
    FileInputStream fis = new FileInputStream(file);
    int fileLength = fis.available();
    byte[] bytes = new byte[fileLength];
    fis.read(bytes);
    close(fis);
    return new String(bytes, 0, fileLength);
  }

  /**
   * close the random access file object
   *
   * @param raf 文件流
   */
  public static void close(RandomAccessFile raf) {
    if (raf != null) {
      try {
        raf.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the FileWriter object
   *
   * @param fw 文件流
   */
  public static void close(FileWriter fw) {
    if (fw != null) {
      try {
        fw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the BufferWriter object
   *
   * @param bw 文件流
   */
  public static void close(BufferedWriter bw) {
    if (bw != null) {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the FileOutputStream object
   *
   * @param fos 文件流
   */
  public static void close(FileOutputStream fos) {
    if (fos != null) {
      try {
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the FileInputStream object
   *
   * @param fis 文件流
   */
  public static void close(InputStream fis) {
    if (fis != null) {
      try {
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the BufferedOutputStream object
   *
   * @param bos 文件流
   */
  public static void close(BufferedOutputStream bos) {
    if (bos != null) {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the ByteArrayOutputStream baos
   *
   * @param baos 文件流
   */
  public static void close(ByteArrayOutputStream baos) {
    if (baos != null) {
      try {
        baos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the GZIPOutputStream bos
   *
   * @param bos 文件流
   */
  public static void close(GZIPOutputStream bos) {
    if (bos != null) {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the ZipOutputStream bos
   *
   * @param bos 文件流
   */
  public static void close(ZipOutputStream bos) {
    if (bos != null) {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * close the OutputStream bos
   *
   * @param stream 文件流
   */
  public static void close(OutputStream stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
