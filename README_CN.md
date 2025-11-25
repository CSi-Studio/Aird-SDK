# 1 什么是 Aird？

## 1.1 概述

Aird 是一种用于质谱数据存储的新格式。它是一种开源且面向计算的格式，具有可控精度、灵活的索引策略，以及对 _**m/z**_、**强度**和**离子淌度**对的高压缩率。Aird 提供了一种名为 **ComboComp** 的新型压缩器用于 _m/z_ 数据压缩，实现了惊人的压缩率。与 Zlib 相比，Aird 中的 m/z 数据平均降低了约 65%。Aird 是一种计算友好的算法。通过 SIMD 优化，Aird 的解码速度远高于 Zlib。<br/>
Aird SDK 是一个用 Java、C# 和 Python 语言编写的开发工具。它方便开发人员快速读取 Aird 文件中的谱图数据。凭借高性能的读取和出色的压缩率，开发人员可以基于 Aird 开发大量用于数据可视化和分析的应用程序。

Aird 索引文件后缀：.json <br/>
Aird 数据文件后缀：.aird <br/>
Aird 索引文件和 Aird 数据文件应存储在具有相同文件名的同一目录中。

## 1.2 AirdPro：供应商文件的转换客户端

您应该使用 AirdPro 客户端将供应商文件转换为 Aird 格式。<br/>
您可以从 GitHub 下载 AirdPro：<br/>
https://github.com/CSi-Studio/AirdPro/releases/ <br/>
下载后，解压文件，点击 AirdPro.exe 启动 AirdPro 应用程序。AirdPro 是用 C# 编写的，也是一个开源项目。AirdPro 提供了简单的 UI，方便人们快速将供应商文件转换为 Aird 文件。

## 1.3 支持的数据采集方法

- DIA/SWATH
- DDA
- PRM
- DIA_PASEF
- DDA_PASEF

演示代码：参见项目中的 SampleCode.java 或 "如何使用" 章节

## 1.4 引用

1. Lu, M., An, S., Wang, R. et al. Aird: a computation-oriented mass spectrometry data format enables a
   higher compression ratio and less decoding time. BMC Bioinformatics 23, 35 (2022)

2. Wang,J. et al. StackZDPD: a novel encoding scheme for mass spectrometry data optimized for speed and compression
   ratio. Scientific Reports, 12, 5384.(2022)

# 2. 如何导入（Java、C#、Python）

## 2.1 Java SDK 的 Maven 依赖

    <dependency>
        <groupId>net.csibio.aird</groupId>
        <artifactId>aird-sdk</artifactId>
        <version>2.5.1.1</version>
    </dependency>

## 2.2 C# SDK 的 Nuget 包

在 Nuget 包管理器中搜索 "AirdSDK"

## 2.3 Python SDK 的 PyPI 包

    pip install AirdSDK

# 3 领域定义

## 3.1 AirdInfo

| 名称                     | 类型                 | 必需 | 描述                                                                                                                                             |
|--------------------------|----------------------|------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| version                  | String               | 是   | Aird 格式版本                                                                                                                                     |
| versionCode              | Integer              | 是   | Aird 格式版本代码                                                                                                                                |
| engine                   | Integer              | 是   | 压缩引擎类型 (0: 行式压缩, 1: 列式压缩)                                                                                     |
| compressors              | List<Compressor>     | 是   | m/z、强度和淌度数组的压缩策略                                                                                        |
| instruments              | List<Instrument>     | 是   | MS 仪器的通用信息                                                                                                             |
| dataProcessings          | List<DataProcessing> | 否   | 从首次转换为 Aird 格式到创建当前 Aird 实例文档期间应用于数据的任何操作的描述 |
| softwares                | List<Software>       | 否   | 用于转换数据的软件。如果数据已被任何其他程序处理（例如 profile > centroid），也应添加这些程序               |
| parentFiles              | List<ParentFile>     | 否   | 用于生成当前 Aird 文档的所有祖先文件（直到原始采集文件）的路径                                           |
| rangeList                | List<WindowRange>    | 否   | 已根据实验重叠调整的前体 m/z 窗口范围。此字段针对 DIA 和 PRM 类型格式                    |
| indexList                | List<BlockIndex>     | 是   | 质谱数据的索引                                                                                                                    |
| indexStartPtr            | Long                 | 否   | 压缩二进制索引数据的起始位置 (版本代码 >=7)                                                                                       |
| indexEndPtr              | Long                 | 否   | 压缩二进制索引数据的结束位置 (版本代码 >=7)                                                                                         |
| chromatogramIndex        | ChromatogramIndex    | 否   | MRM 采集模式下的色谱图信息                                                                                                       |
| type                     | String               | 是   | Aird 类型。支持的类型：DIA, DDA, PRM, DIA_PASEF, DDA_PASEF, MRM, MSI_MALDI, COMMON                                                                 |
| fileSize                 | Long                 | 是   | Aird 文件和 JSON 文件的文件大小                                                                                                               |
| totalCount               | Long                 | 是   | 总谱图数                                                                                                                                   |
| airdPath                 | String               | 否   | .aird 文件路径                                                                                                                                     |
| activator                | String               | 否   | 激活方法，CID,HCD,ETD,ECD                                                                                                                       |
| energy                   | Float                | 否   | 碰撞能量                                                                                                                                        |
| msType                   | String               | 是   | 质谱类型，PROFILE, CENTROIDED                                                                                                                 |
| rtUnit                   | String               | 是   | rt 单位，始终为秒                                                                                                                                  |
| polarity                 | String               | 是   | 极性类型，POSITIVE, NEGATIVE, NEUTRAL                                                                                                              |
| filterString             | String               | 否   | 谱图选择的过滤字符串                                                                                                                   |
| ignoreZeroIntensityPoint | Boolean              | 是   | 是否忽略强度为 0 的点                                                                                                           |
| mobiInfo                 | MobiInfo             | 否   | 离子淌度信息                                                                                                                                |
| msiInfo                  | MsiInfo              | 否   | MSI（质谱成像）信息                                                                                                            |
| creator                  | String               | 否   | 文件创建者，此字段可在 AirdPro 中设置                                                                                               |
| createDate               | String               | 否   | aird 文件的创建日期                                                                                                                       |
| features                 | String               | 否   | 以 "key:value;key:value" 格式存储的其他特性                                                                                            |
| startTimeStamp           | String               | 否   | 实验开始时间戳                                                                                                                              |

## 3.2 Compressor

| 名称      | 类型         | 必需 | 描述                                           |
|-----------|--------------|------|-------------------------------------------------------|
| target    | String       | 是   | 压缩目标：mz, intensity, mobility, rt                 |
| methods   | List<String> | 是   | 压缩方法列表，按顺序使用，例如 ["VB","Zstd"]         |
| precision | Integer      | 是   | 精度乘数：1000=3位小数，10000=4位小数等              |
| digit     | Integer      | 否   | 用于StackZDPD算法，2^digit = 层数 (仅Python SDK)      |
| byteOrder | String       | 否   | 字节序：LITTLE_ENDIAN(默认), BIG_ENDIAN              |

## 3.3 WindowRange

| 名称     | 类型    | 必需 | 描述                                                  |
|----------|---------|------|--------------------------------------------------------------|
| start    | Double  | 是   | 前体 m/z 起始                                          |
| end      | Double  | 是   | 前体 m/z 结束                                            |
| mz       | Double  | 是   | 前体 m/z                                                |
| charge   | Integer | 否   | 前体电荷，空时为 0                               |
| features | String  | 否   | 以 "key:value;key:value" 格式存储的其他特性 |

## 3.4 BlockIndex

| 名称                | 类型              | 必需 | 描述                                                                                                                          |
|---------------------|-------------------|------|--------------------------------------------------------------------------------------------------------------------------------------|
| level               | Integer           | 是   | 1:MS1, 2:MS2                                                                                                                         |
| startPtr            | Long              | 是   | 块的起始点                                                                                                        |
| endPtr              | Long              | 是   | 块的结束点                                                                                                           |
| num                 | Integer           | 否   | 供应商文件中的扫描号。如果块有 MS2 列表，此字段是相关 MS1 的编号                             |
| rangeList           | List<WindowRange> | 否   | 已根据实验重叠调整的前体 m/z 窗口范围。此字段针对 DIA 和 PRM 类型格式 |
| nums                | List<Integer>     | 否   | 块中的扫描号                                                                                                            |
| rts                 | List<Double>      | 是   | 块中的所有保留时间                                                                                                 |
| tics                | List<Long>        | 否   | 块中每个谱图的总强度                                                                                        |
| injectionTimes      | List<Float>       | 否   | 块中每个谱图的注入时间（仅 C# 和 Java SDK）                                                                  |
| basePeakIntensities | List<Double>      | 是   | 块中每个谱图的总基峰强度                                                                              |
| basePeakMzs         | List<Double>      | 是   | 块中每个谱图的总基峰 mz                                                                                     |
| filterStrings       | List<String>      | 否   | 块中每个谱图的过滤字符串                                                                                          |
| activators          | List<String>      | 否   | 块中每个谱图的激活器                                                                                              |
| energies            | List<Float>       | 否   | 块中每个谱图的能量                                                                                                 |
| polarities          | List<String>      | 否   | 块中每个谱图的极性                                                                                              |
| msTypes             | List<String>      | 否   | 块中每个谱图的 msType                                                                                                |
| tags                | List<Integer>     | 否   | 用于 StackZDPD，每个 mz 点的原始层数（仅 Python SDK）                                                          |
| mzs                 | List<Integer>     | 是   | 每个 m/z 字节大小的大小                                                                                                        |
| ints                | List<Integer>     | 是   | 每个强度字节大小的大小                                                                                                  |
| mobilities          | List<Integer>     | 否   | 每个离子淌度字节大小的大小                                                                                               |
| cvList              | List<List<CV>>    | 否   | PSI 受控词汇表（仅 Python SDK）                                                                                         |
| features            | String            | 否   | 以 "key:value;key:value" 格式存储的其他特性                                                                         |

## 3.5 Instrument

| 名称         | 类型         | 必需 | 描述                                                  |
|--------------|--------------|------|--------------------------------------------------------------|
| manufacturer | String       | 否   | 仪器制造商："ABSciex","Thermo Fisher"           |
| ionisation   | String       | 否   | 电离方式                                                   |
| resolution   | String       | 否   | 分辨率                                                   |
| model        | String       | 否   | 仪器型号                                             |
| source       | List<String> | 否   | 源："electrospray ionization", "electrospray inlet"      |
| analyzer     | List<String> | 否   | 分析器："quadrupole", "orbitrap"                           |
| detector     | List<String> | 否   | 检测器："inductive detector"                               |

## 3.6 DataProcessing

| 名称                 | 类型         | 必需 | 描述                                                                      |
|----------------------|--------------|------|----------------------------------------------------------------------------------|
| processingOperations | List<String> | 否   | 数据处理元素中其他地方未包含的任何其他操作 |

## 3.7 Software

| 名称    | 类型   | 必需 | 描述                                    |
|---------|--------|------|------------------------------------------------|
| name    | String | 是   | 软件名称                              |
| version | String | 否   | 软件版本                           |
| type    | String | 否   | 软件功能类型，如 "acquisition" |

## 3.8 ParentFile

| 名称     | 类型   | 必需 | 描述       |
|----------|--------|------|-------------------|
| name     | String | 是   | 文件名      |
| location | String | 否   | 文件位置 |
| type     | String | 否   | 文件类型     |

## 3.9 MobiInfo

| 名称      | 类型   | 必需 | 描述                               |
|-----------|--------|------|-------------------------------------------|
| dictStart | long   | 是   | aird 中淌度数组的起始位置 |
| dictEnd   | long   | 是   | aird 中淌度数组的结束位置   |
| unit      | String | 否   | 离子淌度单位                         |
| type      | String | 否   | 离子淌度类型，见 MobilityType       |

# 4 API 文档

## 4.1 从目标目录扫描 Aird 文件

```
    List<File> files = AirdScanUtil.scanIndexFiles("E:\\data\\SGS");
    files.forEach(file -> {
        AirdManager.getInstance().load(file.getPath());
    });
```

## 4.2 将 Aird 信息加载到内存中

```
    DIAParser diaParser = new DIAParser("\\FilePath\\file.json");
    DDAParser ddaParser = new DDAParser("\\FilePath\\file.json");
    DDAPasefParser ddaPasefParser = new DDAPasefParser("\\FilePath\\file.json");
    DIAPasefParser diaPasefParser = new DIAPasefParser("\\FilePath\\file.json");
    PRMParser prmParser = new PRMParser("\\FilePath\\file.json");
```

## 4.3 读取 AirdInfo

```
    DDAParser parser1 = new DDAParser(YOUR_AIRD_INDEX_FILE_PATH);
    AirdInfo airdInfo = parser.getAirdInfo();
```

## 4.4 按谱图编号读取谱图

```
    int num = 12
    Spectrum pairs = parser.getSpectrum(num);
```

## 4.5 按保留时间读取谱图

```
    double rt = 12.3456
    Spectrum pairs = parser.getSpectrum(num);
```

## 4.6 逐个读取 DIA/SWATH 窗口块

```
    DIAParser diaParser = new DIAParser("\\FilePath\\file.json");
    AirdInfo airdInfo = diaParser.getAirdInfo();
    airdInfo.getIndexList().forEach(blockIndex -> {
       TreeMap<Double, Spectrum> map = diaParser.getSpectrums(blockIndex); //key 是保留时间
    });
```

## 4.7 将所有数据读入内存

这仅适用于小型 DDA 数据文件（建议 < 200MB）的 DDAParser。将所有谱图读入内存

```
    DDAParser ddaParser = new DDAParser("\\FilePath\\file.json");
    List<DDAMs> cycleList = ddaParser.readAllToMemory();
```

# 5 详细文档

## 5.1 多语言 SDK 文档

### Java SDK 文档
- [中文使用指南](./docs/Java/Java_SDK_Parser_使用指南_中文.md)
- [English Usage Guide](./docs/Java/Java_SDK_Parser_Usage_Guide_English.md)

### C# SDK 文档
- [中文使用指南](./docs/CSharp/CSharp_SDK_Parser_使用指南_中文.md)
- [English Usage Guide](./docs/CSharp/CSharp_SDK_Parser_Usage_Guide_English.md)

### Python SDK 文档
- [中文使用指南](./docs/Python/Python_SDK_Parser_使用指南_中文.md)
- [English Usage Guide](./docs/Python/Python_SDK_Parser_Usage_Guide_English.md)

## 5.2 项目结构

```
Aird-SDK/
├── CSharpSDK/          # C# SDK 源代码
├── JavaSDK/            # Java SDK 源代码
├── PyAirdSDK/          # Python SDK 源代码
├── docs/               # 文档目录
│   ├── Java/           # Java SDK 文档
│   ├── CSharp/         # C# SDK 文档
│   └── Python/         # Python SDK 文档
└── README.md           # 项目总览文档
```

## 5.3 支持的 Parser 类

所有 SDK 都支持以下核心 Parser 类：

### 基础解析器
- **BaseParser** - 所有 Parser 类的基类，提供通用功能

### 数据采集模式解析器
- **DDAParser** - 数据依赖采集（DDA）模式
- **DIAParser** - 数据独立采集（DIA）模式
- **MRMParser** - 多反应监测（MRM）模式
- **PRMParser** - 平行反应监测（PRM）模式

### 高级功能解析器
- **DDAPasefParser** - DDA-PASEF 模式（含离子淌度）
- **DIAPasefParser** - DIA-PASEF 模式（含离子淌度）
- **MSIMaldiParser** - MALDI 质谱成像
- **ColumnParser** - 列式数据解析

# 示例代码

详细示例代码。参见 net.csibio.aird.sample.SampleCode