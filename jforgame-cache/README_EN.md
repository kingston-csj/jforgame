# jforgame-cache

Entity cache API. Provides a unified read/write entrypoint of "cache + persistence" for entities that implement `jforgame.commons.persist.Entity`. The core module does not bind to any container or ORM.

## Core API

### EntityCacheService\<E>

Top-level interface for entity cache services. `E` must be an implementation of `jforgame.commons.persist.Entity`.

| Method | Description |
| --- | --- |
| `E getEntity(Object id)` | Read entity by primary key. Falls back to the database on cache miss and writes the result back to cache; returns `null` when the entity does not exist (`null` is never cached). |
| `E putEntity(E entity)` | Persists the entity to the database and writes it to cache; returns the entity itself. |
| `void removeEntity(E entity)` | Deletes the entity from the database and evicts it from cache. The default implementation throws `UnsupportedOperationException`; override it in concrete implementations. |

### BaseEntityCacheService\<E>

Default implementation of `EntityCacheService`. Dependencies are injected via constructor — no Spring required.

```java
BaseEntityCacheService(
        Class<E> entityClass,
        DatabaseAccessor databaseAccessor,
        CacheStore cacheStore)
```

| Member | Description |
| --- | --- |
| `E getEntity(Object id)` | First queries `CacheStore`, then falls back to `DatabaseAccessor.load` on miss and writes the hit back to cache. |
| `E putEntity(E entity)` | Calls `DatabaseAccessor.saveOrUpdate` first, then `CacheStore.put` on success. |
| `void removeEntity(E entity)` | Calls `DatabaseAccessor.remove` first, then `CacheStore.evict` on success. |
| `E getOrCreate(Object id, EntityBuilder<E> builder)` | Double-checked locking read; when the entity does not exist, creates it via `builder` and persists it. |
| `Class<E> getEntityClass()` | Returns the entity type handled by this service. |
| `protected String getCacheName()` | Cache name; defaults to the entity class's `getSimpleName()`, overridable by subclasses. |

Concurrency behavior:

- `getEntity` / `putEntity` / `removeEntity` are lock-free and rely on the thread-safety guarantees of `CacheStore`.
- `getOrCreate` uses 10-segment `ReentrantLock` (modulo `id.hashCode()`) for double-checking, avoiding concurrent creation of entities with the same primary key.

### DatabaseAccessor

Database access SPI, implemented by the business layer to decouple the cache component from concrete persistence technologies.

| Method | Description |
| --- | --- |
| `<E> E load(Class<E> entityClass, Object id)` | Load an entity by primary key; returns `null` when not found. |
| `void saveOrUpdate(Entity<?> entity)` | Save the entity; update if exists, insert otherwise. |
| `void remove(Entity<?> entity)` | Delete the entity. |

Conventions:

- Implementations should be thread-safe, or at least safe under the caller's concurrency model.
- It is recommended to complete post-load initialization (e.g., the ORM `afterLoad` hook) during load, and run pre-save / post-save hooks during save.

### CacheStore

Cache storage SPI. The core module only depends on this interface, so it can be replaced with any cache implementation.

| Method | Description |
| --- | --- |
| `Object get(String cacheName, Object key)` | Read from cache; returns `null` on miss. |
| `void put(String cacheName, Object key, Object value)` | Write to cache. |
| `void evict(String cacheName, Object key)` | Evict from cache. |

### LocalCacheStore

Default `CacheStore` implementation backed by `ConcurrentHashMap`. No capacity limit, no expiration. Intended only for examples — **do not use in production**.

```java
CacheStore cacheStore = new LocalCacheStore();
```

### EntityBuilder\<E>

Entity construction callback used by `getOrCreate`.

```java
@FunctionalInterface
public interface EntityBuilder<E> {
    E build();
}
```

### EntityCacheServiceFactory

Cache service factory — reuses service instances per entity type.

| Type | Description |
| --- | --- |
| `EntityCacheServiceFactory` | Interface: `<E extends Entity> BaseEntityCacheService<E> create(Class<E> entityClass)`. |
| `EntityCacheServiceFactoryImpl` | Default implementation, constructor params are `DatabaseAccessor` and `CacheStore`; repeated `create` calls for the same entity class return the same instance. |

---

## Quick Start (without Spring)

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

`DatabaseAccessor` is provided by the business layer. For a complete example based on `jforgame-orm`, see:
`src/test/java/jforgame/cache/jdbc/JdbcDatabaseAccessor.java`.

That example requires entities to:

- Be marked with `@javax.persistence.Entity`, fields with `@Column` / `@Id`;
- Extend `jforgame.orm.entity.BaseEntity`;
- Have ORM metadata initialized via `OrmProcessor.INSTANCE.initOrmBridges(...)` before use.

---

## Spring Boot Starter API
Refer to the documentation of the `jforgame-cache-spring-boot-starter` module.
