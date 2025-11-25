# C# SDK Parser 使用指南

## 概述

C# SDK 提供了一系列强大的 Parser 类，用于解析和处理 Aird 格式的质谱数据文件。这些 Parser 类位于 `AirdSDK.Parser` 命名空间中，支持多种质谱数据采集模式，包括 DDA、DIA、MRM、PRM 等。

## 核心 Parser 类

### 1. BaseParser（基础解析器）

BaseParser 是所有 Parser 类的基类，提供了通用的文件操作和数据解析功能。

**主要功能：**
- 文件路径管理和验证
- AirdInfo 元数据加载
- 压缩器配置和管理
- 随机访问文件读取

**构造函数：**
```csharp
// 通过索引文件路径创建解析器
BaseParser parser = new BaseParser("/path/to/index.json");

// 通过索引文件路径和 AirdInfo 创建解析器
BaseParser parser = new BaseParser("/path/to/index.json", airdInfo);

// 通过 Aird 文件路径和压缩器创建解析器
BaseParser parser = new BaseParser("/path/to/file.aird", mzCompressor, intCompressor, mobiCompressor, "DDA");
```

**静态方法：**
```csharp
// 根据文件类型自动选择合适的解析器
BaseParser parser = BaseParser.BuildParser("/path/to/index.json");
```

### 2. DDAParser（DDA 模式解析器）

DDAParser 专门用于解析数据依赖采集（DDA）模式的质谱数据。

**主要方法：**
- `GetMs1Index()` - 获取 MS1 索引
- `GetAllMs2Index()` - 获取所有 MS2 索引
- `GetMs2IndexMap()` - 获取以 parentNum 为键的 MS2 索引映射
- `ReadAllToMemory()` - 一次性加载所有 DDA 数据到内存
- `GetMs1SpectraMap()` - 返回 MS1 谱图的 RT 映射
- `GetSpectraByRtRange()` - 按保留时间范围获取谱图

### 3. DDAPasefParser（DDA-PASEF 模式解析器）

DDAPasefParser 用于解析结合离子淌度分离的 DDA-PASEF 数据。

### 4. DIAParser（DIA 模式解析器）

DIAParser 用于解析数据独立采集（DIA）模式的质谱数据。

### 5. DIAPasefParser（DIA-PASEF 模式解析器）

DIAPasefParser 用于解析结合离子淌度分离的 DIA-PASEF 数据。

### 6. MRMParser（MRM 模式解析器）

MRMParser 专门用于解析多反应监测（MRM）模式的色谱数据。

### 7. PRMParser（PRM 模式解析器）

PRMParser 用于解析平行反应监测（PRM）模式的数据。

### 8. MSIMaldiParser（MALDI 成像解析器）

MSIMaldiParser 用于解析质谱成像（MSI）MALDI 数据。

### 9. ColumnParser（列式解析器）

ColumnParser 提供列式数据解析功能。

## 使用示例

### 基础工作流

```csharp
// 1. 创建解析器实例
DDAParser parser = new DDAParser("/path/to/dda_data.json");

// 2. 获取文件信息
AirdInfo airdInfo = parser.airdInfo;
Console.WriteLine($"文件类型: {airdInfo.type}");

// 3. 读取数据到内存
List<DDAMs> ddaData = parser.ReadAllToMemory();

// 4. 处理谱图数据
foreach (DDAMs ddaMs in ddaData)
{
    double rt = ddaMs.rt;
    Spectrum spectrum = ddaMs.spectrum;
    // 处理每个 MS1 谱图
}

// 5. 释放资源
parser.fs?.Close();
```

### 按保留时间范围查询

```csharp
// 查询保留时间在 10-20 分钟之间的谱图
List<DDAMs> spectra = parser.GetSpectraByRtRange(10.0, 20.0, true);
foreach (DDAMs ddaMs in spectra)
{
    // 处理每个谱图
    double[] mzArray = ddaMs.spectrum.mzs;
    double[] intensityArray = ddaMs.spectrum.ints;
}
```

### 自动选择解析器

```csharp
// 根据文件类型自动选择合适的解析器
BaseParser parser = BaseParser.BuildParser("/path/to/index.json");

if (parser is DDAParser ddaParser)
{
    // 处理 DDA 数据
    List<DDAMs> data = ddaParser.ReadAllToMemory();
}
else if (parser is MRMParser mrmParser)
{
    // 处理 MRM 数据
    // ...
}
```

## 数据模型

### DDAMs 类
表示 DDA 模式下的质谱扫描数据。

**主要属性：**
- `rt` - 保留时间
- `spectrum` - 谱图数据
- `num` - 扫描编号
- `msLevel` - 质谱级别
- `ms2List` - MS2 子列表（仅 MS1）

### Spectrum 类
表示单个质谱谱图。

**主要属性：**
- `mzs` - m/z 数组
- `ints` - 强度数组
- `rt` - 保留时间

### AirdInfo 类
表示 Aird 文件的元数据信息。

**主要属性：**
- `type` - 文件类型（DDA、DIA、MRM 等）
- `indexList` - 索引列表
- `compressorList` - 压缩器列表

## 最佳实践

### 1. 资源管理
```csharp
using (DDAParser parser = new DDAParser(filePath))
{
    // 使用解析器
    var data = parser.ReadAllToMemory();
    // 处理数据
}
// 自动释放资源
```

### 2. 内存优化
对于大型文件，避免一次性加载所有数据：
```csharp
// 分批处理数据
List<DDAMs> spectra = parser.GetSpectraByRtRange(startRt, endRt, false);
```

### 3. 异常处理
```csharp
try
{
    DDAParser parser = new DDAParser(filePath);
    if (parser.airdInfo == null)
    {
        throw new ArgumentException("无效的 Aird 文件");
    }
}
catch (Exception e)
{
    Console.WriteLine($"文件读取错误: {e.Message}");
}
```

## 常见问题

### Q: 如何判断文件是否支持特定解析器？
A: 使用 `BaseParser.BuildParser()` 方法，它会根据文件内容自动选择合适的解析器。

### Q: 如何处理压缩数据？
A: 解析器会自动处理数据解压缩，无需手动干预。

### Q: 如何获取文件的元数据信息？
A: 通过 `parser.airdInfo` 属性获取完整的文件元数据。

## 性能建议

1. **批量处理**：尽量使用批量操作方法，如 `GetSpectraByRtRange()`
2. **内存管理**：大型文件使用流式处理，避免内存溢出
3. **缓存策略**：频繁访问的数据可以适当缓存
4. **并行处理**：多核环境下可以考虑并行处理不同数据块

---

*本文档基于 C# SDK 版本: 1.0.0*