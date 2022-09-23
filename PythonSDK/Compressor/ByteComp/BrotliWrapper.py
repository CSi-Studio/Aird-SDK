from Compressor.ByteComp import ByteComp
from Enums.ByteCompType import ByteCompType


class BrotliWrapper(ByteComp):

    def getName(self):
        return ByteCompType.Brotli

    def encode(self, input):

        return

    def decode(self, input, offset, length):
        return
