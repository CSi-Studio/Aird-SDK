from pyfastpfor import *
from Enums.IntCompType import IntCompType


class BinPackWrapper:

    def getName(self):
        return IntCompType.BP

    def encode(self, input):
        return input

    def decode(self, input):
        return input

    def decode(self, input, offset, length):
        return input[offset, offset + length]
