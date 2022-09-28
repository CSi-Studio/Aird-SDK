from Compressor.IntegratedIntCompressor import IntegratedIntCompressor
from Enums.IntCompType import IntCompType
from pyfastpfor import *


class IntegratedBinPackingWrapper:

    def __init__(self):
        self.codec = getCodec('fastbinarypacking32')

    def getName(self):
        return IntCompType.BP

    def encode(self, input):
        compressed = IntegratedIntCompressor().encode(self.codec, input)
        return compressed

    def decode(self, input, offset, length):
        decompressed = IntegratedIntCompressor().decode(self.codec, input, offset, length)
        return decompressed
