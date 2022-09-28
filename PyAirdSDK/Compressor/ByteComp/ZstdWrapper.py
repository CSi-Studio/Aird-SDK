import zstandard as zstd

from Enums.ByteCompType import ByteCompType


class ZstdWrapper:

    def getName(self):
        return ByteCompType.Zstd

    def encode(self, input):
        return zstd.compress(input)

    def decode(self, input, offset, length):
        return zstd.decompress(input[offset: offset + length])
