from pyfastpfor import *
from Enums.IntCompType import IntCompType
import numpy as np


class VarByteWrapper:

    def getName(self):
        return IntCompType.VB

    def encode(self, input):
        array = np.array(input, dtype=np.uint32)
        arraySize = len(input)
        inpComp = np.zeros(arraySize + 1024, dtype=np.uint32)
        codec = getCodec('vbyte')
        compSize = codec.encodeArray(array, arraySize, inpComp, len(inpComp))
        compressed = np.insert(inpComp[:compSize], 0, arraySize)

        return compressed.tolist()

    def decode(self, input, offset, length):
        input = input[offset:offset + length]
        originalSize = input[0]
        input = input[1: len(input)]
        array = np.array(input, dtype=np.uint32)
        decompress = np.zeros(originalSize + 1024, dtype=np.uint32)
        codec = getCodec('vbyte')
        decompressSize = codec.decodeArray(array, len(array), decompress, originalSize)
        return decompress[0:decompressSize].tolist()

