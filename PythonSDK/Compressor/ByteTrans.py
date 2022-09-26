import numpy as np


class ByteTrans:

    @staticmethod
    def intToByte(ints):
        res = bytes()
        length = len(ints)
        for i in range(0, length):
            res += ints[i].to_bytes(4, 'little')

        return res

    @staticmethod
    def byteToInt(bytes):
        res = []
        length = len(bytes)
        for i in range(0, length, 4):
            res.append(int.from_bytes(bytes[i: i + 4], 'little', signed=True))

        return res
