class Compressor:

    def __init__(self, dict):
        self.target = dict['target'] if 'target' in dict else None
        self.methods = dict['methods'] if 'methods' in dict else None
        self.precision = dict['precision'] if 'precision' in dict else None
        self.digit = dict['digit'] if 'digit' in dict else None
        self.byteOrder = dict['byteOrder'] if 'byteOrder' in dict else "LITTLE_ENDIAN"




