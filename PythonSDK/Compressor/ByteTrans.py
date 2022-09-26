import numpy as np


class ByteTrans:

    @staticmethod
    def intToByte(ints):
        res = []
        length = len(ints)
        for i in range(0, length):
            res.append(ints[i].to_bytes(4, 'little'))

        return res

    @staticmethod
    def byteToInt(bytes):
        res = []
        length = len(bytes)
        for i in range(0, length):
            res.append(int.from_bytes(bytes[i], 'little', signed=True))

        return res
