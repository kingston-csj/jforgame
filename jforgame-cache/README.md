# jforgame-cache

实体缓存 API。为实现了 `jforgame.commons.persist.Entity` 的实体提供「缓存 + 持久化」的统一读写入口，核心模块不绑定任何容器或 ORM。

## 核心 API

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

基于 `ConcurrentHashMap` 的默认 `CacheStore` 实现，无容量上限，无过期机制，仅用于示例，不推荐生产使用。

```java
CacheStore cacheStore = new LocalCacheStore();
```

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
参考 jforgame-cache-spring-boot-starter 模块的相关文档
