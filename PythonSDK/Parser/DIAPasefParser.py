import os
from Constants.SuffixConst import SuffixConst
from Constants.SymbolConst import SymbolConst
import json

from Enums.AirdType import AirdType
from Parser.BaseParser import BaseParser
from Utils.AirdScanUtil import AirdScanUtil


class DIAPasefParser(BaseParser):

    def __init__(self, indexJsonPath, airdInfo):
        self.indexPath = indexJsonPath
        self.airdInfo = airdInfo
    


