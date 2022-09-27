from pyfastpfor import *

from Compressor.Delta import Delta
from Enums.IntCompType import IntCompType
import numpy as np


class IntegratedIntCompressor:

    @staticmethod
    def encode(codec, input):
        array = np.array(input, dtype=np.uint32).ravel()
        arraySize = len(input)
        delta4(array, len(array))
        inpComp = np.zeros(arraySize + 1024, dtype=np.uint32).ravel()
        compSize = codec.encodeArray(array, arraySize, inpComp, len(inpComp))
        compressed = np.insert(inpComp[:compSize], 0, arraySize)
        return compressed.tolist()

    @staticmethod
    def decode(codec, input, offset, length):
        input = input[offset:offset + length]
        originalSize = input[0]
        input = input[1: len(input)]
        decompress = np.zeros(originalSize, dtype=np.uint32).ravel()
        codec.decodeArray(input, input.size, decompress, originalSize)
        prefixSum1(decompress, decompress.size)
        return decompress