# What is Aird?

Aird is a new format for mass spectrometry data storage. It is an opensource and
computation-oriented format with controllable precision, flexible indexing strategies, and high
compression rate for m/z and intensity pairs. Aird provides a novel compressor call ZDPD for m/z
data compression,which makes up an amazing compression rate. Compared with Zip, m/z data is about
55% lower in the Aird on average. Aird is a computational friendly algorithm. Through SIMD
optimization, the decoding speed of Aird is much higher than that of Zip. <br/>
Aird SDK is a developer tool written in Java. It is convenient for developers who want to read the
spectrum data in the Aird file quickly. With the high performance of reading and excellent
compression rate, developer can develop a lot of application based on Aird for data visualization
and analysis.

# Citation

1. Lu, M., An, S., Wang, R. et al. Aird: a computation-oriented mass spectrometry data format enables a
   higher compression ratio and less decoding time. BMC Bioinformatics 23, 35 (2022)

2. Wang,J. et al. StackZDPD: a novel encoding scheme for mass spectrometry data optimized for speed and compression
   ratio. Scientific Reports, 12, 5384.(2022)

# AirdPro

You should use the AirdPro client to transfer the vendor files into Aird format.<br/>
You can download the AirdPro from the github: <br/>
https://github.com/CSi-Studio/AirdPro/releases/ <br/>
After downloading, unzip the file, click the AirdPro.exe to start the AirdPro Application AirdPro is
written in C#, it is also an opensource project. Simple UI is provided by AirdPro for people to
convert the vendor file to the Aird file quickly.

# What does Aird format like?

Aird Index File Suffix: .json <br/>
Aird Data File Suffix: .aird <br/>
Aird Index File and Aird Data File show be stored in the same directory with the same file name but
with different suffix, so that AirdScanUtil class can scan both of the two files with the same file
name;<br/>
When dealing with Spectra, we advise that you should process with SWATH Window one by one so that we
can control the Memory

# Maven for Java SDK

    <dependency>
        <groupId>net.csibio.aird</groupId>
        <artifactId>aird-sdk</artifactId>
        <version>2.0.3.1</version>
    </dependency>

# Nuget for C# SDK

Search "AirdSDK" in Nuget Package Manager

# Main API Class

AirdManager is the entry level class. You can also use the Parsers under the parser package.
Aird-SDK 1.1.X is currently support three types of MS file.

- DIA/SWATH
- DDA
- PRM
- DIA_PASEF
- DDA_PASEF
- Common type like mzXML

Demo code: see SampleCode.java in the project

# MetaData JSON Schema

refer in the root package of the project: AirdMetaData.json

# Detail Description

## AirdInfo

| Name                     | Type                 | Required | Description                                                                                                                                             |
|--------------------------|----------------------|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| version                  | String               | True     | Aird format version                                                                                                                                     |
| versionCode              | Integer              | True     | Aird format version code                                                                                                                                |
| compressors              | List<Compressor>     | True     | The compression strategies for m/z, intensity and mobility array                                                                                        |
| instruments              | List<Instrument>     | True     | General information about the MS instrument                                                                                                             |
| dataProcessings          | List<DataProcessing> | False    | Description of any manipulation (from the first conversion to Aird format until the creation of the current Aird instance document) applied to the data |
| softwares                | List<Software>       | False    | Software used to convert the data. If data has been processed (e.g. profile > centroid) by any additional progs these should be added too               |
| parentFiles              | List<ParentFile>     | False    | Path to all the ancestor files (up to the native acquisition file) used to generate the current Aird document                                           |
| rangeList                | List<WindowRange>    | False    | The precursor m/z window ranges which have been adjusted with experiment overlap. This field is targeted for DIA and PRM type format                    |
| indexList                | List<BlockIndex>     | True     | The index for mass spectrometry data                                                                                                                    |
| type                     | String               | True     | Aird Type. There are four types now: DIA, DDA, PRM, DIA_PASEF, DDA_PASEF, COMMON                                                                        |
| fileSize                 | Long                 | True     | The file size for Aird file and JSON file                                                                                                               |
| totalCount               | Long                 | True     | Total spectrums count                                                                                                                                   |
| airdPath                 | String               | False    | The .aird file path                                                                                                                                     |
| activator                | String               | False    | Activator Method                                                                                                                                        |
| energy                   | Float                | False    | Collision Energy                                                                                                                                        |
| msType                   | String               | True     | Mass Spectrum Type, PROFILE, CENTROIDED                                                                                                                 |
| rtUnit                   | String               | True     | rt unit                                                                                                                                                 |
| polarity                 | String               | True     | Polarity type, POSITIVE, NEGATIVE, NEUTRAL                                                                                                              |
| ignoreZeroIntensityPoint | Boolean              | True     | Whether ignore the point which intensity is 0                                                                                                           |
| mobiInfo                 | MobiInfo             | False    | ion mobility information                                                                                                                                |
| creator                  | String               | False    | The file creator, this field can be set up in the AirdPro                                                                                               |
| createDate               | String               | False    | The create date for the aird file                                                                                                                       |
| features                 | String               | False    | Some other features stored with “key:value;key:value” format                                                                                            |

## Compressor

| Name      | Type         | Required | Description                                           |
|-----------|--------------|----------|-------------------------------------------------------|
| target    | String       | True     | mz, intensity                                         |
| methods   | List<String> | True     | zlib, pFor, log10                                     |
| precision | Integer      | False    | 10^N, the N means N decimal places for the final data |
| digit     | Integer      | False    | Use for Stack-ZDPD algorithm 2^digit = layers         |
| byteOrder | String       | True     | LITTLE_ENDIAN, BIG_ENDIAN                             |

## WindowRange

| Name     | Type    | Required | Description                                                  |
|----------|---------|----------|--------------------------------------------------------------|
| start    | Double  | True     | Precursor m/z start                                          |
| end      | Double  | True     | Precursor m/z end                                            |
| mz       | Double  | True     | Precursor m/z                                                |
| charge   | Integer | False    | Precursor charge, 0 when empty                               |
| features | String  | False    | Some other features stored with “key:value;key:value” format |

## BlockIndex

| Name       | Type              | Required | Description                                                                                                                          |
|------------|-------------------|----------|--------------------------------------------------------------------------------------------------------------------------------------|
| level      | Integer           | True     | 1:MS1, 2:MS2                                                                                                                         |
| startPtr   | Long              | True     | The start point for the block                                                                                                        |
| endPtr     | Long              | True     | The endpoint for the block                                                                                                           |
| num        | Integer           | False    | The scan number in the vendor file. If a block has a list of MS2, this field is the related MS1’s number                             |
| rangeList  | List<WindowRange> | False    | The precursor m/z window ranges which have been adjusted with experiment overlap. This field is targeted for DIA and PRM type format |
| nums       | List<Integer>     | False    | Scan numbers in the block                                                                                                            |
| rts        | List<Float>       | True     | All the retention times in the block                                                                                                 |
| tics       | List<Long>        | False    | Every Spectrum's total intensity in the block                                                                                        |
| mzs        | List<Integer>     | True     | COMMON: the start position for every m/z bytes. Others: the size for every m/z bytes size                                            |
| ints       | List<Integer>     | True     | COMMON: the start position for every intensity bytes. Others: the size for every intensity bytes size                                |
| mobilities | List<Integer>     | False    | COMMON: the start position for every mobility bytes. Others: the size for every mobility bytes size                                  |
| cvList     | List<List<CV>>    | False    | PSI Controlled Vocabulary                                                                                                            |
| features   | String            | False    | Some other features stored with “key:value;key:value” format                                                                         |

## Instrument

| Name         | Type         | Required | Description                                                  |
|--------------|--------------|----------|--------------------------------------------------------------|
| manufacturer | String       | False    | Instrument manufacturer: ”ABSciex”,”Thermo Fisher”           |
| ionization   | String       | False    | Ionization                                                   |
| resolution   | String       | False    | Resolution                                                   |
| model        | String       | False    | Instrument model                                             |
| source       | List<String> | False    | Source: ”electrospray ionization”, ”electrospray inlet”      |
| analyzer     | List<String> | False    | Analyzer: “quadrupole”, “orbitrap”                           |
| detector     | List<String> | False    | Detector: ”inductive detector”                               |
| features     | String       | False    | Some other features stored with “key:value;key:value” format |

## DataProcessing

| Name                 | Type         | Required | Description                                                                      |
|----------------------|--------------|----------|----------------------------------------------------------------------------------|
| processingOperations | List<String> | False    | Any additional manipulation not included elsewhere in the dataProcessing element |

## Software

| Name    | Type   | Required | Description          |
|---------|--------|----------|----------------------|
| name    | String | True     | The software name    |
| version | String | False    | The software version |

## ParentFile

| Name     | Type   | Required | Description       |
|----------|--------|----------|-------------------|
| name     | String | True     | The filename      |
| location | String | False    | The file location |
| type     | String | False    | The file type     |

## MobiInfo

| Name      | Type   | Required | Description                               |
|-----------|--------|----------|-------------------------------------------|
| dictStart | long   | True     | start position in the aird for mobi array |
| dictEnd   | long   | True     | end position in the aird for mobi array   |
| unit      | String | False    | ion mobility unit                         |
| type      | String | False    | ion mobility type, see MobilityType       |

# Sample Code

Detail sample code. See net.csibio.aird.sample.SampleCode

## Scan Aird files from target directory

```
    List<File> files = AirdScanUtil.scanIndexFiles("E:\\data\\SGS");
    files.forEach(file -> {
        AirdManager.getInstance().load(file.getPath());
    });
```

## Load Aird Info into memory

```
    DIAParser diaParser = new DIAParser("\\FilePath\\file.json");
    DDAParser ddaParser = new DDAParser("\\FilePath\\file.json");
    DDAPasefParser ddaPasefParser = new DDAPasefParser("\\FilePath\\file.json");
    DIAPasefParser diaPasefParser = new DIAPasefParser("\\FilePath\\file.json");
    PRMParser prmParser = new PRMParser("\\FilePath\\file.json");
```

## Read AirdInfo

```
    AirdInfo airdInfo = parser.getAirdInfo();
```

## Read Spectrum directly

```
    Spectrum pairs = parser.getSpectrum(0);
```

## Read DIA SWATH block one by one

```
    airdInfo.getIndexList().forEach(blockIndex -> {
       TreeMap<Float, Spectrum> map = diaParser.getSpectrums(blockIndex);
    });
```

## Read DDA MsCycle

Read all spectra into the memory

```
    List<DDAMs> cycleList = ddaParser.readAllToMemory();
```

