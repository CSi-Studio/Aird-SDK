#
# Copyright (c) 2020 CSi Biotech
# AirdSDK and AirdPro are licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
#* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.

class Instrument:

    def __init__(self, dict):
        self.manufacturer = dict['manufacturer'] if 'manufacturer' in dict else None
        self.ionisation = dict['ionisation'] if 'ionisation' in dict else None
        self.resolution = dict['resolution'] if 'resolution' in dict else None
        self.model = dict['model'] if 'model' in dict else None
        self.source = dict['source'] if 'source' in dict else None
        self.analyzer = dict['analyzer'] if 'analyzer' in dict else None
        self.detector = dict['detector'] if 'detector' in dict else None




