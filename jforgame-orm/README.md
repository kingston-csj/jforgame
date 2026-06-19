### Ⅰ. 简介

- `jforgame-orm` 是专为游戏领域设计的轻量级ORM框架
- 游戏领域ORM特性：
  - **单表操作**：每次操作只涉及一张表，无需事务保证一致性
  - **缓存优先**：数据先写入内存缓存，保证响应速度
  - **异步持久化**：定期批量写入数据库，提高性能
  - **最终一致性**：通过其他机制保证数据最终一致性
- 自定义ORM框架特点：
  1. 提供类似Apache DbUtils的API，超轻量级，完美适配游戏服务器ORM特性
  2. 支持多数据源
  3. 支持全量字段更新和增量字段更新
  4. 支持ORM属性数据转换 `javax.persistence.AttributeConverter`
  5. 支持自动数据库表创建和字段添加，但不自动删除字段或更改字段类型
  6. 提供多种异步持久化容器

### Ⅱ. DDL策略

框架参考Hibernate的自动建表工具，提供以下DDL策略：

| 策略 | 说明 |
| --- | --- |
| `create` | 每次启动时删除所有现有表，然后根据实体类重新创建表结构 |
| `update` | 启动时根据实体类自动更新表结构（添加字段、索引等，不删除现有字段或表） |
| `validate` | 启动时验证实体类与表结构的一致性，不一致则抛出异常，不做任何修改 |
| `none` | 不做任何操作 |

DDL工具特点：
- 结合 `update` 和 `validate` 策略的优点
- 自动创建表、自动添加字段
- **不删除字段**，避免数据丢失
- 当表字段代码与数据库不一致时给出错误提示

### Ⅲ. 实体状态管理

实体对象拥有多种数据库状态，用于追踪持久化操作：

| 状态 | 说明 |
| --- | --- |
| `NORMAL` | 正常状态，无需持久化 |
| `UPDATE` | 待更新状态，需要更新到数据库 |
| `INSERT` | 新增状态，需要插入到数据库 |
| `DELETE` | 删除状态，准备从数据库物理删除 |

实体生命周期钩子方法：
- `afterLoad()`：从数据库加载完成后调用，标记实体为持久化状态
- `beforeSave()`：持久化前调用，自动识别实体是更新还是插入状态
- `afterSave()`：持久化后调用，重置实体为普通状态

### Ⅳ. 主要注解

实体类使用JPA标准注解进行映射：

| 注解 | 说明 |
| --- | --- |
| `@Entity` | 标记类为数据库实体 |
| `@Table` | 指定实体对应的表名 |
| `@Id` | 标记字段为主键 |
| `@Column` | 配置字段与数据库列的映射关系 |
| `@Convert` | 指定字段使用的属性转换器 |

### Ⅴ. 属性转换器

框架支持JPA标准的 `AttributeConverter`，用于自定义字段类型转换：

- `ObjectToJsonJpaConverter`：将对象属性转换为JSON字符串存储
  - 如果字段类型既不是基本类型也不是String，默认使用此转换器
  - 支持泛型类型，但建议Map的key使用String类型
- `ObjectToJsonZipJpaConverter`：将对象属性转换为压缩后的JSON字符串存储
- 可自定义转换器实现 `AttributeConverter` 接口

### Ⅵ. 使用示例

#### 1. 实体类定义

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

    // 使用JSON转换器存储复杂对象
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

#### 2. 初始化ORM引擎

```java
// 配置ORM属性
OrmProperties properties = new OrmProperties();
properties.setEntityPath("com.game.entity");
properties.setDdlAuto("update");  // 自动更新表结构

// 初始化数据源
DataSource dataSource = new HikariDataSource(config);

// 启动ORM引擎
OrmEngine.run(properties, dataSource);
```

#### 3. 数据操作

```java
OrmTemplate ormTemplate = new OrmTemplate(dataSource);

// 查询实体
Player player = ormTemplate.queryOne(Player.class, 1L);

// 查询列表
List<Player> players = ormTemplate.queryList("SELECT * FROM player WHERE level > ?", Player.class, 10);

// 插入实体
Player newPlayer = new Player();
newPlayer.setId(100L);
newPlayer.setName("test");
newPlayer.markAsNew();  // 标记为新增状态
ormTemplate.save(newPlayer);

// 更新实体
player.setLevel(20);
player.markAsModified();  // 标记为修改状态
ormTemplate.save(player);

// 增量更新（只更新指定字段）
player.setGold(1000);
player.addModifiedColumn("gold");  // 只更新gold字段
ormTemplate.save(player);

// 删除实体
player.markAsSoftDeleted();  // 标记为删除状态
ormTemplate.save(player);
```

#### 4. 执行任意SQL

```java
// 查询返回Map
Map<String, Object> result = ormTemplate.selectOne("SELECT COUNT(*) as count FROM player");

// 查询返回Map列表
List<Map<String, Object>> results = ormTemplate.selectAll("SELECT * FROM player WHERE level > 10");

// 执行更新SQL
int affectedRows = ormTemplate.update("UPDATE player SET gold = gold + 100 WHERE level > 10");
```

### Ⅶ. 增量更新

框架支持增量字段更新，只更新变化的字段，提高性能：

```java
// 方式一：手动添加需要更新的字段
player.setGold(1000);
player.setLevel(20);
player.addModifiedColumn("gold", "level");
ormTemplate.save(player);

// 方式二：强制保存所有字段（如玩家登出时）
player.forceSaveAll();
ormTemplate.save(player);
```

### Ⅷ. 注意事项

1. **主键类型**：主键字段不能是基本类型，只能是包装类型或String类型
2. **实体继承**：所有需要持久化的实体类都应继承 `BaseEntity`
3. **钩子方法**：确保正确调用 `afterLoad()`、`beforeSave()`、`afterSave()` 钩子方法
4. **事务支持**：本框架不支持事务，如需事务请使用其他方案
5. **连接管理**：所有CRUD方法会自动关闭数据库连接，避免在事务环境中使用