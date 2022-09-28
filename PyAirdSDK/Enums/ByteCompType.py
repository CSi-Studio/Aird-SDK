from enum import Enum


class ByteCompType(Enum):
    Zlib = 0
    Zstd = 1
    Snappy = 2
    Brotli = 3
