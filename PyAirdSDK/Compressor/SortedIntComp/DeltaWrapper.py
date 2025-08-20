#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

from Compressor.Delta import Delta
from Enums.SortedIntCompType import SortedIntCompType


class DeltaWrapper:

    def __init__(self):
        pass

    def getName(self):
        return SortedIntCompType.Delta

    def encode(self, input):
        return Delta.delta(input)

    def decode(self, input, offset, length):
        return Delta.recover(input[offset: offset + length])
