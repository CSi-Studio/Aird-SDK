class WindowRange:

    def __init__(self, dict):
        self.start = dict['start'] if 'start' in dict else None
        self.end = dict['end'] if 'end' in dict else None
        self.mz = dict['mz'] if 'mz' in dict else None
        self.charge = dict['charge'] if 'charge' in dict else None
        self.features = dict['features'] if 'features' in dict else None