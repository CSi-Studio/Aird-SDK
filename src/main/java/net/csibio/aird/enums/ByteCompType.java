package net.csibio.aird.enums;

public enum ByteCompType {

  /**
   * Zlib Compressor
   */
  Zlib(0,"Zlib"),

  /**
   * Zstd Compressor from facebook(meta platform)
   */
  Zstd(1,"Zstd"),

  /**
   * Snappy Compressor from google
   */
  Snappy(2,"Snappy"),

  /**
   * Brotli Compressor from google
   */
  Brotli(3,"Brotli"),

  Unknown(-999,"Unknown"),
  ;

  public int code;
  public String name;

  ByteCompType(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static ByteCompType getByName(String name) {
    for (ByteCompType value : values()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    return Unknown;
  }

  public String getName() {
    return name;
  }
  public int getCode() {
    return code;
  }
}
