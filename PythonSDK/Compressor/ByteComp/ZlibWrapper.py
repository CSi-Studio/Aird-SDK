from Enums.ByteCompType import ByteCompType
import zlib


class ZlibWrapper:

    def getName(self):
        return ByteCompType.Zlib

    def encode(self, input):
        return zlib.compress(input)

    def decode(self, input, offset, length):
        return zlib.decompress(input[offset, offset+length])
