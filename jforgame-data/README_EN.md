# jforgame-data

`jforgame-data` is a configuration data module that provides core capabilities including configuration table reading, container loading, common constant injection, and data validation.

## Module Positioning

- Does not depend on Spring Boot, only depends on Spring Framework basic capabilities
- Does not handle auto-configuration, which is provided by `jforgame-data-spring-boot-starter`
- Runtime configuration is expressed through `ResourceOptions`, not `@ConfigurationProperties` binding objects

## Core Features

### 1. Configuration Table Management

Annotation-driven data binding mechanism supporting CSV, Excel, and JSON data source formats:

| Data Source | Implementation | Description |
|-------------|----------------|-------------|
| CSV | `CsvDataReader` | Based on Apache Commons CSV parsing |
| Excel | `ExcelDataReader` | Based on Apache POI parsing |
| JSON | `JsonDataReader` | Based on Jackson parsing |

### 2. Annotation System

| Annotation | Scope | Description |
|------------|-------|-------------|
| `@DataTable` | Class | Marks a class as a configuration table, can specify table name |
| `@Id` | Field | Marks primary key field |
| `@Index` | Field/Method | Marks index field, supports unique index |
| `@ForeignKey` | Field | Defines foreign key constraint, references other configuration tables |

### 3. Data Container

`Container<K, V>` is the in-memory container for configuration data, providing the following query capabilities:

- `getRecordById(id)` - Query by primary key
- `getRecordsByIndex(name, value)` - Query list by index
- `getUniqueRecordByIndex(name, value)` - Query by unique index
- `getAllRecords()` - Get all records
- `getAllKeys()` - Get all primary keys

Supports extending `Container` to implement secondary cache, initializing cache data in `afterLoad()` method.

### 4. Common Constant Injection

Injects configuration items from the common constant table into Spring Bean fields using `@CommonConfig` annotation:

```java
@Service
public class PlayerService {
    
    @CommonConfig("maxLevel")
    private int maxLevel;
    
    @CommonConfig(value = "rewardIds", parser = IntArrayConfigValueParser.class)
    private int[] rewardIds;
}
```

Supports custom `ConfigValueParser` for complex type conversion.

### 5. Data Validation

Provides two data integrity validators:

| Validator | Description |
|-----------|-------------|
| `ForeignKeyValidator` | Foreign key constraint check, ensures referenced data exists |
| `CustomValidator` | Custom validation, implemented through `Container.validate()` |

### 6. Hot Reload Support

Implements configuration table hot update by publishing `ConfigReloadEvent`:

```java
applicationEventPublisher.publishEvent(new ConfigReloadEvent("playerLevel"));
```

## Configuration Object

`ResourceOptions` is the plain configuration object used by the module:

| Property | Default | Description |
|----------|---------|-------------|
| `location` | `csv/` | Resource root directory |
| `suffix` | `.csv` | Configuration file suffix |
| `tableScanPath` | - | Configuration entity scan path |
| `containerScanPath` | - | Configuration container scan path |
| `commonTableName` | `common` | Common constant table name |

In Spring Boot environment, `ResourceProperties` in the starter module reads `jforgame.data.*` configuration and converts it to `ResourceOptions` injected into `DataManager`.

## Core Classes

### DataManager

Data manager, the only externally exposed API, implements `DataRepository` interface:

```java
DataManager dataManager = new DataManager(options, dataReader);
dataManager.init();

// Query interfaces
PlayerLevel level = dataManager.queryById(PlayerLevel.class, 1);
List<PlayerLevel> levels = dataManager.queryAll(PlayerLevel.class);
List<PlayerLevel> levelsByType = dataManager.queryByIndex(PlayerLevel.class, "type", 1);
```

### TableDefinition

Configuration table metadata definition, containing:

- Primary key metadata `ColumnMeta`
- Index metadata `IndexMeta`
- Configuration table name

### Container

Configuration data container, uses `LinkedHashMap` to preserve record order:

- Primary key mapping: `Map<K, V>`
- Index mapping: `Map<String, List<V>>` (key format is `name@value`)

## Usage Examples

### Define Configuration Table Entity

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

### Define Secondary Cache Container

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

## Package Structure

```
jforgame.data
├── annotation          # Annotation definitions
│   ├── DataTable       # Configuration table marker
│   ├── ForeignKey      # Foreign key constraint
│   ├── Id              # Primary key marker
│   └── Index           # Index marker
├── common              # Common constant injection
│   ├── CommonConfig    # Injection annotation
│   ├── CommonContainer # Constant container
│   ├── CommonData      # Constant entity
│   └── ConfigValueParser # Value parser
├── convertor           # Type converters
│   ├── ArrayTypeConvertor
│   ├── CollectionTypeConvertor
│   └── MapTypeConvertor
├── event               # Event definitions
│   └ ConfigReloadEvent
├── exception           # Exception definitions
│   ├── DataValidateException
│   └ ForeignKeyConstraintException
├── reader              # Data readers
│   ├── BaseDataReader
│   ├── CsvDataReader
│   ├── ExcelDataReader
│   └ JsonDataReader
├── validate            # Data validators
│   ├── CustomValidator
│   ├── ForeignKeyValidator
├── Container           # Configuration container
├── DataManager         # Data manager
├── DataRepository      # Query interface
├── ResourceOptions     # Configuration options
└── TableDefinition     # Table definition
```

## Dependencies

```
jforgame-data
    ├── jforgame-commons (reflection utilities, class scanning)
    ├── spring-core (ConversionService)
    ├── apache-commons-csv (CSV parsing)
    ├── apache-poi (Excel parsing)
    └── jackson (JSON parsing)
```