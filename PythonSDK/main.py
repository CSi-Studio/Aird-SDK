# This is a sample Python script.
import json
import os.path

from Compressor.IntComp.BinPackingWrapper import BinPackingWrapper
from Compressor.SortedIntComp.IntegratedBinPackingWrapper import IntegratedBinPackingWrapper
from Utils.AirdScanUtil import AirdScanUtil


# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.
    # path = '/mnt/d/AirdTest/ComboComp4dp'
    path = '/mnt/d/AirdTest/ComboComp'
    print(os.path.exists(path))
    fileList = AirdScanUtil.scanIndexFiles(path)
    for filePath in fileList:
        airdPath = AirdScanUtil.getAirdPathByIndexPath(filePath)
        print(airdPath)

    input = [1, 2, 3]
    compressed = BinPackingWrapper().encode(input)
    decompressed = BinPackingWrapper().decode(compressed, 0, len(compressed))
    print(decompressed)

    compressed1 = IntegratedBinPackingWrapper().encode(input)
    decompressed1 = IntegratedBinPackingWrapper().decode(compressed1, 0, len(compressed1))
    print(decompressed1)
    # with open('/mnt/d/AirdTest/ComboComp4dp/File1.json', "r") as indexFile:
    #     airdInfo = json.load(indexFile)
    #     print('JSON Format:', airdInfo)


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
