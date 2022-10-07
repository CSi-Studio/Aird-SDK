from Enums.ByteCompType import ByteCompType
import brotli


class BrotliWrapper:

    def getName(self):
        return ByteCompType.Brotli

    def encode(self, input):
        return brotli.compress(input)

    def decode(self, input, offset, length):
        return brotli.decompress(input[offset: offset + length])
