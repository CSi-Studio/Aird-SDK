from Beans.CV import CV
from Beans.WindowRange import WindowRange


class BlockIndex:

    def __init__(self, dict):
        self.level = dict['level'] if 'level' in dict else None
        self.startPtr = dict['startPtr'] if 'startPtr' in dict else None
        self.endPtr = dict['endPtr'] if 'endPtr' in dict else None
        self.num = dict['num'] if 'num' in dict else None
        rangeList = []
        if "rangeList" in dict and len(dict['rangeList']) > 0:
            for rangeDict in dict['rangeList']:
                rangeList.append(WindowRange(rangeDict))
        self.rangeList = rangeList
        self.nums = dict['nums'] if 'nums' in dict else None
        self.rts = dict['rts'] if 'rts' in dict else None
        self.tics = dict['tics'] if 'tics' in dict else None
        self.basePeakIntensities = dict['basePeakIntensities'] if 'basePeakIntensities' in dict else None
        self.basePeakMzs = dict['basePeakMzs'] if 'basePeakMzs' in dict else None
        self.mzs = dict['mzs'] if 'mzs' in dict else None
        self.tags = dict['tags'] if 'tags' in dict else None
        self.ints = dict['ints'] if 'ints' in dict else None
        self.mobilities = dict['mobilities'] if 'mobilities' in dict else None
        cvList = []
        if "cvList" in dict and len(dict['cvList']) > 0:
            for cvDict in dict['cvList']:
                cvList.append(CV(cvDict))
        self.cvList = cvList

        self.features = dict['features'] if 'features' in dict else None

    def getParentNum(self):
        if self.level is 2:
            return self.num
        else:
            return -1
