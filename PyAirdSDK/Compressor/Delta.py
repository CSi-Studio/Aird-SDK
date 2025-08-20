#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

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

