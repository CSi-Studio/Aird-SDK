# Python SDK Parser 使用指南

## 概述

Python SDK 提供了一系列强大的 Parser 类，用于解析和处理 Aird 格式的质谱数据文件。这些 Parser 类位于 `Parser` 模块中，支持多种质谱数据采集模式，包括 DDA、DIA、PRM 等。

## 核心 Parser 类

### 1. BaseParser（基础解析器）

BaseParser 是所有 Parser 类的基类，提供了通用的文件操作和数据解析功能。

**主要功能：**
- 文件路径管理和验证
- AirdInfo 元数据加载
- 压缩器配置和管理
- 随机访问文件读取

**构造函数：**
```python
# 通过索引文件路径创建解析器
parser = BaseParser("/path/to/index.json")
```

**主要方法：**
- `getSpectrum()` - 获取单个谱图
- `getSpectra()` - 获取多个谱图
- `getSpectrumByIndex()` - 通过索引获取谱图
- `getSpectrumByRt()` - 通过保留时间获取谱图
- `getMzs()` - 获取 m/z 数组
- `getInts()` - 获取强度数组
- `getMobilities()` - 获取淌度数组（PASEF 数据）

### 2. DDAParser（DDA 模式解析器）

DDAParser 专门用于解析数据依赖采集（DDA）模式的质谱数据。

**主要方法：**
- `getMs1Index()` - 获取 MS1 索引
- `getAllMs2Index()` - 获取所有 MS2 索引
- `getMs2IndexMap()` - 获取以 parentNum 为键的 MS2 索引映射
- `readAllToMemory()` - 一次性加载所有 DDA 数据到内存
- `getMs1SpectraMap()` - 返回 MS1 谱图的 RT 映射
- `getSpectraByRtRange()` - 按保留时间范围获取谱图

### 3. DIAParser（DIA 模式解析器）

DIAParser 用于解析数据独立采集（DIA）模式的质谱数据。

### 4. PRMParser（PRM 模式解析器）

PRMParser 用于解析平行反应监测（PRM）模式的数据。

## 使用示例

### 基础工作流

```python
# 1. 导入必要的模块
from Parser.DDAParser import DDAParser
from Beans.Common.Spectrum import Spectrum

# 2. 创建解析器实例
parser = DDAParser("/path/to/dda_data.json")

# 3. 获取文件信息
aird_info = parser.airdInfo
print(f"文件类型: {aird_info.type}")

# 4. 读取数据到内存
dda_data = parser.readAllToMemory()

# 5. 处理谱图数据
for dda_ms in dda_data:
    rt = dda_ms.rt
    spectrum = dda_ms.spectrum
    # 处理每个 MS1 谱图
    mz_array = spectrum.mzs
    intensity_array = spectrum.ints

# 6. 释放资源
parser.airdFile.close()
```

### 按保留时间范围查询

```python
# 查询保留时间在 10-20 分钟之间的谱图
spectra = parser.getSpectraByRtRange(10.0, 20.0)
for spectrum in spectra:
    # 处理每个谱图
    mz_array = spectrum.mzs
    intensity_array = spectrum.ints
```

### 获取特定谱图

```python
# 通过索引获取特定谱图
spectrum = parser.getSpectrumByIndex(block_index, 5)  # 获取第6个谱图

# 通过保留时间获取谱图
spectrum = parser.getSpectrumByRt(block_index, rt_list, mz_offsets, int_offsets, 15.5)
```

### 批量获取谱图

```python
# 获取某个数据块的所有谱图
spectra_map = parser.getSpectra(block_index.startPtr, block_index.endPtr, 
                                block_index.rts, block_index.mzs, block_index.ints)

for rt, spectrum in spectra_map.items():
    print(f"保留时间: {rt}")
    print(f"m/z 数量: {len(spectrum.mzs)}")
```

## 数据模型

### Spectrum 类
表示单个质谱谱图。

**主要属性：**
- `mzs` - m/z 数组
- `ints` - 强度数组
- `rt` - 保留时间

**构造函数：**
```python
spectrum = Spectrum(mz_array, intensity_array, retention_time)
```

### AirdInfo 类
表示 Aird 文件的元数据信息。

**主要属性：**
- `type` - 文件类型（DDA、DIA、PRM 等）
- `compressors` - 压缩器列表
- `indexList` - 索引列表

### BlockIndex 类
表示数据块的索引信息。

**主要属性：**
- `startPtr` - 数据块起始位置
- `endPtr` - 数据块结束位置
- `rts` - 保留时间数组
- `mzs` - m/z 偏移量数组
- `ints` - 强度偏移量数组

## 最佳实践

### 1. 资源管理
```python
try:
    parser = DDAParser(file_path)
    # 使用解析器
    data = parser.readAllToMemory()
    # 处理数据
finally:
    if parser.airdFile:
        parser.airdFile.close()
```

### 2. 内存优化
对于大型文件，避免一次性加载所有数据：
```python
# 分批处理数据
spectra = parser.getSpectraByRtRange(start_rt, end_rt)
```

### 3. 异常处理
```python
try:
    parser = DDAParser(file_path)
    if parser.airdInfo is None:
        raise ValueError("无效的 Aird 文件")
except Exception as e:
    print(f"文件读取错误: {e}")
```

### 4. 使用上下文管理器
```python
# 自定义上下文管理器
class AirdParser:
    def __init__(self, file_path):
        self.parser = DDAParser(file_path)
    
    def __enter__(self):
        return self.parser
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.parser.airdFile:
            self.parser.airdFile.close()

# 使用方式
with AirdParser(file_path) as parser:
    data = parser.readAllToMemory()
    # 处理数据
```

## 常见问题

### Q: 如何判断文件是否支持特定解析器？
A: 检查 `parser.airdInfo.type` 属性，根据文件类型选择合适的解析器。

### Q: 如何处理压缩数据？
A: 解析器会自动处理数据解压缩，无需手动干预。

### Q: 如何获取文件的元数据信息？
A: 通过 `parser.airdInfo` 属性获取完整的文件元数据。

### Q: Python SDK 支持哪些压缩算法？
A: 支持多种压缩算法，包括 Zstd、Brotli、Snappy、Zlib 等。

## 性能建议

1. **批量处理**：尽量使用批量操作方法，如 `getSpectra()`
2. **内存管理**：大型文件使用流式处理，避免内存溢出
3. **缓存策略**：频繁访问的数据可以适当缓存
4. **并行处理**：多核环境下可以考虑使用多进程处理不同数据块

## 扩展功能

### 自定义压缩器
```python
from Compressor.ByteComp.ZstdWrapper import ZstdWrapper
from Compressor.IntComp.VarByteWrapper import VarByteWrapper

# 创建自定义压缩器
custom_compressor = Compressor()
custom_compressor.methods = ['VB', 'Zstd']
custom_compressor.precision = 0.001
```

### 数据处理管道
```python
def process_spectrum(spectrum):
    """自定义谱图处理函数"""
    # 过滤低强度信号
    threshold = 100
    filtered_mzs = []
    filtered_ints = []
    
    for mz, intensity in zip(spectrum.mzs, spectrum.ints):
        if intensity > threshold:
            filtered_mzs.append(mz)
            filtered_ints.append(intensity)
    
    return Spectrum(filtered_mzs, filtered_ints, spectrum.rt)

# 应用处理管道
processed_spectra = [process_spectrum(s) for s in spectra]
```

---

*本文档基于 Python SDK 版本: 1.0.0*