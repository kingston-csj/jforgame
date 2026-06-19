# jforgame-data

`jforgame-data` 是配置数据功能模块，负责提供配置表读取、容器装载、通用常量注入和数据校验等核心能力。

## 模块定位

- 不依赖 Spring Boot，只依赖 Spring Framework 基础能力
- 不负责自动装配，自动装配由 `jforgame-data-spring-boot-starter` 提供
- 运行期配置通过 `ResourceOptions` 表达，而不是 `@ConfigurationProperties` 绑定对象

## 核心功能

### 1. 配置表管理

通过注解驱动的数据绑定机制，支持 CSV、Excel、JSON 三种数据源格式：

| 数据源 | 实现类 | 说明 |
|--------|--------|------|
| CSV | `CsvDataReader` | 基于 Apache Commons CSV 解析 |
| Excel | `ExcelDataReader` | 基于 Apache POI 解析 |
| JSON | `JsonDataReader` | 基于 Jackson 解析 |

### 2. 注解体系

| 注解 | 作用范围 | 说明 |
|------|----------|------|
| `@DataTable` | 类 | 标记类为配置表，可指定表名 |
| `@Id` | 字段 | 标记主键字段 |
| `@Index` | 字段/方法 | 标记索引字段，支持唯一索引 |
| `@ForeignKey` | 字段 | 定义外键约束，引用其他配置表 |

### 3. 数据容器

`Container<K, V>` 是配置数据的内存容器，提供以下查询能力：

- `getRecordById(id)` - 根据主键查询
- `getRecordsByIndex(name, value)` - 根据索引查询列表
- `getUniqueRecordByIndex(name, value)` - 根据唯一索引查询
- `getAllRecords()` - 获取所有记录
- `getAllKeys()` - 获取所有主键

支持继承 `Container` 实现二级缓存，在 `afterLoad()` 方法中初始化缓存数据。

### 4. 通用常量注入

通过 `@CommonConfig` 注解将通用常量表的配置项注入到 Spring Bean 的字段中：

```java
@Service
public class PlayerService {
    
    @CommonConfig("maxLevel")
    private int maxLevel;
    
    @CommonConfig(value = "rewardIds", parser = IntArrayConfigValueParser.class)
    private int[] rewardIds;
}
```

支持自定义 `ConfigValueParser` 实现复杂类型转换。

### 5. 数据校验

提供两种数据完整性校验器：

| 校验器 | 说明 |
|--------|------|
| `ForeignKeyValidator` | 外键约束检查，确保引用数据存在 |
| `CustomValidator` | 自定义校验，通过 `Container.validate()` 实现 |

### 6. 热重载支持

通过发布 `ConfigReloadEvent` 事件实现配置表热更新：

```java
applicationEventPublisher.publishEvent(new ConfigReloadEvent("playerLevel"));
```

## 配置对象

`ResourceOptions` 是模块对外使用的普通配置对象：

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `location` | `csv/` | 资源根目录 |
| `suffix` | `.csv` | 配置文件后缀 |
| `tableScanPath` | - | 配置实体扫描路径 |
| `containerScanPath` | - | 配置容器扫描路径 |
| `commonTableName` | `common` | 通用常量表名称 |

在 Spring Boot 环境下，starter 模块中的 `ResourceProperties` 会负责读取 `jforgame.data.*` 配置，并转换为 `ResourceOptions` 注入到 `DataManager`。

## 核心类说明

### DataManager

数据管理器，是模块对外暴露的唯一 API，实现 `DataRepository` 接口：

```java
DataManager dataManager = new DataManager(options, dataReader);
dataManager.init();

// 查询接口
PlayerLevel level = dataManager.queryById(PlayerLevel.class, 1);
List<PlayerLevel> levels = dataManager.queryAll(PlayerLevel.class);
List<PlayerLevel> levelsByType = dataManager.queryByIndex(PlayerLevel.class, "type", 1);
```

### TableDefinition

配置表元数据定义，包含：

- 主键元信息 `ColumnMeta`
- 索引元信息 `IndexMeta`
- 配置表名称

### Container

配置数据容器，使用 `LinkedHashMap` 保证记录顺序：

- 主键映射：`Map<K, V>`
- 索引映射：`Map<String, List<V>>`（key 格式为 `name@value`）

## 使用示例

### 定义配置表实体

```java
@DataTable(name = "playerLevel")
public class PlayerLevel {
    
    @Id
    private int id;
    
    @Index(name = "type")
    private int type;
    
    @ForeignKey(refer = Reward.class)
    private int rewardId;
    
    private int exp;
}
```

### 定义二级缓存容器

```java
public class PlayerLevelContainer extends Container<Integer, PlayerLevel> {
    
    private Map<Integer, List<PlayerLevel>> typeCache;
    
    @Override
    public void afterLoad() {
        typeCache = new HashMap<>();
        for (PlayerLevel level : getAllRecords()) {
            typeCache.computeIfAbsent(level.getType(), k -> new ArrayList<>()).add(level);
        }
    }
    
    public List<PlayerLevel> getByType(int type) {
        return typeCache.getOrDefault(type, Collections.emptyList());
    }
}
```

## 包结构

```
jforgame.data
├── annotation          # 注解定义
│   ├── DataTable       # 配置表标记
│   ├── ForeignKey      # 外键约束
│   ├── Id              # 主键标记
│   └── Index           # 索引标记
├── common              # 通用常量注入
│   ├── CommonConfig    # 注入注解
│   ├── CommonContainer # 常量容器
│   ├── CommonData      # 常量实体
│   └── ConfigValueParser # 值解析器
├── convertor           # 类型转换器
│   ├── ArrayTypeConvertor
│   ├── CollectionTypeConvertor
│   └── MapTypeConvertor
├── event               # 事件定义
│   └ ConfigReloadEvent
├── exception           # 异常定义
│   ├── DataValidateException
│   └ ForeignKeyConstraintException
├── reader              # 数据读取器
│   ├── BaseDataReader
│   ├── CsvDataReader
│   ├── ExcelDataReader
│   └ JsonDataReader
├── validate            # 数据校验器
│   ├── CustomValidator
│   ├── ForeignKeyValidator
├── Container           # 配置容器
├── DataManager         # 数据管理器
├── DataRepository      # 查询接口
├── ResourceOptions     # 配置选项
└── TableDefinition     # 表定义
```

## 依赖关系

```
jforgame-data
    ├── jforgame-commons (反射工具、类扫描)
    ├── spring-core (ConversionService)
    ├── apache-commons-csv (CSV解析)
    ├── apache-poi (Excel解析)
    └── jackson (JSON解析)
```