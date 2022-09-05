package net.csibio.aird.enums;

/**
 * Byte Compressor Type
 */
public enum ByteCompType {

    /**
     * Zlib Compressor
     */
    Zlib(0, "Zlib"),

    /**
     * Zstd Compressor from facebook(meta platform)
     */
    Zstd(1, "Zstd"),

    /**
     * Snappy Compressor from google
     */
    Snappy(2, "Snappy"),

    /**
     * Brotli Compressor from google
     */
    Brotli(3, "Brotli"),

    /**
     * Unknown Compressor
     */
    Unknown(-999, "Unknown"),
    ;

    /**
     * Compressor code
     */
    public int code;

    /**
     * Compressor name
     */
    public String name;

    /**
     * Byte Compressor type
     *
     * @param code compressor code
     * @param name compressor name
     */
    ByteCompType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Get byte comp type by code
     *
     * @param name compressor name
     * @return The instance of the compressor
     */
    public static ByteCompType getByName(String name) {
        for (ByteCompType value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return Unknown;
    }

    /**
     * Get byte comp name
     *
     * @return The name of compressor
     */
    public String getName() {
        return name;
    }

    /**
     * Get byte comp code
     *
     * @return The code of compressor
     */
    public int getCode() {
        return code;
    }
}
