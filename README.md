# 1 What is Aird?

## 1.1 Abstract

Aird is a new format for mass spectrometry data storage. It is an opensource and
computation-oriented format with controllable precision, flexible indexing strategies, and high
compression rate for _**m/z**_, **intensity** and **ion mobility** pairs. Aird provides a novel compressor called
**ComboComp** for _m/z_ data compression,which makes up an amazing compression rate. Compared with Zlib, m/z data is
about
65% lower in the Aird on average. Aird is a computational friendly algorithm. Through SIMD
optimization, the decoding speed of Aird is much higher than that of Zlib. <br/>
Aird SDK is a developer tool written in Java, C# and Python language. It is convenient for developers who want to read
the spectrum data in the Aird file quickly. With the high performance of reading and excellent
compression rate, developer can develop a lot of application based on Aird for data visualization
and analysis.

Aird Index File Suffix: .json <br/>
Aird Data File Suffix: .aird <br/>
Aird Index File and Aird Data File show be stored in the same directory with the same file.

## 1.2 AirdPro: Conversion Client for Vendor Files

You should use the AirdPro client to transfer the vendor files into Aird format.<br/>
You can download the AirdPro from the github: <br/>
https://github.com/CSi-Studio/AirdPro/releases/ <br/>
After downloading, unzip the file, click the AirdPro.exe to start the AirdPro Application AirdPro is
written in C#, it is also an opensource project. Simple UI is provided by AirdPro for people to
convert the vendor file to the Aird file quickly.

## 1.3 Supported Acquisition Methods

- DIA/SWATH
- DDA
- PRM
- DIA_PASEF
- DDA_PASEF

Demo code: see SampleCode.java in the project or in the "How to use" chapter

## 1.4 Citation

1. Lu, M., An, S., Wang, R. et al. Aird: a computation-oriented mass spectrometry data format enables a
   higher compression ratio and less decoding time. BMC Bioinformatics 23, 35 (2022)

2. Wang,J. et al. StackZDPD: a novel encoding scheme for mass spectrometry data optimized for speed and compression
   ratio. Scientific Reports, 12, 5384.(2022)

# 2. How to import (Java, C#, Python)

## 2.1 Maven for Java SDK

    <dependency>
        <groupId>net.csibio.aird</groupId>
        <artifactId>aird-sdk</artifactId>
        <version>2.5.1.1</version>
    </dependency>

## 2.2 Nuget for C# SDK

Search "AirdSDK" in Nuget Package Manager

## 2.3 PyPI for Python SDK

    pip install AirdSDK

# 3 Domain Definition

## 3.1 AirdInfo

| Name                     | Type                 | Required | Description                                                                                                                                             |
|--------------------------|----------------------|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| version                  | String               | True     | Aird format version                                                                                                                                     |
| versionCode              | Integer              | True     | Aird format version code                                                                                                                                |
| engine                   | Integer              | True     | Compression engine type (0: Row Compression, 1: Column Compression)                                                                                     |
| compressors              | List<Compressor>     | True     | The compression strategies for m/z, intensity and mobility array                                                                                        |
| instruments              | List<Instrument>     | True     | General information about the MS instrument                                                                                                             |
| dataProcessings          | List<DataProcessing> | False    | Description of any manipulation (from the first conversion to Aird format until the creation of the current Aird instance document) applied to the data |
| softwares                | List<Software>       | False    | Software used to convert the data. If data has been processed (e.g. profile > centroid) by any additional progs these should be added too               |
| parentFiles              | List<ParentFile>     | False    | Path to all the ancestor files (up to the native acquisition file) used to generate the current Aird document                                           |
| rangeList                | List<WindowRange>    | False    | The precursor m/z window ranges which have been adjusted with experiment overlap. This field is targeted for DIA and PRM type format                    |
| indexList                | List<BlockIndex>     | True     | The index for mass spectrometry data                                                                                                                    |
| indexStartPtr            | Long                 | False    | Start position of compressed binary index data (version code >=7)                                                                                       |
| indexEndPtr              | Long                 | False    | End position of compressed binary index data (version code >=7)                                                                                         |
| chromatogramIndex        | ChromatogramIndex    | False    | Chromatogram information for MRM acquisition mode                                                                                                       |
| type                     | String               | True     | Aird Type. Supported types: DIA, DDA, PRM, DIA_PASEF, DDA_PASEF, MRM, MSI_MALDI, COMMON                                                                 |
| fileSize                 | Long                 | True     | The file size for Aird file and JSON file                                                                                                               |
| totalCount               | Long                 | True     | Total spectrums count                                                                                                                                   |
| airdPath                 | String               | False    | The .aird file path                                                                                                                                     |
| activator                | String               | False    | Activator Method, CID,HCD,ETD,ECD                                                                                                                       |
| energy                   | Float                | False    | Collision Energy                                                                                                                                        |
| msType                   | String               | True     | Mass Spectrum Type, PROFILE, CENTROIDED                                                                                                                 |
| rtUnit                   | String               | True     | rt unit, always second                                                                                                                                  |
| polarity                 | String               | True     | Polarity type, POSITIVE, NEGATIVE, NEUTRAL                                                                                                              |
| filterString             | String               | False    | Filter string for spectrum selection                                                                                                                   |
| ignoreZeroIntensityPoint | Boolean              | True     | Whether ignore the point which intensity is 0                                                                                                           |
| mobiInfo                 | MobiInfo             | False    | ion mobility information                                                                                                                                |
| msiInfo                  | MsiInfo              | False    | MSI (Mass Spectrometry Imaging) information                                                                                                            |
| creator                  | String               | False    | The file creator, this field can be set up in the AirdPro                                                                                               |
| createDate               | String               | False    | The create date for the aird file                                                                                                                       |
| features                 | String               | False    | Some other features stored with "key:value;key:value" format                                                                                            |
| startTimeStamp           | String               | False    | Experiment start timestamp                                                                                                                              |

## 3.2 Compressor

| Name      | Type         | Required | Description                                           |
|-----------|--------------|----------|-------------------------------------------------------|
| target    | String       | True     | Compression target: mz, intensity, mobility, rt      |
| methods   | List<String> | True     | Compression methods in order, e.g. ["VB","Zstd"]     |
| precision | Integer      | True     | Precision multiplier: 1000=3dp, 10000=4dp, etc.      |
| digit     | Integer      | False    | Use for StackZDPD algorithm, 2^digit = layers (Python SDK only) |
| byteOrder | String       | False    | Byte order: LITTLE_ENDIAN(default), BIG_ENDIAN        |

## 3.3 WindowRange

| Name     | Type    | Required | Description                                                  |
|----------|---------|----------|--------------------------------------------------------------|
| start    | Double  | True     | Precursor m/z start                                          |
| end      | Double  | True     | Precursor m/z end                                            |
| mz       | Double  | True     | Precursor m/z                                                |
| charge   | Integer | False    | Precursor charge, 0 when empty                               |
| features | String  | False    | Some other features stored with "key:value;key:value" format |

## 3.4 BlockIndex

| Name                | Type              | Required | Description                                                                                                                          |
|---------------------|-------------------|----------|--------------------------------------------------------------------------------------------------------------------------------------|
| level               | Integer           | True     | 1:MS1, 2:MS2                                                                                                                         |
| startPtr            | Long              | True     | The start point for the block                                                                                                        |
| endPtr              | Long              | True     | The endpoint for the block                                                                                                           |
| num                 | Integer           | False    | The scan number in the vendor file. If a block has a list of MS2, this field is the related MS1's number                             |
| rangeList           | List<WindowRange> | False    | The precursor m/z window ranges which have been adjusted with experiment overlap. This field is targeted for DIA and PRM type format |
| nums                | List<Integer>     | False    | Scan numbers in the block                                                                                                            |
| rts                 | List<Double>      | True     | All the retention times in the block                                                                                                 |
| tics                | List<Long>        | False    | Every Spectrum's total intensity in the block                                                                                        |
| injectionTimes      | List<Float>       | False    | Every Spectrum's injection time in the block (C# and Java SDK only)                                                                  |
| basePeakIntensities | List<Double>      | True     | Every Spectrum's total base peak intensity in the block                                                                              |
| basePeakMzs         | List<Double>      | True     | Every Spectrum's total base peak mz in the block                                                                                     |
| filterStrings       | List<String>      | False    | Every Spectrum's filter string in the block                                                                                          |
| activators          | List<String>      | False    | Every Spectrum's activator in the block                                                                                              |
| energies            | List<Float>       | False    | Every Spectrum's energy in the block                                                                                                 |
| polarities          | List<String>      | False    | Every Spectrum's polarity in the block                                                                                              |
| msTypes             | List<String>      | False    | Every Spectrum's msType in the block                                                                                                |
| tags                | List<Integer>     | False    | Used in StackZDPD, the original layers of every mz point (Python SDK only)                                                          |
| mzs                 | List<Integer>     | True     | Size for every m/z bytes size                                                                                                        |
| ints                | List<Integer>     | True     | Size for every intensity bytes size                                                                                                  |
| mobilities          | List<Integer>     | False    | Size for every ion mobility bytes size                                                                                               |
| cvList              | List<List<CV>>    | False    | PSI Controlled Vocabulary (Python SDK only)                                                                                         |
| features            | String            | False    | Some other features stored with "key:value;key:value" format                                                                         |

## 3.5 Instrument

| Name         | Type         | Required | Description                                                  |
|--------------|--------------|----------|--------------------------------------------------------------|
| manufacturer | String       | False    | Instrument manufacturer: "ABSciex","Thermo Fisher"           |
| ionisation   | String       | False    | Ionisation method                                            |
| resolution   | String       | False    | Resolution                                                   |
| model        | String       | False    | Instrument model                                             |
| source       | List<String> | False    | Source: "electrospray ionization", "electrospray inlet"      |
| analyzer     | List<String> | False    | Analyzer: "quadrupole", "orbitrap"                           |
| detector     | List<String> | False    | Detector: "inductive detector"                               |

## 3.6 DataProcessing

| Name                 | Type         | Required | Description                                                                      |
|----------------------|--------------|----------|----------------------------------------------------------------------------------|
| processingOperations | List<String> | False    | Any additional manipulation not included elsewhere in the dataProcessing element |

## 3.7 Software

| Name    | Type   | Required | Description                                    |
|---------|--------|----------|------------------------------------------------|
| name    | String | True     | The software name                              |
| version | String | False    | The software version                           |
| type    | String | False    | The software function type, like "acquisition" |

## 3.8 ParentFile

| Name     | Type   | Required | Description       |
|----------|--------|----------|-------------------|
| name     | String | True     | The filename      |
| location | String | False    | The file location |
| type     | String | False    | The file type     |

## 3.9 MobiInfo

| Name      | Type   | Required | Description                               |
|-----------|--------|----------|-------------------------------------------|
| dictStart | long   | True     | start position in the aird for mobi array |
| dictEnd   | long   | True     | end position in the aird for mobi array   |
| unit      | String | False    | ion mobility unit                         |
| type      | String | False    | ion mobility type, see MobilityType       |

# 4 API Document

## 4.1 Scan Aird files from target directory

```
    List<File> files = AirdScanUtil.scanIndexFiles("E:\\data\\SGS");
    files.forEach(file -> {
        AirdManager.getInstance().load(file.getPath());
    });
```

## 4.2 Load Aird Info into memory

```
    DIAParser diaParser = new DIAParser("\\FilePath\\file.json");
    DDAParser ddaParser = new DDAParser("\\FilePath\\file.json");
    DDAPasefParser ddaPasefParser = new DDAPasefParser("\\FilePath\\file.json");
    DIAPasefParser diaPasefParser = new DIAPasefParser("\\FilePath\\file.json");
    PRMParser prmParser = new PRMParser("\\FilePath\\file.json");
```

## 4.3 Read AirdInfo

```
    DDAParser parser1 = new DDAParser(YOUR_AIRD_INDEX_FILE_PATH);
    AirdInfo airdInfo = parser.getAirdInfo();
```

## 4.4 Read Spectrum by spectrum number

```
    int num = 12
    Spectrum pairs = parser.getSpectrum(num);
```

## 4.5 Read Spectrum by spectrum number

```
    double rt = 12.3456
    Spectrum pairs = parser.getSpectrum(num);
```

## 4.6 Read DIA/SWATH window block one by one

```
    DIAParser diaParser = new DIAParser("\\FilePath\\file.json");
    AirdInfo airdInfo = diaParser.getAirdInfo();
    airdInfo.getIndexList().forEach(blockIndex -> {
       TreeMap<Double, Spectrum> map = diaParser.getSpectrums(blockIndex); //key is retention time
    });
```

## 4.7 Read All data into memory

This is only for DDAParser with small DDA data file(< 200MB as an advice). Read all spectra into the memory

```
    DDAParser ddaParser = new DDAParser("\\FilePath\\file.json");
    List<DDAMs> cycleList = ddaParser.readAllToMemory();
```

# 5 Detailed Documentation

## 5.1 Multi-language SDK Documentation

### Java SDK Documentation
- [中文使用指南](./docs/Java/Java_SDK_Parser_使用指南_中文.md)
- [English Usage Guide](./docs/Java/Java_SDK_Parser_Usage_Guide_English.md)

### C# SDK Documentation
- [中文使用指南](./docs/CSharp/CSharp_SDK_Parser_使用指南_中文.md)
- [English Usage Guide](./docs/CSharp/CSharp_SDK_Parser_Usage_Guide_English.md)

### Python SDK Documentation
- [中文使用指南](./docs/Python/Python_SDK_Parser_使用指南_中文.md)
- [English Usage Guide](./docs/Python/Python_SDK_Parser_Usage_Guide_English.md)

## 5.2 Project Structure

```
Aird-SDK/
├── CSharpSDK/          # C# SDK Source Code
├── JavaSDK/            # Java SDK Source Code
├── PyAirdSDK/          # Python SDK Source Code
├── docs/               # Documentation Directory
│   ├── Java/           # Java SDK Documentation
│   ├── CSharp/         # C# SDK Documentation
│   └── Python/         # Python SDK Documentation
└── README.md           # Project Overview
```

## 5.3 Supported Parser Classes

All SDKs support the following core Parser classes:

### Base Parsers
- **BaseParser** - Base class for all Parser classes, providing common functionality

### Data Acquisition Mode Parsers
- **DDAParser** - Data-Dependent Acquisition (DDA) mode
- **DIAParser** - Data-Independent Acquisition (DIA) mode
- **MRMParser** - Multiple Reaction Monitoring (MRM) mode
- **PRMParser** - Parallel Reaction Monitoring (PRM) mode

### Advanced Feature Parsers
- **DDAPasefParser** - DDA-PASEF mode (with ion mobility)
- **DIAPasefParser** - DIA-PASEF mode (with ion mobility)
- **MSIMaldiParser** - MALDI imaging
- **ColumnParser** - Column data parsing

# Sample Code

Detail sample code. See net.csibio.aird.sample.SampleCode