import os

from Beans.AirdInfo import AirdInfo
from Constants.SuffixConst import SuffixConst
from Constants.SymbolConst import SymbolConst
import json


class AirdScanUtil:

    @staticmethod
    def scanIndexFiles(path):
        fileList = []
        for filename in os.listdir(path):
            if filename.lower().endswith(SuffixConst.JSON):
                fileList.append(os.path.join(path, filename))

        return fileList

    @staticmethod
    def loadAirdInfo(indexFilePath):
        content = open(indexFilePath, 'r')
        dict = json.load(content)
        return AirdInfo(dict)

    @staticmethod
    def getAirdPathByIndexPath(indexPath):
        if indexPath is None or SymbolConst.DOT not in indexPath or SuffixConst.JSON not in indexPath.lower():
            return None
        return indexPath[0: indexPath.rindex(SymbolConst.DOT)] + SuffixConst.JSON

    @staticmethod
    def getIndexPathByAirdPath(airdPath):
        if airdPath is None or SymbolConst.DOT not in airdPath or SuffixConst.AIRD not in airdPath.lower():
            return None
        return airdPath[0: airdPath.rindex(SymbolConst.DOT)] + SuffixConst.AIRD
