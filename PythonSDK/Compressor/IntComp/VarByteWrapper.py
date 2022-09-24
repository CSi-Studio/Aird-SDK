from Enums.IntCompType import IntCompType


class VarByteWrapper:

    def getName(self):
        return IntCompType.VB

    def encode(self, input):
        return input

    def decode(self, input):
        return input

    def decode(self, input, offset, length):
        return input[offset, offset + length]
