package net.csibio.aird.compressor;

public enum CompressorType {

  /**
   * Zlib Compressor
   */
  Zlib("Zlib"),

  /**
   * Snappy Compressor from google
   */
  Snappy("Snappy"),

  /**
   * Brotli Compressor from google
   */
  Brotli("Brotli"),

  /**
   * Zstd Compressor from facebook(meta platform)
   */
  Zstd("Zstd"),

  /**
   * Integrated Variable Byte
   */
  IVB("IVB"),

  /**
   * Integrated Binary Packing
   */
  IBP("IBP"),

  /**
   * Variable Byte
   */
  VB("VB"),

  /**
   * Binary Packing
   */
  BP("BP"),
  Unknown("Unknown"),
  ;

  public String name;

  CompressorType(String name) {
    this.name = name;
  }

  public static CompressorType getByName(String name) {
    for (CompressorType value : values()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    return Unknown;
  }

  public String getName() {
    return name;
  }
}
