import os


class AirdScanUtil:

    @staticmethod
    def scanIndexFiles(directoryPath):
        for filename in os.listdir(directoryPath):
            print(filename)
