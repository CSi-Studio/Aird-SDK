# AirdSDK Java 版本

## 概述

AirdSDK Java版本是一个用于处理Aird格式质谱数据的高性能Java库。该SDK提供了多种Parser类，支持不同的质谱采集模式，包括DDA、DIA、MRM、PRM等。

## 项目结构

```
JavaSDK/
├── docs/                          # 文档目录
│   ├── Java_SDK_Parser_使用指南_中文.md     # 中文使用指南
│   └── Java_SDK_Parser_Usage_Guide_English.md  # 英文使用指南
├── examples/                      # 示例代码
│   ├── BasicDDAParserExample.java    # 基础DDA解析器示例
│   ├── DDAPasefParserExample.java    # DDA-PASEF解析器示例
│   └── MRMParserExample.java         # MRM解析器示例
├── src/                          # 源代码
│   ├── main/
│   │   └── java/net/csibio/aird/
│   │       ├── parser/           # Parser类目录
│   │       │   ├── BaseParser.java
│   │       │   ├── DDAParser.java
│   │       │   ├── DDAPasefParser.java
│   │       │   ├── DIAParser.java
│   │       │   ├── DIAPasefParser.java
│   │       │   ├── MRMParser.java
│   │       │   ├── PRMParser.java
│   │       │   └── MSIMaldiParser.java
│   │       └── ...
│   └── test/                     # 测试代码
└── pom.xml                      # Maven配置文件
```

## 快速开始

### 依赖配置

在您的Maven项目中添加以下依赖：

```xml
<dependency>
    <groupId>net.csibio</groupId>
    <artifactId>aird-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 基础使用示例

```java
import net.csibio.aird.parser.DDAParser;
import net.csibio.aird.bean.DDAMs;

public class BasicExample {
    public static void main(String[] args) {
        try {
            // 创建DDA解析器
            DDAParser parser = new DDAParser("path/to/your/aird_index.json");
            
            // 读取所有数据
            List<DDAMs> allSpectra = parser.readAllToMemory();
            
            // 处理数据
            for (DDAMs spectrum : allSpectra) {
                System.out.println("RT: " + spectrum.getRt() + 
                                 ", MS Level: " + spectrum.getMsLevel());
            }
            
            // 关闭资源
            parser.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Parser类概览

| Parser类 | 用途 | 支持的数据类型 |
|---------|------|---------------|
| `DDAParser` | 数据依赖采集(DDA) | MS1, MS2光谱数据 |
| `DDAPasefParser` | DDA-PASEF模式 | 含离子迁移率的DDA数据 |
| `DIAParser` | 数据独立采集(DIA) | DIA光谱数据 |
| `DIAPasefParser` | DIA-PASEF模式 | 含离子迁移率的DIA数据 |
| `MRMParser` | 多反应监测(MRM) | 色谱图数据 |
| `PRMParser` | 平行反应监测(PRM) | PRM数据 |
| `MSIMaldiParser` | MALDI成像 | MALDI成像数据 |

## 主要特性

### 1. 高性能数据读取
- 优化的内存管理
- 支持流式读取和批量读取
- 高效的压缩算法支持

### 2. 多种数据模式支持
- 支持所有主流质谱采集模式
- 包含离子迁移率数据支持
- 色谱图数据处理

### 3. 灵活的数据访问
- 按保留时间范围查询
- 支持选择性数据加载
- 丰富的数据过滤选项

### 4. 完整的元数据支持
- 文件信息获取
- 仪器参数读取
- 数据处理信息

## 详细文档

- [中文使用指南](docs/Java_SDK_Parser_使用指南_中文.md) - 完整的中文使用说明
- [English Usage Guide](docs/Java_SDK_Parser_Usage_Guide_English.md) - Complete English documentation

## 示例代码

项目提供了多个实用的示例代码：

1. [BasicDDAParserExample](examples/BasicDDAParserExample.java) - 基础DDA数据读取示例
2. [DDAPasefParserExample](examples/DDAPasefParserExample.java) - DDA-PASEF数据处理示例
3. [MRMParserExample](examples/MRMParserExample.java) - MRM色谱图分析示例

## 最佳实践

### 1. 资源管理
```java
// 正确做法：使用try-with-resources或手动关闭
DDAParser parser = new DDAParser(indexPath);
try {
    // 使用parser
    List<DDAMs> data = parser.readAllToMemory();
} finally {
    parser.close(); // 确保资源被释放
}
```

### 2. 内存优化
```java
// 对于大文件，使用范围查询而非全量加载
List<DDAMs> spectra = parser.getSpectraByRtRange(startRt, endRt, includeMS2);
```

### 3. 异常处理
```java
try {
    DDAParser parser = new DDAParser(filePath);
    // 数据处理
} catch (FileNotFoundException e) {
    System.err.println("文件不存在: " + filePath);
} catch (Exception e) {
    System.err.println("处理错误: " + e.getMessage());
}
```

## 开发指南

### 构建项目

```bash
# 克隆项目
git clone https://github.com/csibio/aird-sdk-java.git

# 进入项目目录
cd aird-sdk-java/JavaSDK

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package
```

### 添加新功能

1. 在`src/main/java/net/csibio/aird/parser/`目录下创建新的Parser类
2. 继承`BaseParser`基类
3. 实现特定的数据解析逻辑
4. 添加相应的测试用例

## 技术支持

- 文档问题：请查看项目文档
- 技术问题：提交GitHub Issue
- 功能请求：通过GitHub Issues提出

## 许可证

本项目采用Mulan PSL v2许可证。详细信息请查看[LICENSE](LICENSE)文件。

## 版本历史

- v1.0.0: 初始版本发布，包含基础Parser功能

## 贡献

欢迎提交Pull Request和Issue来帮助改进这个项目。