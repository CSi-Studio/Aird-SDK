class MobiInfo:
    def __init__(self, dict):
        self.dictStart = dict['dictStart'] if 'dictStart' in dict else None
        self.dictEnd = dict['dictEnd'] if 'dictEnd' in dict else None
        self.unit = dict['unit'] if 'unit' in dict else None
        self.type = dict['type'] if 'type' in dict else None




