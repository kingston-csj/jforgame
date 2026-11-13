# jforgame-cache

实体缓存 API。为实现了 `jforgame.commons.persist.Entity` 的实体提供「缓存 + 持久化」的统一读写入口，核心模块不绑定任何容器或 ORM。

## 模块组成

| 模块 | 依赖 | 说明 |
| --- | --- | --- |
| `jforgame-cache` | `jforgame-commons` | 纯 Java 核心 API：缓存服务、缓存存储 SPI、数据库访问 SPI |
| `jforgame-cache-spring-boot-starter` | `jforgame-cache`、Spring Cache | Spring 集成：自动注入、`CacheManager` 适配、自动配置 |

---

## 核心 API（`jforgame-cache`）

包名：`jforgame.cache`

### EntityCacheService\<E>

实体缓存服务的顶层接口，`E` 必须是 `jforgame.commons.persist.Entity` 的实现。

| 方法 | 说明 |
| --- | --- |
| `E getEntity(Object id)` | 按主键读取实体。缓存未命中时回源数据库并写回缓存；不存在返回 `null`（`null` 不进入缓存）。 |
| `E putEntity(E entity)` | 保存实体到数据库并写入缓存，返回实体本身。 |
| `void removeEntity(E entity)` | 从数据库删除实体并驱逐缓存。默认实现抛出 `UnsupportedOperationException`，由具体实现覆盖。 |

### BaseEntityCacheService\<E>

`EntityCacheService` 的默认实现，通过构造器注入依赖，不依赖 Spring。

```java
BaseEntityCacheService(
        Class<E> entityClass,
        DatabaseAccessor databaseAccessor,
        CacheStore cacheStore)
```

| 成员 | 说明 |
| --- | --- |
| `E getEntity(Object id)` | 先查 `CacheStore`，未命中调用 `DatabaseAccessor.load`，命中结果写回缓存。 |
| `E putEntity(E entity)` | 先调用 `DatabaseAccessor.saveOrUpdate`，成功后 `CacheStore.put`。 |
| `void removeEntity(E entity)` | 先调用 `DatabaseAccessor.remove`，成功后 `CacheStore.evict`。 |
| `E getOrCreate(Object id, EntityBuilder<E> builder)` | 双检锁读取；不存在时用 `builder` 创建并落库。 |
| `Class<E> getEntityClass()` | 返回当前服务处理的实体类型。 |
| `protected String getCacheName()` | 缓存名，默认取实体类 `getSimpleName()`；子类可覆盖。 |

并发行为：

- `getEntity` / `putEntity` / `removeEntity` 本身不加锁，依赖 `CacheStore` 的线程安全语义。
- `getOrCreate` 使用 10 个分段 `ReentrantLock`（按 `id.hashCode()` 取模）做双重检查，避免同一主键并发创建。

### DatabaseAccessor

数据库访问 SPI，由业务层实现，用于把缓存组件与具体持久化技术解耦。

| 方法 | 说明 |
| --- | --- |
| `<E> E load(Class<E> entityClass, Object id)` | 按主键加载实体，不存在返回 `null`。 |
| `void saveOrUpdate(Entity<?> entity)` | 保存实体；已存在则更新，否则插入。 |
| `void remove(Entity<?> entity)` | 删除实体。 |

约定：

- 实现需保证线程安全，或至少在调用方并发模型下可用。
- 建议加载成功时完成实体加载后的初始化（如 ORM 的 `afterLoad` 钩子），保存时完成入库前后的钩子调用。

### CacheStore

缓存存储 SPI。核心模块只依赖该接口，因此可以替换为任意缓存实现。

| 方法 | 说明 |
| --- | --- |
| `Object get(String cacheName, Object key)` | 读取缓存，未命中返回 `null`。 |
| `void put(String cacheName, Object key, Object value)` | 写入缓存。 |
| `void evict(String cacheName, Object key)` | 驱逐缓存。 |

### LocalCacheStore

基于 `ConcurrentHashMap` 的默认 `CacheStore` 实现，适用于单机、无外部缓存场景。

```java
CacheStore cacheStore = new LocalCacheStore();
```

每个 `cacheName` 对应一个独立的 `ConcurrentHashMap`；不做过期、容量限制或持久化。

### EntityBuilder\<E>

`getOrCreate` 使用的实体构造回调。

```java
@FunctionalInterface
public interface EntityBuilder<E> {
    E build();
}
```

### EntityCacheServiceFactory

缓存服务工厂，按实体类型复用服务实例。

| 类型 | 说明 |
| --- | --- |
| `EntityCacheServiceFactory` | 接口：`<E extends Entity> BaseEntityCacheService<E> create(Class<E> entityClass)`。 |
| `EntityCacheServiceFactoryImpl` | 默认实现，构造参数为 `DatabaseAccessor` 与 `CacheStore`；同一实体类重复 `create` 返回同一实例。 |

---

## 快速开始（不使用 Spring）

```java
DatabaseAccessor accessor = new JdbcDatabaseAccessor(dataSource);
CacheStore cacheStore = new LocalCacheStore();

BaseEntityCacheService<User> userCache =
        new BaseEntityCacheService<>(User.class, accessor, cacheStore);

User user = userCache.getEntity(1001L);
user = userCache.getOrCreate(1002L, () -> {
    User created = new User();
    created.setId(1002L);
    return created;
});
userCache.putEntity(user);
userCache.removeEntity(user);
```

`DatabaseAccessor` 由业务层提供。基于 `jforgame-orm` 的完整示例见：
`src/test/java/jforgame/cache/jdbc/JdbcDatabaseAccessor.java`。

该示例要求实体：

- 使用 `@javax.persistence.Entity` 标记，字段使用 `@Column` / `@Id`；
- 继承 `jforgame.orm.entity.BaseEntity`；
- 使用前已调用 `OrmProcessor.INSTANCE.initOrmBridges(...)` 完成 ORM 元数据初始化。

---

## Spring Boot Starter API

包名：`jforgame.cache.spring`

### 依赖与前置条件

引入 `jforgame-cache-spring-boot-starter` 后：

1. 必须由业务层提供 `DatabaseAccessor` Bean，否则自动配置的 `EntityCacheServiceFactory` 无法创建；
2. Spring Cache 为可选依赖：类路径上存在 `CacheManager` 时使用 `SpringCacheStore` 委托 Spring Cache；不存在时自动回退到 `LocalCacheStore`。

### @EntityCacheAutowired

标记在 `BaseEntityCacheService<E>` 类型的字段上，启动时自动注入对应实体的缓存服务。

```java
@Service
public class UserService {

    @EntityCacheAutowired
    private BaseEntityCacheService<User> userCache;
}
```

注入规则：

- 字段类型必须是 `BaseEntityCacheService` 的子类型；
- 从字段的泛型实参提取实体类（如 `BaseEntityCacheService<User>` → `User`）；
- 同一实体类在容器内复用同一服务实例。

### EntityCacheProperties

配置前缀：`jforgame.cache`

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `jforgame.cache.base-packages` | `List<String>` | 空（扫描全部） | 限定 `@EntityCacheAutowired` 的 Bean 扫描包，按类名前缀匹配。 |

### SpringCacheStore

`CacheStore` 的 Spring 实现，委托给 `CacheManager`。

```java
SpringCacheStore(CacheManager cacheManager)
```

- 缓存名即 `BaseEntityCacheService.getCacheName()`，默认是实体类简单名；
- `CacheManager` 中不存在对应名称的缓存时，读写会抛出 `IllegalStateException`。使用 Redis 等固定缓存名的实现时，需要预先创建同名缓存；使用 `ConcurrentMapCacheManager` 时默认动态创建。

### 自动配置

`EntityCacheAutoConfiguration` 注册以下 Bean：

| Bean | 条件 | 说明 |
| --- | --- | --- |
| `CacheStore` | 存在 `CacheManager` 且无自定义 `CacheStore` | `SpringCacheStore` |
| `CacheStore` | 类路径无 `CacheManager` 且无自定义 `CacheStore` | `LocalCacheStore` |
| `EntityCacheServiceFactory` | 容器中无自定义工厂 | 注入 `DatabaseAccessor` 与 `CacheStore` |
| `EntityCacheServiceInjectProcessor` | 始终注册 | `BeanPostProcessor`，处理 `@EntityCacheAutowired` |

自动配置通过 `META-INF/spring.factories` 与 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册。

---

## 扩展点

### 自定义 DatabaseAccessor

实现 `DatabaseAccessor` 并注册为 Bean（或手动传给 `BaseEntityCacheService`），即可接入 MyBatis、JPA、远程存储等：

```java
public class MyBatisDatabaseAccessor implements DatabaseAccessor {

    @Override
    public <E> E load(Class<E> entityClass, Object id) {
        return sqlSession.selectOne(entityClass.getSimpleName() + ".selectById", id);
    }

    @Override
    public void saveOrUpdate(Entity<?> entity) {
        // insert or update
    }

    @Override
    public void remove(Entity<?> entity) {
        // delete by id
    }
}
```

### 自定义 CacheStore

实现 `CacheStore` 并注册为 Bean，可覆盖 starter 自动选择的缓存实现（`SpringCacheStore` 或 `LocalCacheStore`）：

```java
@Bean
public CacheStore cacheStore() {
    return new LocalCacheStore();
}
```

---

## 注意事项

- 实体主键由 `Entity#getId()` 提供，缓存 key 直接使用该返回值，主键类型需正确实现 `equals` / `hashCode`。
- 缓存写回发生在数据库操作成功之后；数据库操作抛异常时不会更新缓存。
- `getEntity` 不回填 `null`，避免缓存穿透保护需要由业务层自行处理。
- `LocalCacheStore` 不做容量与过期控制，长期运行的服务建议使用带淘汰策略的 `CacheStore`。
