from enum import Enum


class IntCompType(Enum):
    Empty = -1
    VB = 2 # Variable Byte
    BP = 3 # Binary Packing
    FPF256 = 4  # fastpfor256
