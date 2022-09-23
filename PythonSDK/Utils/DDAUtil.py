import os
from Constants.SuffixConst import SuffixConst
from Constants.SymbolConst import SymbolConst
import json


class AirdScanUtil:

    @staticmethod
    def initFromIndex(ms, index, loc):
        if index.nums is not None and index.nums.length > 0:
            ms.num = index.nums[loc]
        if index.cvList is not None and index.cvList.length > 0:
            ms.cvList = index.cvList[loc]
        if index.tics is not None and index.tics.length > 0:
            ms.tic = index.tics[loc]
        if index.rangeList is not None and index.rangeList.length > 0:
            ms.range = index.rangeList[loc]