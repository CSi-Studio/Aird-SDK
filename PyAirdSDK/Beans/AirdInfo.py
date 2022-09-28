from Beans.BlockIndex import BlockIndex
from Beans.Compressor import Compressor
from Beans.DataProcessing import DataProcessing
from Beans.Instrument import Instrument
from Beans.MobiInfo import MobiInfo
from Beans.ParentFile import ParentFile
from Beans.Software import Software
from Beans.WindowRange import WindowRange


class AirdInfo:
    def __init__(self, dict):
        self.version = dict['version'] if 'version' in dict else None
        self.versionCode = dict['versionCode'] if 'versionCode' in dict else None
        compressors = []
        if "compressors" in dict and len(dict['compressors']) > 0:
            for compressorDict in dict['compressors']:
                compressors.append(Compressor(compressorDict))
        self.compressors = compressors

        instruments = []
        if "instruments" in dict and len(dict['instruments']) > 0:
            for instrumentDict in dict['instruments']:
                instruments.append(Instrument(instrumentDict))
        self.instruments = instruments

        dataProcessings = []
        if "dataProcessings" in dict and len(dict['dataProcessings']) > 0:
            for dataProcessingDict in dict['dataProcessings']:
                dataProcessings.append(DataProcessing(dataProcessingDict))
        self.dataProcessings = dataProcessings

        softwares = []
        if "softwares" in dict and len(dict['softwares']) > 0:
            for softwareDict in dict['softwares']:
                softwares.append(Software(softwareDict))
        self.softwares = softwares

        parentFiles = []
        if "parentFiles" in dict and len(dict['parentFiles']) > 0:
            for parentFileDict in dict['parentFiles']:
                parentFiles.append(ParentFile(parentFileDict))
        self.parentFiles = parentFiles

        rangeList = []
        if "rangeList" in dict and len(dict['rangeList']) > 0:
            for rangeDict in dict['rangeList']:
                rangeList.append(WindowRange(rangeDict))
        self.rangeList = rangeList

        indexList = []
        if "indexList" in dict and len(dict['indexList']) > 0:
            for indexDict in dict['indexList']:
                indexList.append(BlockIndex(indexDict))
        self.indexList = indexList

        self.type = dict['type'] if 'type' in dict else None
        self.fileSize = dict['fileSize'] if 'fileSize' in dict else None
        self.totalCount = dict['totalCount'] if 'totalCount' in dict else None
        self.airdPath = dict['airdPath'] if 'airdPath' in dict else None
        self.activator = dict['activator'] if 'activator' in dict else None
        self.energy = dict['energy'] if 'energy' in dict else None
        self.msType = dict['msType'] if 'msType' in dict else None
        self.rtUnit = dict['rtUnit'] if 'rtUnit' in dict else None
        self.polarity = dict['polarity'] if 'polarity' in dict else None
        self.ignoreZeroIntensityPoint = dict['ignoreZeroIntensityPoint'] if 'ignoreZeroIntensityPoint' in dict else None
        if 'mobiInfo' in dict:
            self.mobiInfo = MobiInfo(dict['mobiInfo'])
        self.creator = dict['creator'] if 'creator' in dict else None
        self.features = dict['features'] if 'features' in dict else None



