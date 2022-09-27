from Compressor.IntCompressor import IntCompressor
from Enums.IntCompType import IntCompType
from pyfastpfor import *


class FastPFor256Wrapper:

    def __init__(self):
        self.codec = getCodec('fastpfor256')

    def getName(self):
        return IntCompType.FPF256

    def encode(self, input):
        compressed = IntCompressor.encode(self.codec, input)
        return compressed

    def decode(self, input, offset, length):
        decompressed = IntCompressor.decode(self.codec, input, offset, length)
        return decompressed
