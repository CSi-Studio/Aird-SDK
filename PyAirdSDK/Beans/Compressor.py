#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

class Compressor:

    def __init__(self, dict):
        self.target = dict['target'] if 'target' in dict else None
        self.methods = dict['methods'] if 'methods' in dict else None
        self.precision = dict['precision'] if 'precision' in dict else None
        self.digit = dict['digit'] if 'digit' in dict else None
        self.byteOrder = dict['byteOrder'] if 'byteOrder' in dict else "LITTLE_ENDIAN"




