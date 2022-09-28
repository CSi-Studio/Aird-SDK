class Software:

    def __init__(self, dict):
        self.name = dict['name'] if 'name' in dict else None
        self.version = dict['version'] if 'version' in dict else None
        self.type = dict['type'] if 'type' in dict else None




