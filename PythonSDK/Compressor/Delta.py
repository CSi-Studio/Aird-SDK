import numpy as np


class Delta:

    @staticmethod
    def delta(data):
        res = [0] * len(data)
        res[0] = data[0]
        for i in range(1, len(data)):
            res[i] = data[i] - data[i-1]
        return res

    @staticmethod
    def recover(data):
        res = [0] * len(data)
        res[0] = data[0]
        for i in range(1, len(data)):
            res[i] = data[i] + res[i - 1]
        return res

