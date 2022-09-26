class Instrument:

    def __init__(self, dict):
        self.manufacturer = dict['manufacturer'] if 'manufacturer' in dict else None
        self.ionisation = dict['ionisation'] if 'ionisation' in dict else None
        self.resolution = dict['resolution'] if 'resolution' in dict else None
        self.model = dict['model'] if 'model' in dict else None
        self.source = dict['source'] if 'source' in dict else None
        self.analyzer = dict['analyzer'] if 'analyzer' in dict else None
        self.detector = dict['detector'] if 'detector' in dict else None




