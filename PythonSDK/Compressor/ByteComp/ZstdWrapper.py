import zstandard as zstd

from Compressor.ByteComp import ByteComp
from Enums.ByteCompType import ByteCompType


class ZstdWrapper(ByteComp):

    def getName(self):
        return ByteCompType.Zstd

    def encode(self, input):
        return zstd.compress(input)

    def decode(self, input):
        return zstd.decompress(input)

    def decode(self, input, offset, length):
        return zstd.decompress(input[offset, offset + length])
