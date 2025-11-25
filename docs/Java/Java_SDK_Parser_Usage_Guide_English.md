# Java SDK Parser Usage Guide

## Overview

The Java SDK provides a comprehensive set of Parser classes for parsing and processing Aird format mass spectrometry data files. These Parser classes are located in the `net.csibio.aird.parser` package and support various mass spectrometry data acquisition modes including DDA, DIA, MRM, PRM, etc.

## Core Parser Classes

### 1. BaseParser (Base Parser)

BaseParser is the base class for all Parser classes, providing common file operations and data parsing functionality.

**Main Features:**
- File path management and validation
- AirdInfo metadata loading
- Compressor configuration and management
- Random access file reading

**Constructors:**
```java
// Create parser with file path
BaseParser parser = new BaseParser("/path/to/file.aird");

// Create parser with file path and compressor
BaseParser parser = new BaseParser("/path/to/file.aird", compressor);
```

### 2. DDAParser (DDA Mode Parser)

DDAParser is specifically designed for parsing Data-Dependent Acquisition (DDA) mode mass spectrometry data.

**Main Methods:**
- `getMs1Index()` - Get MS1 index
- `getAllMs2Index()` - Get all MS2 indices
- `getMs2IndexMap()` - Get MS2 index map keyed by parentNum
- `readAllToMemory()` - Load all DDA data into memory at once
- `getMs1SpectraMap()` - Return MS1 spectra RT mapping
- `getSpectraByRtRange()` - Get spectra by retention time range

### 3. DDAPasefParser (DDA-PASEF Mode Parser)

DDAPasefParser is used for parsing DDA-PASEF data with ion mobility separation.

**Main Methods:**
- `getMs1Index()` - Get MS1 index
- `getAllMs2Index()` - Get all MS2 indices
- `getMs2IndexMap()` - Get MS2 index map
- `readAllToMemory()` - Load all DDA-PASEF data into memory
- `getMobilityMzHeatmap()` - Get mobility-m/z heatmap data

### 4. DIAParser (DIA Mode Parser)

DIAParser is used for parsing Data-Independent Acquisition (DIA) mode mass spectrometry data.

**Constructors:**
```java
// Create with file path
DIAParser parser = new DIAParser("/path/to/file.aird");

// Create with file path and compressor
DIAParser parser = new DIAParser("/path/to/file.aird", compressor);

// Create with file path, compressor and window range
DIAParser parser = new DIAParser("/path/to/file.aird", compressor, windowRange);
```

### 5. DIAPasefParser (DIA-PASEF Mode Parser)

DIAPasefParser is used for parsing DIA-PASEF data with ion mobility separation.

### 6. MRMParser (MRM Mode Parser)

MRMParser is specifically designed for parsing Multiple Reaction Monitoring (MRM) mode chromatography data.

**Main Methods:**
- `getChromatogramIndex()` - Get chromatogram index
- `getAllMrmPairs()` - Get all MRM ion pairs
- `getChromatogram()` - Get chromatogram for specific ion pair
- `getChromatogramsByPrecursorMz()` - Get chromatograms by precursor m/z

### 7. PRMParser (PRM Mode Parser)

PRMParser is used for parsing Parallel Reaction Monitoring (PRM) mode data.

### 8. MSIMaldiParser (MALDI Imaging Parser)

MSIMaldiParser is used for parsing Mass Spectrometry Imaging (MSI) MALDI data.

## Usage Examples

### Basic Workflow

```java
// 1. Create parser instance
DDAParser parser = new DDAParser("/path/to/dda_data.aird");

// 2. Get file information
AirdInfo airdInfo = parser.getAirdInfo();
System.out.println("File type: " + airdInfo.getAirdType());

// 3. Read data into memory
parser.readAllToMemory();

// 4. Process spectrum data
Map<Double, Spectrum> ms1Spectra = parser.getMs1SpectraMap();
for (Map.Entry<Double, Spectrum> entry : ms1Spectra.entrySet()) {
    Double rt = entry.getKey();
    Spectrum spectrum = entry.getValue();
    // Process each MS1 spectrum
}

// 5. Release resources
parser.close();
```

### Query by Retention Time Range

```java
// Query spectra with retention time between 10-20 minutes
List<Spectrum> spectra = parser.getSpectraByRtRange(10.0, 20.0);
for (Spectrum spectrum : spectra) {
    // Process each spectrum
    double[] mzArray = spectrum.getMzs();
    double[] intensityArray = spectrum.getIntensities();
}
```

### Processing MRM Data

```java
// Create MRM parser
MRMParser mrmParser = new MRMParser("/path/to/mrm_data.aird");

// Get all MRM ion pairs
List<MrmPair> mrmPairs = mrmParser.getAllMrmPairs();

// Get chromatogram for specific ion pair
Chromatogram chromatogram = mrmParser.getChromatogram(
    mrmPairs.get(0).getPrecursorMz(), 
    mrmPairs.get(0).getProductMz()
);
```

## Data Models

### DDAMs Class
Represents mass spectrometry scan data in DDA mode.

**Main Properties:**
- `msLevel` - Mass spectrometry level (1 or 2)
- `rt` - Retention time
- `precursorMz` - Precursor m/z (MS2 only)
- `precursorCharge` - Precursor charge (MS2 only)
- `mzs` - m/z array
- `intensities` - Intensity array

### Spectrum Class
Represents a single mass spectrometry spectrum.

**Main Methods:**
- `getMzs()` - Get m/z array
- `getIntensities()` - Get intensity array
- `getRt()` - Get retention time
- `getMsLevel()` - Get mass spectrometry level

## Best Practices

### 1. Resource Management
```java
try (DDAParser parser = new DDAParser(filePath)) {
    // Use parser
    parser.readAllToMemory();
    // Process data
} catch (Exception e) {
    // Exception handling
}
```

### 2. Memory Optimization
For large files, avoid loading all data at once:
```java
// Process data in batches
List<Spectrum> spectra = parser.getSpectraByRtRange(startRt, endRt);
```

### 3. Exception Handling
```java
try {
    DDAParser parser = new DDAParser(filePath);
    if (!parser.isValidFile()) {
        throw new IllegalArgumentException("Invalid Aird file");
    }
} catch (IOException e) {
    System.err.println("File reading error: " + e.getMessage());
}
```

## Frequently Asked Questions

### Q: How to determine if a file supports a specific parser?
A: Use the `BaseParser.buildParser()` method, which automatically selects the appropriate parser based on file content.

### Q: How to handle compressed data?
A: The parser automatically handles data decompression, no manual intervention required.

### Q: How to get file metadata information?
A: Get complete file metadata through `parser.getAirdInfo()` method.

## Performance Recommendations

1. **Batch Processing**: Use batch operation methods like `getSpectraByRtRange()` whenever possible
2. **Memory Management**: Use streaming processing for large files to avoid memory overflow
3. **Caching Strategy**: Cache frequently accessed data appropriately
4. **Parallel Processing**: Consider parallel processing of different data blocks in multi-core environments

---

*This document is based on Java SDK version: 1.0.0*