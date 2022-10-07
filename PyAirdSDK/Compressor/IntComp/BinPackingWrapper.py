from Compressor.IntCompressor import IntCompressor
from Enums.IntCompType import IntCompType
from pyfastpfor import *


class BinPackingWrapper:

    def __init__(self):
        self.codec = getCodec('fastbinarypacking32')  # binary packing with variable byte

    def getName(self):
        return IntCompType.BP

    def encode(self, input):
        compressed = IntCompressor.encode(self.codec, input)
        return compressed

    def decode(self, input, offset, length):
        decompressed = IntCompressor.decode(self.codec, input, offset, length)
        return decompressed
