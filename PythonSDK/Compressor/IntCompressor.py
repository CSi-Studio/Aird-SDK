from pyfastpfor import *
from Enums.IntCompType import IntCompType
import numpy as np


class IntCompressor:

    def __init__(self, codec):
        self.codec = codec

    def getName(self):
        return IntCompType.BP

    def encode(self, input):
        array = np.array(input, dtype=np.uint32).ravel()
        arraySize = len(input)
        inpComp = np.zeros(arraySize + 1024, dtype=np.uint32).ravel()
        codec = getCodec(self.codec)
        compSize = codec.encodeArray(array, arraySize, inpComp, len(inpComp))
        compressed = np.insert(inpComp[:compSize], 0, arraySize)

        return compressed.tolist()

    def decode(self, input, offset, length):
        input = input[offset:offset + length]
        originalSize = input[0]
        input = input[1: len(input)]
        array = np.array(input, dtype=np.uint32).ravel()
        decompress = np.zeros(originalSize, dtype=np.uint32).ravel()
        codec = getCodec(self.codec)
        decompressSize = codec.decodeArray(array, len(array), decompress, originalSize)
        return decompress[0:decompressSize].tolist()
