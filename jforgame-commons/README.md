# jforgame-commons

jforgame-commons 是一个通用的工具库模块，提供了游戏服务器开发中常用的数据结构、工具类和基础组件。

## 目录结构

```
jforgame-commons/
├── ds/              # 数据结构
├── eventbus/        # 事件总线
├── persist/         # 持久化容器
├── reflection/      # 反射工具
├── schedule/        # 调度表达式解析
├── thread/          # 线程工具
├── trie/            # 字典树
└── util/            # 通用工具类
```

---

## ds - 数据结构

提供常用的数据结构实现。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `CircleQueue` | 循环队列，支持环形遍历，适用于轮询场景 |
| `ConcurrentHashSet` | 并发安全的HashSet实现 |
| `LazyCacheMap` | 懒加载缓存Map，支持按需加载和过期清理 |
| `LruHashMap` | LRU缓存Map，自动淘汰最近最少使用的元素 |
| `Pair<K, V>` | 二元组，用于存储两个关联值 |
| `Triple<A, B, C>` | 三元组，用于存储三个关联值 |

---

## eventbus - 事件总线

提供发布-订阅模式的事件通信机制，支持同步和异步事件处理。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `EventBus` | 事件总线核心类，负责事件的发布和分发 |
| `Subscribe` | 订阅注解，标记方法为事件订阅者 |
| `Subscriber` | 订阅者封装，包含订阅方法和触发条件 |
| `SubscriberRegistry` | 订阅者注册中心，管理所有订阅者 |
| `BaseEvent` | 事件基类，提供事件的基础属性 |

### 使用方式

- 使用 `@Subscribe` 注解标记订阅方法
- 通过 `EventBus.publish()` 发布事件
- 支持指定事件触发线程模式

---

## persist - 持久化容器

提供异步数据持久化机制，支持多种持久化策略。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `QueueContainer` | 队列容器，按顺序批量持久化数据 |
| `DelayContainer` | 延迟容器，支持延迟指定时间后持久化 |
| `CronContainer` | Cron容器，按Cron表达式定时持久化 |
| `QueueContainerGroup` | 队列容器组，管理多个队列容器 |
| `PersistContainer` | 持久化容器接口，定义持久化行为 |
| `SavingStrategy` | 持久化策略接口，定义何时触发持久化 |
| `Entity` | 实体接口，标记可持久化的对象 |
| `DbService` | 数据库服务接口，定义数据存储操作 |

### 持久化策略

- **队列策略**：数据进入队列，定时批量写入数据库
- **延迟策略**：数据变更后延迟一段时间再持久化，避免频繁写入
- **Cron策略**：按指定时间表达式定时持久化

---

## reflection - 反射工具

提供高性能的反射调用工具，基于MethodHandle实现。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `MethodHandleUtils` | 反射工具类，提供方法查找和调用功能 |
| `MethodCaller` | 方法调用器接口，封装方法调用逻辑 |

### 主要功能

- 支持实例方法和静态方法调用
- 支持方法调用器缓存，提高重复调用性能
- 支持自动装箱/拆箱和参数类型匹配

---

## schedule - 调度表达式解析

提供时间表达式解析功能，支持Cron表达式和自定义表达式。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `ScheduleExpressionParser` | 表达式解析器接口 |
| `ScheduleExpressionParserManager` | 解析器管理器，管理多个解析器 |
| `QuartzScheduleExpressionParser` | Quartz Cron表达式解析器 |

### 主要功能

- 解析Cron表达式，计算下次触发时间
- 支持注册自定义表达式解析器（如开服时间表达式）
- 支持判断表达式是否为周期性表达式

---

## thread - 线程工具

提供线程相关的工具类和注解。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `NamedThreadFactory` | 命名线程工厂，创建带名称的线程 |
| `ThreadSafe` | 线程安全注解，标记类为线程安全 |
| `NotThreadSafe` | 非线程安全注解，标记类为非线程安全 |

### 主要功能

- 创建可命名的线程，便于调试和监控
- 通过注解标识类的线程安全性

---

## trie - 字典树

提供字典树（Trie树）实现，用于敏感词检测和前缀匹配。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `TrieDictionary` | 字典树核心类，提供敏感词检测功能 |
| `TrieNode` | 字典树节点，存储字符和子节点 |
| `NodeContainer` | 节点容器接口，管理子节点集合 |

### 主要功能

- 添加和删除敏感词
- 检测文本是否包含敏感词
- 精确匹配敏感词
- 替换敏感词为指定字符（如`*`）
- 支持内存优化（小节点数时使用数组代替Map）

---

## util - 通用工具类

提供常用的工具类集合。

### 核心类

| 类名 | 功能说明 |
|------|----------|
| `DateUtil` | 日期工具类，提供日期计算、格式化、解析功能 |
| `TimeUtil` | 时间工具类，提供时间常量和时间单位换算 |
| `StringUtil` | 字符串工具类，提供字符串判空、首字母转换功能 |
| `FileUtil` | 文件工具类，提供文件读取、目录创建功能 |
| `JsonUtil` | JSON工具类，基于Jackson实现对象与JSON互转 |
| `DigestUtil` | 编解码工具类，提供MD5摘要、Base64编解码功能 |
| `ZipUtil` | ZIP工具类，提供字符串、字节数组、文件的压缩解压功能 |
| `RandomUtil` | 随机工具类，提供随机数生成、权重随机功能 |
| `RandomWeightPool` | 随机权重池，支持按权重随机选择元素 |
| `SplitUtil` | 分隔符工具类，提供字符串分隔和解析功能 |
| `TypeUtil` | 类型工具类，提供类型判断和兼容性检查功能 |
| `ClassScanner` | 类扫描器，扫描包下的所有类文件 |
| `NumberUtil` | 数字工具类，提供数字转换和计算功能 |

---

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>jforgame</groupId>
    <artifactId>jforgame-commons</artifactId>
    <version>${jforgame.version}</version>
</dependency>
```

### 使用示例

#### 敏感词检测

```java
TrieDictionary trie = new TrieDictionary();
trie.addNode("敏感词1");
trie.addNode("敏感词2");

// 检测是否包含敏感词
boolean contains = trie.containsWords("这是一段包含敏感词1的文本");

// 替换敏感词
String result = trie.replaceWords("这是一段包含敏感词1的文本");
// 输出: "这是一段包含*******的文本"
```

#### 事件总线

```java
EventBus eventBus = new EventBus();

// 注册订阅者
eventBus.register(new MySubscriber());

// 发布事件
eventBus.publish(new MyEvent("event data"));
```

#### 持久化容器

```java
// 创建队列持久化容器
QueueContainer<Player> container = new QueueContainer<>(playerService, 1000);

// 添加待持久化对象
container.add(player);

// 定时批量持久化
container.save();
```

#### 随机权重选择

```java
RandomWeightPool<Item> pool = new RandomWeightPool<Item>(items) {
    @Override
    public int getWeight(Item item) {
        return item.getWeight();
    }
};

// 随机选择一个
Item selected = pool.randomOne();

// 随机选择多个
List<Item> selectedList = pool.randomList(3, true);
```