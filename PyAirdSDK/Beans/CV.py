class CV:

    def __init__(self, dict):
        self.cvid = dict['cvid'] if 'cvid' in dict else None
        self.value = dict['value'] if 'value' in dict else None
        self.units = dict['units'] if 'units' in dict else None

