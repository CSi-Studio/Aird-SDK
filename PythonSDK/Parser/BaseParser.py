import os

import math

from Beans.Common.Spectrum import Spectrum
from Beans.Compressor import Compressor
from Compressor.ByteComp.BrotliWrapper import BrotliWrapper
from Compressor.ByteComp.SnappyWrapper import SnappyWrapper
from Compressor.ByteComp.ZlibWrapper import ZlibWrapper
from Compressor.ByteComp.ZstdWrapper import ZstdWrapper
from Compressor.ByteTrans import ByteTrans
from Compressor.IntComp.BinPackingWrapper import BinPackingWrapper
from Compressor.IntComp.EmptyWrapper import EmptyWrapper
from Compressor.IntComp.VarByteWrapper import VarByteWrapper
from Compressor.SortedIntComp.DeltaWrapper import DeltaWrapper
from Compressor.SortedIntComp.IntegratedBinPackingWrapper import IntegratedBinPackingWrapper
from Compressor.SortedIntComp.IntegratedVarByteWrapper import IntegratedVarByteWrapper
from Enums.DataDim import DataDim
from Utils.AirdScanUtil import AirdScanUtil


class BaseParser:

    def __init__(self, indexPath):
        self.indexPath = indexPath
        self.airdInfo = AirdScanUtil.loadAirdInfo(indexPath)
        self.airdPath = AirdScanUtil.getAirdPathByIndexPath(indexPath)
        self.airdFile = open(self.airdPath, 'rb')
        self.intCompressor = None
        self.intByteComp = None
        self.intIntComp = None
        self.intPrecision = None
        self.mobiCompressor = None
        self.mobiDict = None
        self.mobiIntComp = None
        self.mobiByteComp = None
        self.mobiPrecision = None
        self.mzCompressor = None
        self.mzIntComp = None
        self.mzByteComp = None
        self.mzPrecision = None

        self.parseCompsFromAirdInfo()
        self.parseComboComp()
        self.parseMobilityDict()

    # def buildParser(self, indexJsonPath):
    #     self.indexPath = indexJsonPath
    #     self.airdInfo = AirdScanUtil.loadAirdInfo(self.indexPath)
    #     self.airdPath = AirdScanUtil.getAirdPathByIndexPath(indexJsonPath)
    #     self.airdFile = open(self.airdPath, 'rb')
    #     self.parseCompsFromAirdInfo()
    #     # self.parseComboComp()
    #
    #     if self.airdInfo.type is AirdType.DIA:
    #         return DIAParser(indexJsonPath, self.airdInfo)
    #     elif self.airdInfo.type is AirdType.DDA:
    #         return DDAParser(indexJsonPath, self.airdInfo)
    #     elif self.airdInfo.type is AirdType.PRM:
    #         return PRMParser(indexJsonPath, self.airdInfo)
    #     elif self.airdInfo.type is AirdType.DIA_PASEF:
    #         return DIAPasefParser(indexJsonPath, self.airdInfo)
    #     elif self.airdInfo.type is AirdType.DDA_PASEF:
    #         return DDAPasefParser(indexJsonPath, self.airdInfo)

    def parseCompsFromAirdInfo(self):
        self.mzCompressor = BaseParser.fetchTargetCompressor(self.airdInfo.compressors, DataDim.TARGET_MZ.value)
        self.intCompressor = BaseParser.fetchTargetCompressor(self.airdInfo.compressors, DataDim.TARGET_INTENSITY.value)
        self.mobiCompressor = BaseParser.fetchTargetCompressor(self.airdInfo.compressors, DataDim.TARGET_MOBILITY.value)
        self.mzPrecision = self.mzCompressor.precision
        self.intPrecision = self.intCompressor.precision
        self.mobiPrecision = self.mobiCompressor.precision

    def parseMobilityDict(self):
        mobiInfo = self.airdInfo.mobiInfo
        if mobiInfo.type == "TIMS":
            self.airdFile.seek(mobiInfo.dictStart, 0)
            delta = mobiInfo.dictEnd - mobiInfo.dictStart
            result = self.airdFile.read(delta)
            mobiArray = DeltaWrapper().decode(ByteTrans.byteToInt(ZstdWrapper().decode(result)))
            mobiDArray = []
            for i in range(0, len(mobiArray)):
                mobiDArray[i] = mobiArray[i] / self.mobiPrecision
            self.mobiDict = mobiDArray

    def parseComboComp(self):
        mzMethods = self.mzCompressor.methods
        if len(mzMethods) == 2:
            self.mzIntComp = BaseParser.parseComp(mzMethods[0])
            self.mzByteComp = BaseParser.parseComp(mzMethods[1])

        intMethods = self.intCompressor.methods
        if len(intMethods) == 2:
            self.intIntComp = BaseParser.parseComp(intMethods[0])
            self.intByteComp = BaseParser.parseComp(intMethods[1])

        if self.mobiCompressor is not None:
            mobiMethods = self.mobiCompressor.methods
            if len(mobiMethods) == 2:
                self.mobiIntComp = BaseParser.parseComp(mobiMethods[0])
                self.mobiByteComp = BaseParser.parseComp(mobiMethods[1])

    def getSpectrum(self, bytes, offset, mzOffset, intOffset):
        if mzOffset == 0:
            return Spectrum([], [], None)
        mzArray = self.getMzs(bytes, offset, mzOffset)
        offset = offset + mzOffset
        intensityArray = self.getInts(bytes, offset, intOffset)
        return Spectrum(mzArray, intensityArray, None)

    def getMzs(self, value, offset, length):
        decodedData = self.mzByteComp.decode(value, offset, length)
        intValues = ByteTrans.byteToInt(decodedData)
        intValues = self.mzIntComp.decode(intValues, 0, len(intValues))
        doubleValues = []
        for i in range(len(intValues)):
            doubleValues[i] = intValues[i] / self.mzPrecision

        return doubleValues

    def getInts(self, value, start, length):
        decodedData = self.intByteComp.decode(value, start, length)
        intValues = ByteTrans.byteToInt(decodedData)
        intValues = self.intIntComp.decode(intValues, 0, len(intValues))
        intensityValues = []
        for i in range(len(intValues)):
            intensity = intValues[i]
            if intensity < 0:
                intensity = math.pow(2, -intensity / 100000.0)
            intensityValues[i] = intensity / self.intPrecision

        return intensityValues

    def getMobilities(self, value, start, length):
        decodedData = self.mobiByteComp.decode(value, start, length)
        intValues = ByteTrans.byteToInt(decodedData)
        intValues = self.mobiIntComp.decode(intValues, 0, len(intValues))
        mobilities = []
        for i in range(len(intValues)):
            mobilities[i] = self.mobiDict[intValues[i]]

        return mobilities

    def getSpectraByIndex(self, index):
        return self.getSpectra(index.startPtr, index.endPtr, index.rts, index.mzs, index.ints)

    def getSpectra(self, start, end, rtList, mzOffsets, intOffsets):
        map = {}
        self.airdFile.seek(start)
        delta = end - start
        result = self.airdFile.read(delta)
        iter = 0
        for i in range(len(rtList)):
            map[rtList[i]] = self.getSpectrum(result, iter, mzOffsets[i], intOffsets[i])
            iter = iter + mzOffsets[i] + intOffsets[i]

        return map

    @staticmethod
    def parseComp(name):
        if name == 'VB':
            return VarByteWrapper()
        elif name == 'BP':
            return BinPackingWrapper()
        elif name == 'Empty':
            return EmptyWrapper()
        elif name == 'Zlib':
            return ZlibWrapper()
        elif name == 'Brotli':
            return BrotliWrapper()
        elif name == 'Snappy':
            return SnappyWrapper()
        elif name == 'Zstd':
            return ZstdWrapper()
        elif name == 'IVB':
            return IntegratedVarByteWrapper()
        elif name == 'IBP':
            return IntegratedBinPackingWrapper()
        elif name == 'Delta':
            return DeltaWrapper()

    @staticmethod
    def fetchTargetCompressor(compressors, target):
        if target is None:
            return None
        for compressor in compressors:
            if compressor.target == target:
                return compressor

        return None
