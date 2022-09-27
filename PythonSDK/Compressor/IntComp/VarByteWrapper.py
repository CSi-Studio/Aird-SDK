from Compressor.IntCompressor import IntCompressor
from Enums.IntCompType import IntCompType


class VarByteWrapper:
    codec = 'varint'

    def getName(self):
        return IntCompType.VB

    def encode(self, input):
        compressed = IntCompressor(self.codec).encode(input)
        return compressed

    def decode(self, input, offset, length):
        decompressed = IntCompressor(self.codec).decode(input, offset, length)
        return decompressed
