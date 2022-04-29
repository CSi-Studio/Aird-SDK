package net.csibio.aird.enums;

public enum IntCompType {

  /**
   * 空压缩器
   */
  Empty(-1,"Empty"),

  /**
   * Binary Packing
   */
  BP(2 ,"BP"),

  /**
   * Variable Byte
   */
  VB(3,"VB"),
  ;

  public int code;
  public String name;

  IntCompType(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static IntCompType getByName(String name) {
    for (IntCompType value : values()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    return Empty;
  }

  public int getCode() {
    return code;
  }
  public String getName() {
    return name;
  }
}
