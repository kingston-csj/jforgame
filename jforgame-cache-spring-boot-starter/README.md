# jforgame-cache-spring-boot-starter

`jforgame-cache` 的 Spring Boot 集成，提供：

- `@EntityCacheAutowired` 实体缓存服务自动注入；
- `SpringCacheStore`，把实体缓存委托给 Spring `CacheManager`；
- 自动配置与缓存实现的按需装配。

## 引入依赖

```xml
<dependency>
    <groupId>io.github.jforgame</groupId>
    <artifactId>jforgame-cache-spring-boot-starter</artifactId>
    <version>4.6.0</version>
</dependency>
```

Spring Cache 在 starter 中是 `provided` 依赖。需要把缓存交给 Spring `CacheManager` 时，请自行引入：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

## 前置条件

1. 提供一个 `DatabaseAccessor` Bean，否则 `EntityCacheServiceFactory` 无法创建；
2. 选择缓存实现（见下文）。**推荐 `SpringCacheStore` 或 `LruSoftCacheStore`**。

## 快速开始

```java
@Configuration
public class CacheConfig {

    @Bean
    public DatabaseAccessor databaseAccessor(DataSource dataSource) {
        // 可以基于 jforgame-orm、MyBatis 或任意持久化框架实现
        return new MyBatisDatabaseAccessor();
    }

    @Bean
    public CacheStore cacheStore() {
        // 生产环境推荐二选一：SpringCacheStore / LruSoftCacheStore
        return new LruSoftCacheStore(1000);
    }
}
```

```java
@Service
public class UserService {

    @EntityCacheAutowired
    private BaseEntityCacheService<User> userCache;

    public User getUser(long id) {
        return userCache.getEntity(id);
    }
}
```

## CacheStore 选择

启动时按以下优先级确定 `CacheStore`：

| 条件 | 使用 |
| --- | --- |
| 应用自定义了 `CacheStore` Bean | 使用自定义实现 |
| 类路径存在 `org.springframework.cache.CacheManager` | `SpringCacheStore` |
| 类路径不存在 `CacheManager` | `LocalCacheStore` |

### 注意：LocalCacheStore 仅用于演示

`LocalCacheStore` 只是基于 `ConcurrentHashMap` 的最简实现，**不建议在生产环境使用**：

- 没有容量上限、没有 LRU/LFU 淘汰、没有过期策略；
- 实体按 ID 缓存，长期运行会持续增长，存在 OOM 风险；
- starter 只在完全没有 Spring Cache 时回退到它，目的是让示例或最简单场景能够启动，而不是提供可用的生产缓存。

生产环境请显式声明自己的 `CacheStore` Bean，例如 `SpringCacheStore` 或 `LruSoftCacheStore`。

### SpringCacheStore

使用 Spring `CacheManager`，可与 Caffeine、Redis 等缓存实现共用一套配置。

- 缓存名等于实体类简单名（`BaseEntityCacheService.getCacheName()`）；
- 若使用 Redis 等固定缓存名的实现，需要预先创建同名缓存，否则读写会抛 `IllegalStateException`；
- 类路径存在 `CacheManager` 时由 starter 自动装配；也可以手动声明：

```java
@Bean
public CacheStore cacheStore(CacheManager cacheManager) {
    return new SpringCacheStore(cacheManager);
}
```

### LruSoftCacheStore

本地有界缓存：硬缓存使用 LRU，淘汰的条目进入软引用溢出层，在内存压力下可被 GC 回收。

适合单机、希望本地缓存有明确上限且不想引入外部缓存的场景。

`LruSoftCacheStore` 依赖 `concurrentlinkedhashmap-lru`（在 `jforgame-cache` 中为 `provided`），使用方需要自行引入：

```xml
<dependency>
    <groupId>com.googlecode.concurrentlinkedhashmap</groupId>
    <artifactId>concurrentlinkedhashmap-lru</artifactId>
    <version>1.4.2</version>
</dependency>
```

```java
@Bean
public CacheStore cacheStore() {
    return new LruSoftCacheStore(10_000);
}
```

容量说明：`hardMaxSize` 是**每个 cacheName** 的硬缓存容量，软缓存容量为其 2 倍，因此单个 cacheName 最多保留约 `3 × hardMaxSize` 个 key。该实现是进程内的，不提供分布式一致性。

## 配置项

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `jforgame.cache.base-packages` | `List<String>` | 空（扫描全部） | 限定 `@EntityCacheAutowired` 的 Bean 扫描包，按类名前缀匹配。 |

## @EntityCacheAutowired

- 只能标注在 `BaseEntityCacheService<E>` 类型（或其子类）的字段上；
- 从字段泛型提取实体类，例如 `BaseEntityCacheService<User>` 对应 `User`；
- 同一实体类复用同一个服务实例。

## 自动配置

`EntityCacheAutoConfiguration` 注册：

| Bean | 条件 | 说明 |
| --- | --- | --- |
| `EntityCacheServiceFactory` | 无自定义工厂 | 注入 `DatabaseAccessor` 与 `CacheStore` |
| `EntityCacheServiceInjectProcessor` | 始终注册 | 处理 `@EntityCacheAutowired` |
| `SpringCacheStore` | 存在 `CacheManager` 且无自定义 `CacheStore` | 委托 Spring Cache |
| `LocalCacheStore` | 无 `CacheManager` 且无自定义 `CacheStore` | 仅作演示/兜底 |

## 注意事项

- 未提供 `DatabaseAccessor` Bean 时，应用启动会失败；这是有意的快速失败。
- 缓存 key 直接使用 `Entity#getId()`，主键类型需正确实现 `equals` / `hashCode`。
- 数据库操作成功后才会更新缓存；数据库异常时缓存保持不变。
