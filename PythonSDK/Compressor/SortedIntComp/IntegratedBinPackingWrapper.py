from Compressor.IntCompressor import IntCompressor
from Compressor.IntegratedIntCompressor import IntegratedIntCompressor
from Enums.IntCompType import IntCompType


class IntegratedBinPackingWrapper:
    codec = 'simdbinarypacking'

    def getName(self):
        return IntCompType.BP

    def encode(self, input):
        compressed = IntegratedIntCompressor(self.codec).encode(input)
        return compressed

    def decode(self, input, offset, length):
        decompressed = IntegratedIntCompressor(self.codec).decode(input, offset, length)
        return decompressed
