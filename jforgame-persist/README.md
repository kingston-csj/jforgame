# 异步数据持久化模块 (jforgame-persist)

## 概述

该模块提供了一套基于队列的异步数据持久化机制，支持高吞吐、低延迟的数据落库场景（如游戏玩家数据存档、订单处理等）。

## 核心组件

### 容器 (Container)

| 类 | 描述 | 适用场景 |
|---|---|---|
| `QueueContainer` | 基于 `BlockingQueue` 的 FIFO 持久化容器 | 通用异步持久化，单线程消费 |
| `DelayContainer` | 带延迟时间的持久化容器 | 非实时数据（如玩家离线后延迟存档） |
| `CronContainer` | 基于 Quartz Cron 表达式的定时持久化容器 | 周期性批量持久化 |
| `PersistContainerGroup` | 容器组（分片），使用 `ShardingStrategy` 路由到多个子容器 | 高并发场景，多线程并行持久化 |

### 策略 (Strategy)

| 类/接口 | 描述 |
|---|---|
| `SavingStrategy` | 持久化策略接口，定义 `doSave(Entity)` 行为 |
| `ShardingStrategy` | 分片策略接口，定义实体路由规则 |
| `HashShardingStrategy` | 基于实体 ID 的 Hash 分片实现 |

### 死信机制 (Dead Letter)

| 类 | 描述 |
|---|---|
| `DeadLetterQueue` | 死信管理类，包含重试计数、死信存储、监听器通知等。作为容器的**可选参数**，允许为 `null`（`null` 时退化为无限重试） |
| `DeadLetter` | 死信数据结构，存储失败实体的快照及元信息 |
| `DeadLetterListener` | 死信回调接口，用于接收死信事件反馈（告警、监控等） |

---

## 死信队列 (Dead Letter Queue) 机制

### 设计背景

当实体持久化失败（例如数据库字段类型不匹配、SQL 语法错误等），如果一直无限制地重试，不仅毫无意义，还会占用线程资源、污染日志。受消息中间件（MQ）的死信队列设计启发，本模块引入了 `DeadLetterQueue` 机制。

### 核心流程图

```
业务层
┌─────────────────────────────────────────────┐
│  container.receive(entity)                  │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│  key 是否在死信队列?                         │
└─────────────────────────────────────────────┘
           │YES                          │NO
           ▼                             ▼
┌───────────────────────┐   ┌───────────────────────────────┐
│ 更新死信中 Entity 快照 │   │ 正常入队，等待工作线程消费     │
│ (不重新入队)           │   └───────────────────────────────┘
└───────────────────────┘                             │
                                                     ▼
                                             ┌───────────────────────┐
                                             │ savingStrategy.doSave  │
                                             └───────────────────────┘
                                                       │
                                              ┌────────┴────────┐
                                              ▼                 ▼
                                          成功               异常
                                              │                 │
                                              ▼                 ▼
                                  ┌──────────────┐   ┌───────────────────────┐
                                  │ 重置重试计数  │   │ handleSaveFailure     │
                                  └──────────────┘   └───────────────────────┘
                                                           │
                                                           ▼
                                                  ┌─────────────────────────┐
                                                  │ 重试次数 < maxRetryCount?│
                                                  └─────────────────────────┘
                                                           │YES              │NO
                                                           ▼                 ▼
                                          ┌────────────────────┐   ┌───────────────────────┐
                                          │ 重试计数+1，重新入队│   │ 移入死信队列          │
                                          └────────────────────┘   └───────────────────────┘
                                                                                   │
                                                                                   ▼
                                                                       ┌───────────────────────┐
                                                                       │ 通知 DeadLetterListener│
                                                                       └───────────────────────┘
                                                                                   │
                                                                                   ▼
                                                                       ┌───────────────────────┐
                                                                       │ 等待人工干预           │
                                                                       └───────────────────────┘
                                                                                   │
                                                        ┌──────────────────────────┼──────────────────────────┐
                                                        ▼                          ▼                          ▼
                                              ┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐
                                              │ 修复根因          │    │ dlq.batchReprocess│    │ dlq.reset          │
                                              │ dlq.reprocess     │    │ 批量恢复所有死信   │    │ 清空所有死信和状态 │
                                              └───────────────────┘    └───────────────────┘    └───────────────────┘
                                                        │                          │                          │
                                                        ▼                          ▼                          ▼
                                              ┌───────────────────────────────────────────────────────────┐
                                              │              重新持久化                                   │
                                              └───────────────────────────────────────────────────────────┘
                                                                                   │
                                                                         ┌────────┴────────┐
                                                                         ▼                 ▼
                                                                     成功               失败
                                                                         │                 │
                                                                         ▼                 ▼
                                                            ┌──────────────────────┐   ┌───────────────────────┐
                                                            │ 从死信移除，恢复成功 │   │ 放回死信，重试计数+1  │
                                                            └──────────────────────┘   └───────────────────────┘
```

## 快速开始

### 1. 基本用法 (无死信)

```java
SavingStrategy strategy = new OrmDbStrategy();
QueueContainer container = new QueueContainer("player", strategy);

// 业务逻辑中
container.receive(playerEntity);
```

### 2. 启用死信机制

```java
// 创建死信管理器，设置最大重试次数为 3
DeadLetterQueue dlq = new DeadLetterQueue(3);

// 注册监听器（开发者反馈入口）
dlq.addListener(new DeadLetterListener() {
    @Override
    public void onDeadLetter(DeadLetter deadLetter) {
        // 发送告警
        log.error("[DLQ] 实体持久化失败: key={}, class={}, retries={}, error={}",
            deadLetter.getKey(),
            deadLetter.getEntityClassName(),
            deadLetter.getRetryCount(),
            deadLetter.getLastErrorMessage());
        alertService.alert("数据库持久化异常", deadLetter);
    }

    @Override
    public void onDeadLetterReprocessed(DeadLetter deadLetter, boolean success) {
        // 重试结果通知
    }
});

// 创建带死信的容器
QueueContainer container = new QueueContainer("player", strategy, dlq);
```

### 3. 死信恢复

```java
// 查看当前死信
List<DeadLetter> deadLetters = dlq.getDeadLetters();
log.info("当前死信数量: {}", deadLetters.size());

// 单个恢复
boolean success = dlq.reprocess("PlayerEnt@1001", strategy);

// 批量恢复
DeadLetterQueue.BatchReprocessResult result = dlq.batchReprocess(strategy);
log.info("批量恢复结果: {}", result);
// BatchReprocessResult{total=5, success=4, fail=1, failedKeys=[PlayerEnt@999]}

// 清空所有死信 (谨慎使用)
dlq.reset();
```

### 4. 使用分片容器组

```java
DeadLetterQueue dlq = new DeadLetterQueue(3);

// 使用默认的 HashShardingStrategy
PersistContainerGroup group = PersistContainerGroup.<PlayerEnt>builder()
    .name("player-group")
    .savingStrategy(strategy)
    .workers(4)
    .deadLetterQueue(dlq)
    .build();

```

---

## 最佳实践

1. **合理设置重试次数**：建议 `maxRetryCount` 在 3-5 之间，既能抵御瞬时故障，又能快速发现持久性错误。
2. **注册 DeadLetterListener**：通过告警系统（钉钉、邮件等）及时通知开发人员。
3. **定期巡检**：在运维后台展示死信列表，便于快速发现和处理问题。
4. **批量恢复**：根因修复后，优先使用 `batchReprocess()` 一次性恢复所有积压数据。
5. **快照更新**：业务侧持续调用 `receive()` 更新死信中的实体快照，确保数据不丢失。
6. **动态调整**：通过 `setMaxRetryCount()` 在特殊时期临时调整重试次数。
