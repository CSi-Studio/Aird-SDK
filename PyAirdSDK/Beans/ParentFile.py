class ParentFile:

    def __init__(self, dict):
        self.name = dict['name'] if 'name' in dict else None
        self.location = dict['location'] if 'location' in dict else None
        self.type = dict['type'] if 'type' in dict else None




