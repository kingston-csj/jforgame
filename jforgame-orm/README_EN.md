### Ⅰ. Introduction

- `jforgame-orm` is a lightweight ORM framework designed specifically for the game domain
- Game domain ORM characteristics:
  - **Single table operations**: Each operation involves only one table, no transaction needed for consistency guarantee
  - **Cache first**: Data is first written to memory cache to ensure response speed
  - **Async persistence**: Periodically batch write to database to improve performance
  - **Eventual consistency**: Ensure data eventual consistency through other mechanisms
- Custom ORM framework features:
  1. Provides API similar to Apache DbUtils, super lightweight, perfectly adapted to ORM characteristics of game servers
  2. Supports multiple data sources
  3. Supports full field update and incremental field update
  4. Supports ORM attribute data conversion `javax.persistence.AttributeConverter`
  5. Supports automatic database schema table creation and field addition, but will not automatically delete fields or change field types
  6. Provides multiple async persistence containers

### Ⅱ. DDL Strategies

The framework references Hibernate's automatic table creation tool and provides the following DDL strategies:

| Strategy | Description |
| --- | --- |
| `create` | Drop all existing tables on every startup, then recreate table structure based on entity classes |
| `update` | Auto update table structure based on entity classes on startup (add fields, indexes, etc., do not delete existing fields or tables) |
| `validate` | Validate consistency between entity classes and table structure on startup, throw exception if inconsistent, do not modify anything |
| `none` | Do nothing |

DDL tool features:
- Combines the advantages of `update` and `validate` strategies
- Auto create tables, auto add fields
- **Do not delete fields**, to avoid data loss
- Give error when table field code is inconsistent with database

### Ⅲ. Entity State Management

Entity objects have various database states for tracking persistence operations:

| State | Description |
| --- | --- |
| `NORMAL` | Normal state, no need to persist |
| `UPDATE` | Modified state, needs to be updated to database |
| `INSERT` | New state, needs to be inserted into database |
| `DELETE` | Deleted state, ready to be physically deleted from database |

Entity lifecycle hook methods:
- `afterLoad()`: Called after loading from database, marks entity as persistent state
- `beforeSave()`: Called before persistence, automatically identifies whether entity should be updated or inserted
- `afterSave()`: Called after persistence, resets entity to normal state

### Ⅳ. Main Annotations

Entity classes use JPA standard annotations for mapping:

| Annotation | Description |
| --- | --- |
| `@Entity` | Marks class as database entity |
| `@Table` | Specifies table name for the entity |
| `@Id` | Marks field as primary key |
| `@Column` | Configures mapping between field and database column |
| `@Convert` | Specifies attribute converter for the field |

### Ⅴ. Attribute Converters

The framework supports JPA standard `AttributeConverter` for custom field type conversion:

- `ObjectToJsonJpaConverter`: Converts object properties to JSON string for storage
  - If field type is neither primitive nor String, this converter is used by default
  - Supports generic types, but recommends using String for Map keys
- `ObjectToJsonZipJpaConverter`: Converts object properties to compressed JSON string for storage
- Custom converters can be implemented by `AttributeConverter` interface

### Ⅵ. Usage Examples

#### 1. Entity Class Definition

```java
@Entity
@Table(name = "player")
public class Player extends BaseEntity<Long> {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "level")
    private int level;

    @Column(name = "gold")
    private long gold;

    // Use JSON converter to store complex objects
    @Convert(converter = ObjectToJsonJpaConverter.class)
    @Column(name = "items")
    private List<ItemData> items;

    @Override
    public Long getId() {
        return id;
    }

    // getter/setter...
}
```

#### 2. Initialize ORM Engine

```java
// Configure ORM properties
OrmProperties properties = new OrmProperties();
properties.setEntityPath("com.game.entity");
properties.setDdlAuto("update");  // Auto update table structure

// Initialize data source
DataSource dataSource = new HikariDataSource(config);

// Start ORM engine
OrmEngine.run(properties, dataSource);
```

#### 3. Data Operations

```java
OrmTemplate ormTemplate = new OrmTemplate(dataSource);

// Query entity
Player player = ormTemplate.queryOne(Player.class, 1L);

// Query list
List<Player> players = ormTemplate.queryList("SELECT * FROM player WHERE level > ?", Player.class, 10);

// Insert entity
Player newPlayer = new Player();
newPlayer.setId(100L);
newPlayer.setName("test");
newPlayer.markAsNew();  // Mark as new state
ormTemplate.save(newPlayer);

// Update entity
player.setLevel(20);
player.markAsModified();  // Mark as modified state
ormTemplate.save(player);

// Incremental update (only update specified fields)
player.setGold(1000);
player.addModifiedColumn("gold");  // Only update gold field
ormTemplate.save(player);

// Delete entity
player.markAsSoftDeleted();  // Mark as deleted state
ormTemplate.save(player);
```

#### 4. Execute Arbitrary SQL

```java
// Query returning Map
Map<String, Object> result = ormTemplate.selectOne("SELECT COUNT(*) as count FROM player");

// Query returning Map list
List<Map<String, Object>> results = ormTemplate.selectAll("SELECT * FROM player WHERE level > 10");

// Execute update SQL
int affectedRows = ormTemplate.update("UPDATE player SET gold = gold + 100 WHERE level > 10");
```

### Ⅶ. Incremental Update

The framework supports incremental field update, only updating changed fields to improve performance:

```java
// Method 1: Manually add fields to update
player.setGold(1000);
player.setLevel(20);
player.addModifiedColumn("gold", "level");
ormTemplate.save(player);

// Method 2: Force save all fields (e.g., when player logs out)
player.forceSaveAll();
ormTemplate.save(player);
```

### Ⅷ. Notes

1. **Primary key type**: Primary key field cannot be primitive type, must be wrapper type or String type
2. **Entity inheritance**: All entity classes that need to be persisted should inherit `BaseEntity`
3. **Hook methods**: Ensure correct calling of `afterLoad()`, `beforeSave()`, `afterSave()` hook methods
4. **Transaction support**: This framework does not support transactions, use other solutions if needed
5. **Connection management**: All CRUD methods will automatically close database connections, avoid using in transaction environments