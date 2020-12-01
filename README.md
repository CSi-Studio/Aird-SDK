# What is Aird?
Aird is a new format for mass spectrometry data storage. It is an opensource and computation-oriented format with controllable 
precision, flexible indexing strategies, and high compression rate for m/z and intensity pairs. Aird provides a novel compressor 
call ZDPD for m/z data compression,which makes up an amazing compression rate. Compared with Zip, m/z data is about 55% lower 
in the Aird on average. Aird is a computational friendly algorithm. Through SIMD optimization, the decoding speed of Aird is 
much higher than that of Zip. <br/>
Aird SDK is a developer tool written in Java. It is convenient for developer who want to read the spectrum data in the Aird file quickly.
With the high performance of reading and excellent compression rate, developer can develop a lot of application based on Aird for data 
visualization and analysis.

# AirdPro
You should use the AirdPro client to transfer the vendor files into Aird format.<br/>
You can download the AirdPro1.0.1.zip from the FTP server: <br/>
    `server url: ftp://47.254.93.217/AirdPro` <br/>
    `username: ftp` <br/>
    `password: 123456` <br/>
After downloading, unzip the file, click the AirdPro.exe to start the AirdPro Application
AirdPro is written in C#, it is also an opensource project. Simple UI is provided by AirdPro for people to convert the vendor file to the 
Aird file quickly.

# What does Aird format like?
Aird Index File Suffix: .json <br/>
Aird Data File Suffix: .aird <br/>
Aird Index File and Aird Data File show be stored in the same directory with the same file name but with different suffix, so that AirdScanUtil.class can scan both of the two files with the same file name;<br/>
When dealing with Spectra, we advice that you should process with SWATH Window one by one so that we can control the Memory

# Maven
    <dependency>
        <groupId>net.csibio.aird</groupId>
        <artifactId>aird-sdk</artifactId>
        <version>1.0.3</version>
    </dependency>

# Main API Class
 AirdManager is the entry level class. You can also use the Parsers under the parser package.
 Aird-SDK 1.0.X is currently support three types of MS file.
 - DIA/SWATH
 - DDA
 - Common type like mzXML
 
 Demo code: see SampleCode.java in the project
 
# MetaData JSON Schema
https://aird.oss-cn-beijing.aliyuncs.com/AirdMetaData.json <br/>
or refer in the root package of the project: AirdMetaData.json

# Detail Description
##AirdInfo
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
compressors	    |List<Compressor>|True|The compression strategies for m/z and intensity array
rangeList	    |List<WindowRange>|False|The precursor m/z window ranges which have been adjusted with experiment overlap. This field is targeted for DIA and PRM type format
indexList	    |List<BlockIndex>|True|The index for mass spectrometry data
instruments	    |List<Instrument>|True|General information about the MS instrument
dataProcessings	|List<DataProcessing>|False|Description of any manipulation (from the first conversion to Aird format until the creation of the current Aird instance document) applied to the data
softwares	    |List<Software>|False|Software used to convert the data. If data has been processed (e.g. profile > centroid) by any additional progs these should be added too
parentFiles	    |List<ParentFile>|False|Path to all the ancestor files (up to the native acquisition file) used to generate the current Aird document
version	        |String|True|Aird format version
versionCode	    |Integer|True|Aird format version code
type	        |String|True|Aird Type. There are four types now: DIA, DDA, PRM, COMMON
fileSize	    |Long|True|The file size for Aird file and JSON file
totalScanCount	|Long|True|Total spectrums count
airdPath	    |String|False|The .aird file path
creator	        |String|False|The file creator, this field can be set up in the AirdPro
createDate	    |String|False|The create date for the aird file
ignoreZeroIntensityPoint|Boolean|True|Whether ignore the point which intensity is 0
features	    |String|False|Some other features stored with “key:value;key:value” format
##Compressor
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
target|String|True|mz, intensity
methods|List<String>|True|zlib, pFor, log10
precision|Integer|False|10^N, the N means N decimal places for the final data
byteOrder|String|True|LITTLE_ENDIAN, BIG_ENDIAN
##WindowRange
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
start|Double|True|Precursor m/z start
end|Double|True|Precursor m/z end
mz|Double|True|Precursor m/z
features|String|False|Some other features stored with “key:value;key:value” format
##BlockIndex
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
level|Integer|True|1:MS1, 2:MS2
startPtr|Long|True|The start point for the block
endPtr|Long|True|The endpoint for the block
num|Integer|False|The scan number in the vendor file. If a block has a list of MS2, this field is the related MS1’s number
rangeList|List<WindowRange>|False|The precursor m/z window ranges which have been adjusted with experiment overlap. This field is targeted for DIA and PRM type format
nums|List<Integer>|False|Scan numbers in the block
rts|List<Float>|True|All the retention times in the block
mzs|List<Long>|True|COMMON: the start position for every m/z bytes. Others: the size for every m/z bytes size
ints|List<Long>|True|COMMON: the start position for every m/z bytes. Others: the size for every m/z bytes size
features|String|False|Some other features stored with “key:value;key:value” format
##Instrument
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
manufacturer|String|False|Instrument manufacturer: ”ABSciex”,”Thermo Fisher”
ionization|String|False|Ionization
resolution|String|False|Resolution
model|String|False|Instrument model
source|List<String>|False|Source: ”electrospray ionization”, ”electrospray inlet”
analyzer|List<String>|False|Analyzer: “quadrupole”, “orbitrap”
detector|List<String>|False|Detector: ”inductive detector”
features|String|False|Some other features stored with “key:value;key:value” format
##DataProcessing
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
processingOperations|List<String>|False|Any additional manipulation not included elsewhere in the dataProcessing element
##Software 
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
name|String|True|The software name
version|String|False|The software version
##ParentFile
|  Name   | Type  | Required | Description |
|  ----  | ----  | ----  | ----  |
name|String|True|The filename
location|String|False|The file location
type|String|False|The file type

Paper see https://www.biorxiv.org/content/10.1101/2020.10.14.338921v1