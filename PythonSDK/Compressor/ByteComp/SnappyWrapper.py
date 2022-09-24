from Enums.ByteCompType import ByteCompType
import snappy


class SnappyWrapper:

    def getName(self):
        return ByteCompType.Snappy

    def encode(self, input):
        return snappy.compress(input)

    def decode(self, input):
        return snappy.decompress(input)

    def decode(self, input, offset, length):
        return snappy.decompress(input[offset, offset + length])
