from Compressor.Delta import Delta
from Enums.SortedIntCompType import SortedIntCompType


class DeltaWrapper:

    def getName(self):
        return SortedIntCompType.Delta

    def encode(self, input):
        return Delta.delta(input)

    def decode(self, input):
        return Delta.recover(input)

    def decode(self, input, offset, length):
        return Delta.recover(input[offset, offset + length])
