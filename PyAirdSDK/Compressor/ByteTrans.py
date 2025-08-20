#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

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
