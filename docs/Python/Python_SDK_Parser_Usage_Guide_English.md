# Python SDK Parser Usage Guide

## Overview

The Python SDK provides a comprehensive set of Parser classes for parsing and processing Aird format mass spectrometry data files. These Parser classes are located in the `Parser` module and support various mass spectrometry data acquisition modes including DDA, DIA, PRM, etc.

## Core Parser Classes

### 1. BaseParser (Base Parser)

BaseParser is the base class for all Parser classes, providing common file operations and data parsing functionality.

**Main Features:**
- File path management and validation
- AirdInfo metadata loading
- Compressor configuration and management
- Random access file reading

**Constructor:**
```python
# Create parser with index file path
parser = BaseParser("/path/to/index.json")
```

**Main Methods:**
- `getSpectrum()` - Get single spectrum
- `getSpectra()` - Get multiple spectra
- `getSpectrumByIndex()` - Get spectrum by index
- `getSpectrumByRt()` - Get spectrum by retention time
- `getMzs()` - Get m/z array
- `getInts()` - Get intensity array
- `getMobilities()` - Get mobility array (PASEF data)

### 2. DDAParser (DDA Mode Parser)

DDAParser is specifically designed for parsing Data-Dependent Acquisition (DDA) mode mass spectrometry data.

**Main Methods:**
- `getMs1Index()` - Get MS1 index
- `getAllMs2Index()` - Get all MS2 indices
- `getMs2IndexMap()` - Get MS2 index map keyed by parentNum
- `readAllToMemory()` - Load all DDA data into memory at once
- `getMs1SpectraMap()` - Return MS1 spectra RT mapping
- `getSpectraByRtRange()` - Get spectra by retention time range

### 3. DIAParser (DIA Mode Parser)

DIAParser is used for parsing Data-Independent Acquisition (DIA) mode mass spectrometry data.

### 4. PRMParser (PRM Mode Parser)

PRMParser is used for parsing Parallel Reaction Monitoring (PRM) mode data.

## Usage Examples

### Basic Workflow

```python
# 1. Import necessary modules
from Parser.DDAParser import DDAParser
from Beans.Common.Spectrum import Spectrum

# 2. Create parser instance
parser = DDAParser("/path/to/dda_data.json")

# 3. Get file information
aird_info = parser.airdInfo
print(f"File type: {aird_info.type}")

# 4. Read data into memory
dda_data = parser.readAllToMemory()

# 5. Process spectrum data
for dda_ms in dda_data:
    rt = dda_ms.rt
    spectrum = dda_ms.spectrum
    # Process each MS1 spectrum
    mz_array = spectrum.mzs
    intensity_array = spectrum.ints

# 6. Release resources
parser.airdFile.close()
```

### Query by Retention Time Range

```python
# Query spectra with retention time between 10-20 minutes
spectra = parser.getSpectraByRtRange(10.0, 20.0)
for spectrum in spectra:
    # Process each spectrum
    mz_array = spectrum.mzs
    intensity_array = spectrum.ints
```

### Get Specific Spectrum

```python
# Get specific spectrum by index
spectrum = parser.getSpectrumByIndex(block_index, 5)  # Get 6th spectrum

# Get spectrum by retention time
spectrum = parser.getSpectrumByRt(block_index, rt_list, mz_offsets, int_offsets, 15.5)
```

### Batch Spectrum Retrieval

```python
# Get all spectra from a data block
spectra_map = parser.getSpectra(block_index.startPtr, block_index.endPtr, 
                                block_index.rts, block_index.mzs, block_index.ints)

for rt, spectrum in spectra_map.items():
    print(f"Retention time: {rt}")
    print(f"Number of m/z values: {len(spectrum.mzs)}")
```

## Data Models

### Spectrum Class
Represents a single mass spectrometry spectrum.

**Main Properties:**
- `mzs` - m/z array
- `ints` - Intensity array
- `rt` - Retention time

**Constructor:**
```python
spectrum = Spectrum(mz_array, intensity_array, retention_time)
```

### AirdInfo Class
Represents Aird file metadata information.

**Main Properties:**
- `type` - File type (DDA, DIA, PRM, etc.)
- `compressors` - Compressor list
- `indexList` - Index list

### BlockIndex Class
Represents data block index information.

**Main Properties:**
- `startPtr` - Data block start position
- `endPtr` - Data block end position
- `rts` - Retention time array
- `mzs` - m/z offset array
- `ints` - Intensity offset array

## Best Practices

### 1. Resource Management
```python
try:
    parser = DDAParser(file_path)
    # Use parser
    data = parser.readAllToMemory()
    # Process data
finally:
    if parser.airdFile:
        parser.airdFile.close()
```

### 2. Memory Optimization
For large files, avoid loading all data at once:
```python
# Process data in batches
spectra = parser.getSpectraByRtRange(start_rt, end_rt)
```

### 3. Exception Handling
```python
try:
    parser = DDAParser(file_path)
    if parser.airdInfo is None:
        raise ValueError("Invalid Aird file")
except Exception as e:
    print(f"File reading error: {e}")
```

### 4. Using Context Manager
```python
# Custom context manager
class AirdParser:
    def __init__(self, file_path):
        self.parser = DDAParser(file_path)
    
    def __enter__(self):
        return self.parser
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.parser.airdFile:
            self.parser.airdFile.close()

# Usage
with AirdParser(file_path) as parser:
    data = parser.readAllToMemory()
    # Process data
```

## Frequently Asked Questions

### Q: How to determine if a file supports a specific parser?
A: Check the `parser.airdInfo.type` property and select the appropriate parser based on file type.

### Q: How to handle compressed data?
A: The parser automatically handles data decompression, no manual intervention required.

### Q: How to get file metadata information?
A: Get complete file metadata through the `parser.airdInfo` property.

### Q: What compression algorithms does Python SDK support?
A: Supports various compression algorithms including Zstd, Brotli, Snappy, Zlib, etc.

## Performance Recommendations

1. **Batch Processing**: Use batch operation methods like `getSpectra()` whenever possible
2. **Memory Management**: Use streaming processing for large files to avoid memory overflow
3. **Caching Strategy**: Cache frequently accessed data appropriately
4. **Parallel Processing**: Consider using multiprocessing for different data blocks in multi-core environments

## Extended Features

### Custom Compressor
```python
from Compressor.ByteComp.ZstdWrapper import ZstdWrapper
from Compressor.IntComp.VarByteWrapper import VarByteWrapper

# Create custom compressor
custom_compressor = Compressor()
custom_compressor.methods = ['VB', 'Zstd']
custom_compressor.precision = 0.001
```

### Data Processing Pipeline
```python
def process_spectrum(spectrum):
    """Custom spectrum processing function"""
    # Filter low intensity signals
    threshold = 100
    filtered_mzs = []
    filtered_ints = []
    
    for mz, intensity in zip(spectrum.mzs, spectrum.ints):
        if intensity > threshold:
            filtered_mzs.append(mz)
            filtered_ints.append(intensity)
    
    return Spectrum(filtered_mzs, filtered_ints, spectrum.rt)

# Apply processing pipeline
processed_spectra = [process_spectrum(s) for s in spectra]
```

---

*This document is based on Python SDK version: 1.0.0*