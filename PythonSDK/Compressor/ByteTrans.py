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
        length = len(bytes)
        res = [None] * int(length / 4)
        k = 0
        for i in range(0, length, 4):
            res[k] = (int.from_bytes(bytes[i: i + 4], 'little', signed=True))
            k = k + 1

        return np.array(res, dtype=np.uint32).ravel()
