package net.csibio.aird.compressor;

public enum CompressorType {

    Zlib("Zlib"),
    Snappy("Snappy"),
    Brotli("Brotli"),
    Zstd("Zstd"),
    IVB("IVB"), //Integrated Variable Byte
    IBP("IBP"), //Integrated Binary Packing
    VB("VB"), //Variable Byte
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
