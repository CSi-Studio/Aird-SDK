import os

from Beans.DDAMs import DDAMs
from Constants.SuffixConst import SuffixConst
from Constants.SymbolConst import SymbolConst
import json

from Enums.AirdType import AirdType
from Parser.BaseParser import BaseParser
from Utils.AirdScanUtil import AirdScanUtil
from Utils.DDAUtil import DDAUtil


class DDAParser(BaseParser):

    def getMs1Index(self):
        if self.airdInfo is not None and self.airdInfo.indexList is not None and len(self.airdInfo.indexList) > 0:
            return self.airdInfo.indexList[0]

        return None

    def getAllMs2Index(self):
        if self.airdInfo is not None and self.airdInfo.indexList is not None and len(self.airdInfo.indexList) > 0:
            return self.airdInfo.indexList[1, len(self.airdInfo.indexList)]

        return None

    def getMs2IndexMap(self):
        if self.airdInfo is not None and self.airdInfo.indexList is not None and len(self.airdInfo.indexList) > 0:
            ms2IndexList = self.airdInfo.indexList[1, len(self.airdInfo.indexList)]
            results = {}
            for index in ms2IndexList:
                results[index.getParentNum()] = index

            return results

        return None

    def readAllToMemeory(self):
        ms1Index = self.getMs1Index()
        ms1Map = self.getSpectraByIndex(ms1Index)
        ms1RtList = ms1Map.keys().tolist()
        ms1List = self.buildDDAMsList(ms1RtList, ms1Index, ms1Map, True)
        return ms1List

    def buildDDAMsList(self, rtList, ms1Index, ms1Map, includeMS2):
        ms1List = []
        ms2IndexMap = None
        if includeMS2:
            ms2IndexMap = self.getMs2IndexMap()

        for i in range(len(rtList)):
            ms1 = DDAMs(rtList[i], ms1Map[rtList[i]])
            DDAUtil.initFromIndex(ms1, ms1Index, i)
            if includeMS2:
                ms2Index = ms2IndexMap[ms1.num]
                if ms2Index is not None:
                    ms2Map = self.getSpectra(ms2Index.startPtr, ms2Index.endPtr, ms2Index.rts, ms2Index.mzs,
                                             ms2Index.ints)
                    ms2RtList = ms2Map.keys().tolist()
                    ms2List = []
                    for j in range(len(ms2RtList)):
                        ms2 = DDAMs(ms2RtList[j], ms2Map[ms2RtList[j]])
                        DDAUtil.initFromIndex(ms2, ms2Index, j)
                        ms2List.append(ms2)

                    ms1.ms2List = ms2List

            ms1List.append(ms1)

        return ms1List
