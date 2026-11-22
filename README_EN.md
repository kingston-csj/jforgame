## English | [中文](README.md)

## Project Introduction

jforgame is a lightweight, high-performance mobile game server framework written in Java. It provides a variety of components for rapid secondary development, as well as tools for managing services in production environments. Meanwhile, practical business demos are offered to help developers get started quickly.

## Project Features

- Paired with tutorial articles in the framework blog to help you quickly understand the principles of each module
- Supports Socket/WebSocket access, perfectly adapting to server architecture for mobile games, web games, H5 games and mini-program games
- Comes with a C# network framework counterpart, compatible with Socket/WebSocket and JSON/struct protocols
- Communication protocols support JSON, Protobuf and standard JavaBeans, offering multiple options for client integration
- One-click export of communication protocols for client-side languages such as C# and TypeScript (refer to CSharpProtocolGenerator tool)
- Built-in inter-process communication, with powerful asynchronous/synchronous client APIs to easily implement cross-server business logic
- Adopts a custom lightweight ORM library, supporting multiple data sources, automatic table creation and field addition, as well as full/incremental table field updates
- Rich built-in framework components ready for direct secondary business development
- Supports hot code replacement and hot configuration updates without server shutdown, ensuring uninterrupted game operation and maintenance
- A Go version for game server development is also available → [gforgame](https://github.com/kingston-csj/gforgame)
- SpringBoot-based practical MMORPG server project → [mmorpg Game Server](https://github.com/kingston-csj/mmorpg)
- Independent backend management website for game operation and maintenance → [gGamekeeper Admin System](https://github.com/kingston-csj/gamekeeper)
- Beyond game development, it can also build other network applications such as real-time chat systems → [QQ-like IM Application](https://github.com/kingston-csj/im)

Module Structure

```git
jforgame
├── jforgame-commons -- Basic common utilities
├── jforgame-threadmodel -- Thread model; two implementations: Actor model & keyword-based dispatch. Actor is recommended to avoid uneven thread workload
├── jforgame-runtime -- Application runtime monitoring metrics (memory, threads, classes, etc.)
├── jforgame-socket-parent     -- TCP Socket communication module, including IO gateway, message routing and session management; provides Netty and Mina implementations
├── jforgame-socket-api    -- Base API interfaces for server & client
├── jforgame-socket-netty  -- Netty implementation with WebSocket server & client support
├── jforgame-socket-mina   -- Mina implementation (without WebSocket support)
├── jforgame-orm     -- Game-server-oriented custom ORM library for conversion between database table records and POJO objects
├── jforgame-cache   -- Entity cache API providing a unified "cache + persistence" entrypoint for `Entity` implementations, independent of any container or ORM
├── jforgame-cache-spring-boot-starter -- Spring Boot integration for jforgame-cache, offering `@EntityCacheAutowired`, `SpringCacheStore`, and `JpaDatabaseAccessor` auto-configuration
├── jforgame-persist -- Asynchronous data persistence module based on queues, delivering high throughput and low latency with dead-letter queue support
├── jforgame-data -- Configuration data module, providing CSV/Excel/JSON loading, validation, hot reload and secondary cache support
├── jforgame-data-spring-boot-starter -- Spring Boot starter for jforgame-data, responsible for property binding and auto-configuration
├── jforgame-hotswap  -- Supports business code hot replacement
├── jforgame-codec-parent         -- Codec component for Socket communication
├── jforgame-codec-api        -- Codec API interfaces
├── jforgame-codec-protobuf   -- Protobuf codec implementation
├── jforgame-codec-struct     -- Standard JavaBean reflection codec; advanced version supports heterogeneous collection element types
├── jforgame-demo  -- Demo of basic game components and business modules
```

jforgame follows a component-based design. Each module is independent, allowing developers to import only required components.For example, if only Socket communication is needed, simply introduce jforgame-socket-netty or jforgame-socket-mina.

## Core Module Highlights

### jforgame-cache — Entity Cache API

Provides a unified "cache + persistence" entrypoint for entities implementing `jforgame.commons.persist.Entity`. The core module is free of container and ORM coupling — it is decoupled from concrete implementations via SPI.

**Core API**

| Interface / Class | Description |
| --- | --- |
| `EntityCacheService<E>` | Top-level cache service interface: `getEntity` / `putEntity` / `removeEntity` |
| `BaseEntityCacheService<E>` | Default implementation with constructor injection; additionally exposes segmented-lock `getOrCreate` |
| `DatabaseAccessor` | Database access SPI, implemented by the business layer; contracts are `load` / `saveOrUpdate` / `remove` |
| `CacheStore` | Cache storage SPI, replaceable with any cache (ConcurrentHashMap, Spring Cache, LRU, etc.) |
| `EntityCacheServiceFactory` | Factory interface that reuses service instances per entity type |

**Concurrency behavior**: `getOrCreate` uses 10-segment `ReentrantLock` (modulo `id.hashCode()`) for double-checking, preventing concurrent creation of entities with the same primary key.

For full usage details and SPI contracts see [jforgame-cache/README_EN.md](jforgame-cache/README_EN.md).

### jforgame-cache-spring-boot-starter — Spring Boot Integration

Enables jforgame-cache in Spring Boot with auto-configuration and annotation-driven injection.

**Key Features**

- `@EntityCacheAutowired` — annotate a `BaseEntityCacheService<E>` field and the corresponding cache service is injected automatically from the field's generic type
- `SpringCacheStore` — delegates entity caching to Spring `CacheManager`, so Caffeine, Redis and other providers can share the same configuration
- `JpaDatabaseAccessor` — auto-registered when JPA + Hibernate are on the classpath and a unique `EntityManagerFactory` is present
- Auto-configuration — `EntityCacheAutoConfiguration` registers `EntityCacheServiceFactory`, `SpringCacheStore`, `LocalCacheStore` and other beans conditionally

**CacheStore resolution priority**: custom application bean → `SpringCacheStore` when `CacheManager` is on the classpath → `LocalCacheStore` (demo only). For production, prefer `SpringCacheStore` or `LruSoftCacheStore`.

For full usage see [jforgame-cache-spring-boot-starter/README_EN.md](jforgame-cache-spring-boot-starter/README_EN.md).

### jforgame-persist — Asynchronous Data Persistence

Queue-based asynchronous persistence mechanism for high-throughput, low-latency data sinks such as game player archiving.

**Container Types**

| Container | Description | Use Case |
| --- | --- | --- |
| `QueueContainer` | FIFO container backed by `BlockingQueue` | General async persistence, single-thread consumption |
| `DelayContainer` | Container with delay semantics | Non-real-time data (e.g. delayed offline player archiving) |
| `CronContainer` | Cron-expression-based scheduled container | Periodic batch persistence |
| `PersistContainerGroup` | Group of containers + `ShardingStrategy` | High-concurrency scenarios, multi-threaded parallel persistence |

**Dead-Letter Queue Mechanism**

Persistence failures are routed to a dead-letter queue (DLQ) instead of being retried indefinitely and wasting threads:

- Configurable `maxRetryCount`; entities exceeding the threshold move to the DLQ
- `DeadLetterListener` receives dead-letter events (can bridge to alerting systems)
- Individual reprocess `reprocess(key)`, batch `batchReprocess()`, full reset `reset()` are all supported

```java
DeadLetterQueue dlq = new DeadLetterQueue(3);
dlq.addListener(deadLetter -> log.error("Persistence failed: {}", deadLetter.getKey()));
QueueContainer container = new QueueContainer("player", strategy, dlq);
container.receive(playerEntity);
```

For the full flow diagram and best practices see [jforgame-persist/README.md](jforgame-persist/README.md).


## Quick Start

1.  Tutorials for each module → Wiki
2.  Clone the project: git clone https://github.com/kingston-csj/jforgame
3.  Import the project into an IDE with Maven support by loading the root pom.xml
4.  Create databases game_data_001 and game_user_001, then import the corresponding SQL files under test/resources
5.  Start the server via the ServerStartup entry class
    (When importing all modules, configure the IDE working directory: Run -> Edit Configurations -> Working Directory, set to \*\*\jforgame\jforgame-demo)
6.  Start the client via the ClientStartup entry class
    (Working directory configuration is the same as the server)
7.  Run the project demo

Maven Dependency

```xml
  <dependency>
  <groupId>io.github.jforgame</groupId>
  <artifactId>jforgame-socket-netty</artifactId>
  <version>latest</version>
  </dependency>
  <dependency>
  <groupId>io.github.jforgame</groupId>
  <artifactId>jforgame-codec-struct</artifactId>
  <version>latest</version>
  </dependency>
```

One-line Server Startup (Socket/WebSocket)

```java
  TcpSocketServerBuilder.newBuilder()
  .bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
  .setMessageFactory(GameMessageFactory.getInstance())
  .setMessageCodec(new StructMessageCodec())
  .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
  .build()
  .start();
```

Message Protocol Definition

```java
  @MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.REQ_LOGIN)
  public class ReqAccountLogin implements Message {
  /** Account serial number */
  private long accountId;
  private String password;
  }
```

Message Route Handling

```java
  @MessageRoute
  public class LoginController {

      @RequestHandler
      public void reqAccountLogin(IdSession session, ReqAccountLogin request) {
          GameContext.loginManager.handleAccountLogin(session, request.getAccountId(), request.getPassword());
      }
  }
```

Development is straightforward — just follow the demo pattern and start coding your business logic directly!

## Join the Community

Welcome to star/fork, welcome to learn/use, looking forward to contributing code together!!
If you find bugs or have any questions, please submit an issue!!

## Contributing

We are always looking for people to join us. If you have an issue, feature request, or pull request, let us know!
Many thanks for your stars!!

## Disclaimer

This project is only for learning and research purposes, and is prohibited from being used for illegal profit and commercial activities. Any legal disputes arising therefrom are not related to the author!!
