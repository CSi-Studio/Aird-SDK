#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

from enum import Enum


class AirdType(Enum):
    DIA_PASEF = "DIA_PASEF"
    DDA_PASEF = "DDA_PASEF"
    PRM_PASEF = "PRM_PASEF"
    DIA = "DIA"
    PRM = "PRM"
    SCANNING_SWATH = "SCANNING_SWATH"
    DDA = "DDA"

