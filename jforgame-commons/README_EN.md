# jforgame-commons

jforgame-commons is a general utility library module that provides commonly used data structures, utility classes, and basic components for game server development.

## Directory Structure

```
jforgame-commons/
├── ds/              # Data Structures
├── eventbus/        # Event Bus
├── persist/         # Persistence Containers
├── reflection/      # Reflection Utilities
├── schedule/        # Schedule Expression Parsing
├── thread/          # Thread Utilities
├── trie/            # Trie Tree
└── util/            # General Utility Classes
```

---

## ds - Data Structures

Provides commonly used data structure implementations.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `CircleQueue` | Circular queue that supports ring traversal, suitable for polling scenarios |
| `ConcurrentHashSet` | Thread-safe HashSet implementation |
| `LazyCacheMap` | Lazy-loading cache Map with on-demand loading and expiration cleanup |
| `LruHashMap` | LRU cache Map that automatically evicts least recently used elements |
| `Pair<K, V>` | Pair tuple for storing two associated values |
| `Triple<A, B, C>` | Triple tuple for storing three associated values |

---

## eventbus - Event Bus

Provides publish-subscribe pattern event communication mechanism, supporting synchronous and asynchronous event processing.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `EventBus` | Core event bus class responsible for event publishing and dispatching |
| `Subscribe` | Subscription annotation to mark methods as event subscribers |
| `Subscriber` | Subscriber wrapper containing subscription method and trigger conditions |
| `SubscriberRegistry` | Subscriber registry managing all subscribers |
| `BaseEvent` | Base event class providing basic event properties |

### Usage

- Use `@Subscribe` annotation to mark subscription methods
- Publish events via `EventBus.publish()`
- Support specifying event trigger thread mode

---

## persist - Persistence Containers

Provides asynchronous data persistence mechanism with multiple persistence strategies.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `QueueContainer` | Queue container for sequential batch data persistence |
| `DelayContainer` | Delay container supporting delayed persistence after specified time |
| `CronContainer` | Cron container for scheduled persistence based on Cron expression |
| `QueueContainerGroup` | Queue container group managing multiple queue containers |
| `PersistContainer` | Persistence container interface defining persistence behavior |
| `SavingStrategy` | Saving strategy interface defining when to trigger persistence |
| `Entity` | Entity interface marking persistable objects |
| `DbService` | Database service interface defining data storage operations |

### Persistence Strategies

- **Queue Strategy**: Data enters queue and is batch-written to database periodically
- **Delay Strategy**: Persist after a delay period following data change, avoiding frequent writes
- **Cron Strategy**: Persist at scheduled times according to specified Cron expression

---

## reflection - Reflection Utilities

Provides high-performance reflection invocation utilities based on MethodHandle.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `MethodHandleUtils` | Reflection utility class providing method lookup and invocation |
| `MethodCaller` | Method caller interface encapsulating method invocation logic |

### Key Features

- Supports instance method and static method invocation
- Supports method caller caching for improved repeated invocation performance
- Supports automatic boxing/unboxing and parameter type matching

---

## schedule - Schedule Expression Parsing

Provides time expression parsing functionality, supporting Cron expressions and custom expressions.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `ScheduleExpressionParser` | Expression parser interface |
| `ScheduleExpressionParserManager` | Parser manager for managing multiple parsers |
| `QuartzScheduleExpressionParser` | Quartz Cron expression parser |

### Key Features

- Parse Cron expressions and calculate next trigger time
- Support registering custom expression parsers (e.g., server open time expressions)
- Support determining whether an expression is periodic

---

## thread - Thread Utilities

Provides thread-related utility classes and annotations.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `NamedThreadFactory` | Named thread factory creating threads with names |
| `ThreadSafe` | Thread-safe annotation marking classes as thread-safe |
| `NotThreadSafe` | Non-thread-safe annotation marking classes as not thread-safe |

### Key Features

- Create named threads for easier debugging and monitoring
- Identify class thread safety through annotations

---

## trie - Trie Tree

Provides Trie tree implementation for sensitive word detection and prefix matching.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `TrieDictionary` | Core Trie tree class providing sensitive word detection |
| `TrieNode` | Trie node storing characters and child nodes |
| `NodeContainer` | Node container interface managing child node collections |

### Key Features

- Add and remove sensitive words
- Detect if text contains sensitive words
- Exact match sensitive words
- Replace sensitive words with specified characters (e.g., `*`)
- Memory optimization support (use arrays instead of Map when node count is small)

---

## util - General Utility Classes

Provides a collection of commonly used utility classes.

### Core Classes

| Class Name | Description |
|------------|-------------|
| `DateUtil` | Date utility class providing date calculation, formatting, and parsing |
| `TimeUtil` | Time utility class providing time constants and unit conversion |
| `StringUtil` | String utility class providing null-check and case conversion |
| `FileUtil` | File utility class providing file reading and directory creation |
| `JsonUtil` | JSON utility class for object-JSON conversion based on Jackson |
| `DigestUtil` | Encoding/decoding utility class providing MD5 digest and Base64 encoding |
| `ZipUtil` | ZIP utility class providing compression/decompression for strings, byte arrays, and files |
| `RandomUtil` | Random utility class providing random number generation and weighted random |
| `RandomWeightPool` | Random weight pool supporting weighted random selection |
| `SplitUtil` | Splitter utility class providing string splitting and parsing |
| `TypeUtil` | Type utility class providing type checking and compatibility verification |
| `ClassScanner` | Class scanner for scanning all class files under a package |
| `NumberUtil` | Number utility class providing number conversion and calculation |

### Key Features

- **Date Processing**: Date arithmetic, format conversion, date comparison
- **String Processing**: Null-check, case conversion
- **File Operations**: File reading, directory creation
- **JSON Processing**: Object serialization/deserialization with generics support
- **Encoding/Decoding**: MD5 digest calculation, Base64 encoding/decoding
- **Compression/Decompression**: ZIP compression/decompression for strings, byte arrays, and files
- **Random Numbers**: Random number generation, weighted random selection
- **Type Checking**: Primitive type detection, type compatibility checking
- **Class Scanning**: Scan classes under package paths with annotation filtering

---

## Quick Start

### Add Dependency

```xml
<dependency>
    <groupId>jforgame</groupId>
    <artifactId>jforgame-commons</artifactId>
    <version>${jforgame.version}</version>
</dependency>
```

### Usage Examples

#### Sensitive Word Detection

```java
TrieDictionary trie = new TrieDictionary();
trie.addNode("sensitive_word_1");
trie.addNode("sensitive_word_2");

// Check if text contains sensitive words
boolean contains = trie.containsWords("This text contains sensitive_word_1");

// Replace sensitive words
String result = trie.replaceWords("This text contains sensitive_word_1");
// Output: "This text contains ***********"
```

#### Event Bus

```java
EventBus eventBus = new EventBus();

// Register subscriber
eventBus.register(new MySubscriber());

// Publish event
eventBus.publish(new MyEvent("event data"));
```

#### Persistence Container

```java
// Create queue persistence container
QueueContainer<Player> container = new QueueContainer<>(playerService, 1000);

// Add object to be persisted
container.add(player);

// Batch persist periodically
container.save();
```

#### Random Weight Selection

```java
RandomWeightPool<Item> pool = new RandomWeightPool<Item>(items) {
    @Override
    public int getWeight(Item item) {
        return item.getWeight();
    }
};

// Randomly select one
Item selected = pool.randomOne();

// Randomly select multiple
List<Item> selectedList = pool.randomList(3, true);
```