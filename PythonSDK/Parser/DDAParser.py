import time

from Beans.DDAMs import DDAMs
from Parser.BaseParser import BaseParser
from Utils.DDAUtil import DDAUtil


class DDAParser(BaseParser):

    def getMs1Index(self):
        if self.airdInfo is not None and self.airdInfo.indexList is not None and len(self.airdInfo.indexList) > 0:
            return self.airdInfo.indexList[0]

        return None

    def getAllMs2Index(self):
        if self.airdInfo is not None and self.airdInfo.indexList is not None and len(self.airdInfo.indexList) > 0:
            return self.airdInfo.indexList[1: len(self.airdInfo.indexList)]

        return None

    def getMs2IndexMap(self):
        if self.airdInfo is not None and self.airdInfo.indexList is not None and len(self.airdInfo.indexList) > 0:
            ms2IndexList = self.airdInfo.indexList[1: len(self.airdInfo.indexList)]
            results = {}
            for index in ms2IndexList:
                results[index.getParentNum()] = index

            return results

        return None

    def readAllToMemeory(self):
        ms1Index = self.getMs1Index()
        start = time.time()
        ms1Map = self.getSpectraByIndex(ms1Index)
        print("读取MS1 List,耗时:")
        print(time.time() - start)
        ms1RtList = list(ms1Map.keys())
        ms1List = self.buildDDAMsList(ms1RtList, ms1Index, ms1Map, True)
        return ms1List

    def buildDDAMsList(self, rtList, ms1Index, ms1Map, includeMS2):
        ms1List = [None] * len(rtList)
        ms2IndexMap = None
        if includeMS2:
            ms2IndexMap = self.getMs2IndexMap()

        for i in range(len(rtList)):
            ms1 = DDAMs(rtList[i], ms1Map[rtList[i]])
            DDAUtil.initFromIndex(ms1, ms1Index, i)
            if includeMS2:
                if ms1.num in ms2IndexMap:
                    ms2Index = ms2IndexMap[ms1.num]
                    if ms2Index is not None:
                        ms2Map = self.getSpectra(ms2Index.startPtr, ms2Index.endPtr, ms2Index.rts, ms2Index.mzs,
                                                 ms2Index.ints)
                        ms2RtList = list(ms2Map.keys())
                        ms2List = [None] * len(ms2RtList)
                        for j in range(len(ms2RtList)):
                            ms2 = DDAMs(ms2RtList[j], ms2Map[ms2RtList[j]])
                            DDAUtil.initFromIndex(ms2, ms2Index, j)
                            ms2List[j] = ms2

                        ms1.ms2List = ms2List
            ms1List[i] = ms1

        return ms1List

    def getSpectrumByNum(self, num):
        indexList = self.airdInfo.indexList
        for blockIndex in indexList:
            index = blockIndex.nums.index(num)
            if index >= 0:
                return self.getSpectrumByIndex(blockIndex.startPtr, blockIndex.mzs, blockIndex.ints, index)

        return None

    def getSpectraByRtRange(self, rtStart, rtEnd, includeMS2):
        ms1Index = self.getMs1Index()