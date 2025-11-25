# C# SDK Parser Usage Guide

## Overview

The C# SDK provides a comprehensive set of Parser classes for parsing and processing Aird format mass spectrometry data files. These Parser classes are located in the `AirdSDK.Parser` namespace and support various mass spectrometry data acquisition modes including DDA, DIA, MRM, PRM, etc.

## Core Parser Classes

### 1. BaseParser (Base Parser)

BaseParser is the base class for all Parser classes, providing common file operations and data parsing functionality.

**Main Features:**
- File path management and validation
- AirdInfo metadata loading
- Compressor configuration and management
- Random access file reading

**Constructors:**
```csharp
// Create parser with index file path
BaseParser parser = new BaseParser("/path/to/index.json");

// Create parser with index file path and AirdInfo
BaseParser parser = new BaseParser("/path/to/index.json", airdInfo);

// Create parser with Aird file path and compressors
BaseParser parser = new BaseParser("/path/to/file.aird", mzCompressor, intCompressor, mobiCompressor, "DDA");
```

**Static Methods:**
```csharp
// Automatically select appropriate parser based on file type
BaseParser parser = BaseParser.BuildParser("/path/to/index.json");
```

### 2. DDAParser (DDA Mode Parser)

DDAParser is specifically designed for parsing Data-Dependent Acquisition (DDA) mode mass spectrometry data.

**Main Methods:**
- `GetMs1Index()` - Get MS1 index
- `GetAllMs2Index()` - Get all MS2 indices
- `GetMs2IndexMap()` - Get MS2 index map keyed by parentNum
- `ReadAllToMemory()` - Load all DDA data into memory at once
- `GetMs1SpectraMap()` - Return MS1 spectra RT mapping
- `GetSpectraByRtRange()` - Get spectra by retention time range

### 3. DDAPasefParser (DDA-PASEF Mode Parser)

DDAPasefParser is used for parsing DDA-PASEF data with ion mobility separation.

### 4. DIAParser (DIA Mode Parser)

DIAParser is used for parsing Data-Independent Acquisition (DIA) mode mass spectrometry data.

### 5. DIAPasefParser (DIA-PASEF Mode Parser)

DIAPasefParser is used for parsing DIA-PASEF data with ion mobility separation.

### 6. MRMParser (MRM Mode Parser)

MRMParser is specifically designed for parsing Multiple Reaction Monitoring (MRM) mode chromatography data.

### 7. PRMParser (PRM Mode Parser)

PRMParser is used for parsing Parallel Reaction Monitoring (PRM) mode data.

### 8. MSIMaldiParser (MALDI Imaging Parser)

MSIMaldiParser is used for parsing Mass Spectrometry Imaging (MSI) MALDI data.

### 9. ColumnParser (Column Parser)

ColumnParser provides column-based data parsing functionality.

## Usage Examples

### Basic Workflow

```csharp
// 1. Create parser instance
DDAParser parser = new DDAParser("/path/to/dda_data.json");

// 2. Get file information
AirdInfo airdInfo = parser.airdInfo;
Console.WriteLine($"File type: {airdInfo.type}");

// 3. Read data into memory
List<DDAMs> ddaData = parser.ReadAllToMemory();

// 4. Process spectrum data
foreach (DDAMs ddaMs in ddaData)
{
    double rt = ddaMs.rt;
    Spectrum spectrum = ddaMs.spectrum;
    // Process each MS1 spectrum
}

// 5. Release resources
parser.fs?.Close();
```

### Query by Retention Time Range

```csharp
// Query spectra with retention time between 10-20 minutes
List<DDAMs> spectra = parser.GetSpectraByRtRange(10.0, 20.0, true);
foreach (DDAMs ddaMs in spectra)
{
    // Process each spectrum
    double[] mzArray = ddaMs.spectrum.mzs;
    double[] intensityArray = ddaMs.spectrum.ints;
}
```

### Automatic Parser Selection

```csharp
// Automatically select appropriate parser based on file type
BaseParser parser = BaseParser.BuildParser("/path/to/index.json");

if (parser is DDAParser ddaParser)
{
    // Process DDA data
    List<DDAMs> data = ddaParser.ReadAllToMemory();
}
else if (parser is MRMParser mrmParser)
{
    // Process MRM data
    // ...
}
```

## Data Models

### DDAMs Class
Represents mass spectrometry scan data in DDA mode.

**Main Properties:**
- `rt` - Retention time
- `spectrum` - Spectrum data
- `num` - Scan number
- `msLevel` - Mass spectrometry level
- `ms2List` - MS2 sublist (MS1 only)

### Spectrum Class
Represents a single mass spectrometry spectrum.

**Main Properties:**
- `mzs` - m/z array
- `ints` - Intensity array
- `rt` - Retention time

### AirdInfo Class
Represents Aird file metadata information.

**Main Properties:**
- `type` - File type (DDA, DIA, MRM, etc.)
- `indexList` - Index list
- `compressorList` - Compressor list

## Best Practices

### 1. Resource Management
```csharp
using (DDAParser parser = new DDAParser(filePath))
{
    // Use parser
    var data = parser.ReadAllToMemory();
    // Process data
}
// Resources automatically released
```

### 2. Memory Optimization
For large files, avoid loading all data at once:
```csharp
// Process data in batches
List<DDAMs> spectra = parser.GetSpectraByRtRange(startRt, endRt, false);
```

### 3. Exception Handling
```csharp
try
{
    DDAParser parser = new DDAParser(filePath);
    if (parser.airdInfo == null)
    {
        throw new ArgumentException("Invalid Aird file");
    }
}
catch (Exception e)
{
    Console.WriteLine($"File reading error: {e.Message}");
}
```

## Frequently Asked Questions

### Q: How to determine if a file supports a specific parser?
A: Use the `BaseParser.BuildParser()` method, which automatically selects the appropriate parser based on file content.

### Q: How to handle compressed data?
A: The parser automatically handles data decompression, no manual intervention required.

### Q: How to get file metadata information?
A: Get complete file metadata through the `parser.airdInfo` property.

## Performance Recommendations

1. **Batch Processing**: Use batch operation methods like `GetSpectraByRtRange()` whenever possible
2. **Memory Management**: Use streaming processing for large files to avoid memory overflow
3. **Caching Strategy**: Cache frequently accessed data appropriately
4. **Parallel Processing**: Consider parallel processing of different data blocks in multi-core environments

---

*This document is based on C# SDK version: 1.0.0*