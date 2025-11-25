# Java SDK Parser 使用指南

## 概述

Java SDK 提供了一系列强大的 Parser 类，用于解析和处理 Aird 格式的质谱数据文件。这些 Parser 类位于 `net.csibio.aird.parser` 包中，支持多种质谱数据采集模式，包括 DDA、DIA、MRM、PRM 等。

## 核心 Parser 类

### 1. BaseParser（基础解析器）

BaseParser 是所有 Parser 类的基类，提供了通用的文件操作和数据解析功能。

**主要功能：**
- 文件路径管理和验证
- AirdInfo 元数据加载
- 压缩器配置和管理
- 随机访问文件读取

**构造函数：**
```java
// 通过文件路径创建解析器
BaseParser parser = new BaseParser("/path/to/file.aird");

// 通过文件路径和压缩器创建解析器
BaseParser parser = new BaseParser("/path/to/file.aird", compressor);
```

### 2. DDAParser（DDA 模式解析器）

DDAParser 专门用于解析数据依赖采集（DDA）模式的质谱数据。

**主要方法：**
- `getMs1Index()` - 获取 MS1 索引
- `getAllMs2Index()` - 获取所有 MS2 索引
- `getMs2IndexMap()` - 获取以 parentNum 为键的 MS2 索引映射
- `readAllToMemory()` - 一次性加载所有 DDA 数据到内存
- `getMs1SpectraMap()` - 返回 MS1 谱图的 RT 映射
- `getSpectraByRtRange()` - 按保留时间范围获取谱图

### 3. DDAPasefParser（DDA-PASEF 模式解析器）

DDAPasefParser 用于解析结合离子淌度分离的 DDA-PASEF 数据。

**主要方法：**
- `getMs1Index()` - 获取 MS1 索引
- `getAllMs2Index()` - 获取所有 MS2 索引
- `getMs2IndexMap()` - 获取 MS2 索引映射
- `readAllToMemory()` - 加载所有 DDA-PASEF 数据到内存
- `getMobilityMzHeatmap()` - 获取淌度-m/z 热图数据

### 4. DIAParser（DIA 模式解析器）

DIAParser 用于解析数据独立采集（DIA）模式的质谱数据。

**构造函数：**
```java
// 通过文件路径创建
DIAParser parser = new DIAParser("/path/to/file.aird");

// 通过文件路径和压缩器创建
DIAParser parser = new DIAParser("/path/to/file.aird", compressor);

// 通过文件路径、压缩器和窗口范围创建
DIAParser parser = new DIAParser("/path/to/file.aird", compressor, windowRange);
```

### 5. DIAPasefParser（DIA-PASEF 模式解析器）

DIAPasefParser 用于解析结合离子淌度分离的 DIA-PASEF 数据。

### 6. MRMParser（MRM 模式解析器）

MRMParser 专门用于解析多反应监测（MRM）模式的色谱数据。

**主要方法：**
- `getChromatogramIndex()` - 获取色谱索引
- `getAllMrmPairs()` - 获取所有 MRM 离子对
- `getChromatogram()` - 获取特定离子对的色谱图
- `getChromatogramsByPrecursorMz()` - 按前体 m/z 获取色谱图

### 7. PRMParser（PRM 模式解析器）

PRMParser 用于解析平行反应监测（PRM）模式的数据。

### 8. MSIMaldiParser（MALDI 成像解析器）

MSIMaldiParser 用于解析质谱成像（MSI）MALDI 数据。

## 使用示例

### 基础工作流

```java
// 1. 创建解析器实例
DDAParser parser = new DDAParser("/path/to/dda_data.aird");

// 2. 获取文件信息
AirdInfo airdInfo = parser.getAirdInfo();
System.out.println("文件类型: " + airdInfo.getAirdType());

// 3. 读取数据到内存
parser.readAllToMemory();

// 4. 处理谱图数据
Map<Double, Spectrum> ms1Spectra = parser.getMs1SpectraMap();
for (Map.Entry<Double, Spectrum> entry : ms1Spectra.entrySet()) {
    Double rt = entry.getKey();
    Spectrum spectrum = entry.getValue();
    // 处理每个 MS1 谱图
}

// 5. 释放资源
parser.close();
```

### 按保留时间范围查询

```java
// 查询保留时间在 10-20 分钟之间的谱图
List<Spectrum> spectra = parser.getSpectraByRtRange(10.0, 20.0);
for (Spectrum spectrum : spectra) {
    // 处理每个谱图
    double[] mzArray = spectrum.getMzs();
    double[] intensityArray = spectrum.getIntensities();
}
```

### 处理 MRM 数据

```java
// 创建 MRM 解析器
MRMParser mrmParser = new MRMParser("/path/to/mrm_data.aird");

// 获取所有 MRM 离子对
List<MrmPair> mrmPairs = mrmParser.getAllMrmPairs();

// 获取特定离子对的色谱图
Chromatogram chromatogram = mrmParser.getChromatogram(
    mrmPairs.get(0).getPrecursorMz(), 
    mrmPairs.get(0).getProductMz()
);
```

## 数据模型

### DDAMs 类
表示 DDA 模式下的质谱扫描数据。

**主要属性：**
- `msLevel` - 质谱级别（1 或 2）
- `rt` - 保留时间
- `precursorMz` - 前体 m/z（仅 MS2）
- `precursorCharge` - 前体电荷（仅 MS2）
- `mzs` - m/z 数组
- `intensities` - 强度数组

### Spectrum 类
表示单个质谱谱图。

**主要方法：**
- `getMzs()` - 获取 m/z 数组
- `getIntensities()` - 获取强度数组
- `getRt()` - 获取保留时间
- `getMsLevel()` - 获取质谱级别

## 最佳实践

### 1. 资源管理
```java
try (DDAParser parser = new DDAParser(filePath)) {
    // 使用解析器
    parser.readAllToMemory();
    // 处理数据
} catch (Exception e) {
    // 异常处理
}
```

### 2. 内存优化
对于大型文件，避免一次性加载所有数据：
```java
// 分批处理数据
List<Spectrum> spectra = parser.getSpectraByRtRange(startRt, endRt);
```

### 3. 异常处理
```java
try {
    DDAParser parser = new DDAParser(filePath);
    if (!parser.isValidFile()) {
        throw new IllegalArgumentException("无效的 Aird 文件");
    }
} catch (IOException e) {
    System.err.println("文件读取错误: " + e.getMessage());
}
```

## 常见问题

### Q: 如何判断文件是否支持特定解析器？
A: 使用 `BaseParser.buildParser()` 方法，它会根据文件内容自动选择合适的解析器。

### Q: 如何处理压缩数据？
A: 解析器会自动处理数据解压缩，无需手动干预。

### Q: 如何获取文件的元数据信息？
A: 通过 `parser.getAirdInfo()` 方法获取完整的文件元数据。

## 性能建议

1. **批量处理**：尽量使用批量操作方法，如 `getSpectraByRtRange()`
2. **内存管理**：大型文件使用流式处理，避免内存溢出
3. **缓存策略**：频繁访问的数据可以适当缓存
4. **并行处理**：多核环境下可以考虑并行处理不同数据块

---

*本文档基于 Java SDK 版本: 1.0.0*