#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

from Compressor.IntegratedIntCompressor import IntegratedIntCompressor
from Enums.IntCompType import IntCompType
from pyfastpfor import *


class IntegratedVarByteWrapper:

    def __init__(self):
        self.codec = getCodec('varint')

    def getName(self):
        return IntCompType.BP

    def encode(self, input):
        compressed = IntegratedIntCompressor().encode(self.codec, input)
        return compressed

    def decode(self, input, offset, length):
        decompressed = IntegratedIntCompressor().decode(self.codec, input, offset, length)
        return decompressed
