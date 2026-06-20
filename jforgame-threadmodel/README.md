# jforgame-threadmodel

`jforgame-threadmodel` 是线程模型模块，提供 Actor 模型和基于关键字分发的线程调度能力，专为游戏服务器设计。

## 模块定位

- 不感知 socket、session、message 等业务语义
- 只负责接收 `Runnable` 并按自身模型执行
- 关注并发层面的基础问题：任务排队、线程执行、关闭策略、并发冲突避免
- 支持基于 key 做 hash 分发或 Actor 调度

## 核心概念

### 线程模型接口

`ThreadModel` 是线程模型的顶层抽象，定义三个核心方法：

```java
public interface ThreadModel {
    void accept(Runnable task);    // 接收新任务
    void shutDown();               // 关闭线程模型
    boolean isShutdown();          // 检查是否已关闭
}
```

### 常见实现

| 实现类 | 适用场景 |
|--------|----------|
| `DispatchThreadModel` | 按关键字分发到固定工作线程，适合 IO 密集型任务 |
| `MonitoredDispatchThreadModel` | 带监控能力的分发模型，可检测任务超时 |
| `ActorSystem` | 通过 Actor 邮箱串行处理任务，减轻线程冷热不均 |

## Actor 模型

Actor 模型是一种并发编程模型，每个 Actor 拥有独立的邮箱，通过消息传递实现并发控制。

### 核心组件

```
ActorSystem (Actor系统)
    ├── BaseActor (Actor实例)
    │     └── Mailbox (邮箱)
    │           ├── UnboundedMailbox (无界邮箱)
    │           ├── BoundedMailbox (有界邮箱)
    │           └── PriorityMailbox (优先级邮箱)
    ├── SharedActor (共享Actor)
    └── Mail (消息)
          ├── SimpleMail (简单消息)
          └── PriorityMail (优先级消息)
```

### ActorSystem 使用示例

```java
// 创建Actor系统
ActorSystem actorSystem = new ActorSystem();

// 创建Actor
Actor playerActor = actorSystem.createActor("/player/10001");

// 发送消息
playerActor.tell(new SimpleMail("login", "ip", "192.168.1.1"));

// 获取Actor
Actor actor = actorSystem.getActor("/player/10001");

// 移除Actor（避免内存泄漏）
actorSystem.removeActor("/player/10001");

// 关闭系统
actorSystem.shutDown();
```

### 自定义 Actor

```java
public class PlayerActor extends BaseActor {
    
    @Override
    public void run() {
        // 处理邮箱中的消息
        super.run();
    }
    
    // 处理具体业务逻辑
    public void onLogin(String ip) {
        System.out.println("Player login from: " + ip);
    }
}
```

### 消息定义

```java
// 简单消息
public class LoginMail extends SimpleMail {
    public LoginMail(String ip) {
        super("login", ip);
    }
    
    @Override
    public void action() {
        // 获取当前Actor
        BaseActor receiver = (BaseActor) getReceiver();
        // 处理登录逻辑
        String ip = (String) getContent()[0];
        ((PlayerActor) receiver).onLogin(ip);
    }
}

// 优先级消息
public class HighPriorityMail extends PriorityMail {
    public HighPriorityMail(Object... content) {
        super("highPriority", HIGH_PRIORITY, content);
    }
}
```

## 消息分发模型

基于关键字分发的线程模型，预定义一组工作线程，每个线程绑定一个任务队列。

### DispatchThreadModel 使用示例

```java
// 创建分发模型
DispatchThreadModel model = new DispatchThreadModel();

// 提交任务
model.accept(new BaseDispatchTask() {
    @Override
    public void action() {
        // 业务逻辑
    }
});

// 分发到指定线程（基于 dispatchKey % 线程数）
BaseDispatchTask task = new BaseDispatchTask();
task.setDispatchKey(playerId);  // 同一玩家的任务会分发到同一线程
model.accept(task);
```

### 带监控的分发模型

`MonitoredDispatchThreadModel` 在 `DispatchThreadModel` 基础上增加了任务超时监控：

```java
// 创建监控模型，监控间隔2秒，最大执行时间1秒
MonitoredDispatchThreadModel model = new MonitoredDispatchThreadModel(
    4,                          // 线程数
    2000,                       // 监控间隔（毫秒）
    1000                        // 最大执行时间（毫秒）
);
```

当任务执行超时，会自动打印线程堆栈信息。
