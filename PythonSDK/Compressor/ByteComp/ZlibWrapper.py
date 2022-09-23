from Compressor.ByteComp import ByteComp
from Enums.ByteCompType import ByteCompType
import zlib


class ZlibWrapper(ByteComp):

    def getName(self):
        return ByteCompType.Zlib

    def encode(self, input):
        return zlib.compress(input)

    def decode(self, input):
        return zlib.decompress(input)

    def decode(self, input, offset, length):
        return zlib.decompress(input[offset, offset+length])
