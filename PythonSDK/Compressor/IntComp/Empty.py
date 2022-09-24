from Enums.IntCompType import IntCompType


class Empty:

    def getName(self):
        return IntCompType.Empty

    def encode(self, input):
        return input

    def decode(self, input):
        return input

    def decode(self, input, offset, length):
        return input[offset, offset + length]
