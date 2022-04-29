package net.csibio.aird.enums;

public enum SortedIntCompType {

  /**
   * Integrated Binary Packing
   */
  IBP(0,"IBP"),

  /**
   * Integrated Variable Byte
   */
  IVB(1,"IVB"),

  Unknown(-999,"Unknown"),
  ;

  public String name;
  public int code;
  SortedIntCompType(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static SortedIntCompType getByName(String name) {
    for (SortedIntCompType value : values()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }
}
