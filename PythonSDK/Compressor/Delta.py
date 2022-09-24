import numpy as np


class Delta:

    @staticmethod
    def delta(data):
        res = []
        res[0] = data[0]
        for i in range(1, data.len):
            res[i] = data[i] - data[i-1]

        return res

    @staticmethod
    def recover(data):
        res = []
        res[0] = data[0]
        for i in range(1, data.len):
            res[i] = data[i] + res[i - 1]

        return res

