# jforgame-cache-spring-boot-starter

Spring Boot integration for `jforgame-cache`, providing:

- `@EntityCacheAutowired` for auto-injection of entity cache services;
- `SpringCacheStore` that delegates entity caching to Spring `CacheManager`;
- Default `JpaDatabaseAccessor` based on JPA/Hibernate, auto-configured when `EntityManagerFactory` is present;
- Auto-configuration and on-demand cache implementation assembly.

## Add Dependency

```xml
<dependency>
    <groupId>io.github.jforgame</groupId>
    <artifactId>jforgame-cache-spring-boot-starter</artifactId>
    <version>latest</version>
</dependency>
```

Spring Cache is a `provided` dependency of the starter. If you want to delegate caching to Spring `CacheManager`, add it yourself:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

`spring-boot-starter-data-jpa` is also a `provided` dependency. Introduce it yourself when using the default JPA/Hibernate accessor (see below).

## Prerequisites

1. When using JPA/Hibernate you do **not** need to hand-write a `DatabaseAccessor`: the starter auto-registers `JpaDatabaseAccessor` whenever an `EntityManagerFactory` is present.
2. For MyBatis, jforgame-orm or other persistence frameworks, you must provide a `DatabaseAccessor` bean, otherwise `EntityCacheServiceFactory` cannot be created.
3. Choose a cache implementation (see below). **`SpringCacheStore` or `LruSoftCacheStore` are recommended**.

## Quick Start

With JPA/Hibernate, simply declare a cache implementation:

```java
@Configuration
public class CacheConfig {

    @Bean
    public CacheStore cacheStore() {
        // Pick one for production: SpringCacheStore / LruSoftCacheStore
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

### Default DatabaseAccessor (JPA/Hibernate)

When JPA + Hibernate are on the classpath and a unique `EntityManagerFactory` exists in the container, the starter auto-registers `JpaDatabaseAccessor`. Entities must:

- Use JPA annotations (`@Entity`, `@Id`, etc.);
- Implement `jforgame.commons.persist.Entity`;
- Have a primary key type with a correct `equals` / `hashCode`.

Behavior and limitations:

- `load` uses an independent `EntityManager` that is closed immediately after returning, so **lazy associations are not available** — cached entities should use EAGER or fetch joins.
- Every write operation uses an independent local transaction and does not participate in an outer Spring transaction; JTA environments are not supported.
- Based on Hibernate `Session#saveOrUpdate`, which automatically handles insert vs. update as well as auto-increment primary key backfill.
- To use MyBatis, jforgame-orm, etc., declare your own `DatabaseAccessor` bean to override the default.

```java
@Bean
public DatabaseAccessor databaseAccessor(DataSource dataSource) {
    // Implement yourself for other persistence frameworks
    return new MyBatisDatabaseAccessor();
}
```

## Choosing a CacheStore

On startup, `CacheStore` is resolved with the following priority:

| Condition | Uses |
| --- | --- |
| Application defines a custom `CacheStore` bean | The custom implementation |
| `org.springframework.cache.CacheManager` is on the classpath | `SpringCacheStore` |
| `CacheManager` is absent | `LocalCacheStore` |

### Note: LocalCacheStore is for demo only

`LocalCacheStore` is the simplest possible `ConcurrentHashMap`-based implementation. **It is not recommended for production** because:

- It has no capacity limit, no LRU/LFU eviction, and no expiration strategy.
- Entities are cached by ID; it grows steadily over time and risks OOM on long-running servers.
- The starter falls back to it only when Spring Cache is completely absent, so that demos or the simplest scenarios can still boot. It is not meant to be a production cache.

Always declare your own `CacheStore` bean in production — e.g. `SpringCacheStore` or `LruSoftCacheStore`.

### SpringCacheStore

Delegates to Spring `CacheManager`, so it can share configuration with Caffeine, Redis or any other Spring Cache implementation.

- Cache name equals the entity class's simple name (`BaseEntityCacheService.getCacheName()`);
- If you use an implementation like Redis that requires pre-existing cache names, create caches with matching names beforehand, otherwise reads/writes will throw `IllegalStateException`;
- Auto-configured by the starter when `CacheManager` is present; you can also declare it manually:

```java
@Bean
public CacheStore cacheStore(CacheManager cacheManager) {
    return new SpringCacheStore(cacheManager);
}
```

### LruSoftCacheStore

Local bounded cache: a hard cache with LRU eviction, and an overflow layer using soft references that the GC can reclaim under memory pressure.

Suitable for single-node deployments where you want a hard local cache limit but do not want to introduce an external cache.

`LruSoftCacheStore` depends on `concurrentlinkedhashmap-lru` (marked `provided` in `jforgame-cache`). Add it yourself:

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
    return new LruSoftCacheStore(1_000);
}
```

Capacity note: `hardMaxSize` is the hard-capacity **per cacheName**; the soft cache capacity is twice that. This implementation is in-process only and provides no distributed consistency.

## Configuration Properties

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `jforgame.cache.base-packages` | `List<String>` | empty (scan all) | Restricts bean scanning for `@EntityCacheAutowired`; matched by class name prefix. |

## @EntityCacheAutowired

- Can only annotate fields of type `BaseEntityCacheService<E>` (or a subclass);
- Extracts the entity class from the field's generic type — e.g. `BaseEntityCacheService<User>` maps to `User`;
- The same entity class reuses the same service instance.

## Auto-Configuration

`EntityCacheAutoConfiguration` registers:

| Bean | Condition | Description |
| --- | --- | --- |
| `EntityCacheServiceFactory` | no custom factory | Injects `DatabaseAccessor` and `CacheStore` |
| `EntityCacheServiceInjectProcessor` | always registered | Handles `@EntityCacheAutowired` |
| `JpaDatabaseAccessor` | `EntityManagerFactory` present and no custom `DatabaseAccessor` | Default JPA/Hibernate-based implementation |
| `SpringCacheStore` | `CacheManager` present and no custom `CacheStore` | Delegates to Spring Cache |
| `LocalCacheStore` | no `CacheManager` and no custom `CacheStore` | Demo / fallback only |

## Notes

- When using JPA/Hibernate you do not need to provide a `DatabaseAccessor` by default. With other persistence frameworks, application startup will fail fast without one — this is an intentional fail-fast behavior.
- The JPA default implementation does not participate in outer transactions, and returned entities are detached; do not rely on lazy associations.
- Cache keys directly use `Entity#getId()`, so the primary key type must correctly implement `equals` / `hashCode`.
- Cache is only updated after the database operation succeeds; in case of a DB exception the cache is left unchanged.
