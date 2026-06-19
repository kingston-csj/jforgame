# jforgame-logger

`jforgame-logger` 是日志模块，提供分类日志功能，支持多种日志框架的自动适配。

## 模块定位

- 自动检测应用程序使用的日志框架（Log4j、Log4j2、Logback）
- 提供分类日志能力，每种日志类型输出到独立目录
- 支持 JVM 参数自定义日志路径
- 无需额外配置，自动适配底层日志框架

## 核心功能

### 1. 自动框架检测

`LoggerBuilder` 通过检测 SLF4J 绑定的底层实现，自动选择对应的日志适配器：

| 检测到的框架 | 使用的实现类 |
|-------------|-------------|
| Logback | `LogbackAppLogger` |
| Log4j2 | `Log4J2AppLogger` |
| Log4j (1.x) | `Log4JAppLogger` |
| 其他 SLF4J 绑定 | `Slf4JAppLogger` |

### 2. 分类日志输出

通过 `LoggerUtil.info()` 记录的日志会自动分类到独立目录：

```java
// 日志输出到 logs/login/login.log
LoggerUtil.info("login", "userId", 10001, "time", System.currentTimeMillis());

// 日志输出到 logs/reward/reward.log
LoggerUtil.info("reward", "playerId", 10001, "itemId", 5001, "count", 10);
```

日志格式为键值对，用 `|` 分隔，方便第三方解析：

```
userId|10001|time|1698765432100
playerId|10001|itemId|5001|count|10
```

### 3. 日志路径配置

默认日志路径为 `./logs`，可通过 JVM 参数自定义：

```bash
java -Djforgame.log.path=/var/log/myapp -jar app.jar
```

### 4. 日志滚动策略

所有日志文件采用按天滚动策略：

| 框架 | 滚动策略 | 文件命名 |
|------|---------|---------|
| Logback | TimeBasedRollingPolicy | `name.log.%d{yyyy-MM-dd}` |
| Log4j2 | TimeBasedTriggeringPolicy | `name.log.%d{yyyy-MM-dd}` |
| Log4j | DailyRollingFileAppender | `name.log.yyyy-MM-dd` |

Logback 默认保留 15 天历史日志。

## 核心类说明

### AppLogger

日志接口，定义三个核心方法：

```java
public interface AppLogger {
    void info(String msg);                      // 信息日志
    void error(String msg, Throwable throwable); // 异常日志（带堆栈）
    void error(String format, Object... arguments); // 格式化错误日志
}
```

### LoggerBuilder

日志工厂，负责：

- 自动检测日志框架类型
- 创建对应的 `AppLogger` 实例
- 缓存已创建的 Logger 实例

### LoggerUtil

日志工具类，提供静态方法：

```java
// 错误日志（带堆栈）
LoggerUtil.error("Failed to process request", e);

// 错误日志（格式化）
LoggerUtil.error("Player {} not found", playerId);

// 分类信息日志
LoggerUtil.info("login", "userId", 10001, "ip", "192.168.1.1");
```

## 使用示例

### 基本用法

```java
public class PlayerService {
    
    public void login(int userId, String ip) {
        // 记录登录日志，自动输出到 logs/login/login.log
        LoggerUtil.info("login", 
            "userId", userId, 
            "ip", ip, 
            "time", System.currentTimeMillis());
    }
    
    public void reward(int playerId, int itemId, int count) {
        try {
            // 业务逻辑...
            
            // 记录奖励日志，自动输出到 logs/reward/reward.log
            LoggerUtil.info("reward",
                "playerId", playerId,
                "itemId", itemId,
                "count", count);
        } catch (Exception e) {
            // 错误日志，输出到标准错误日志
            LoggerUtil.error("Reward failed for player " + playerId, e);
        }
    }
}
```

### 自定义日志路径

```bash
# 启动时指定日志路径
java -Djforgame.log.path=/data/logs/game -jar game-server.jar
```

## 最佳实践

1. **分类命名**：使用有意义的日志类型名称，如 `login`、`payment`、`reward`
2. **键值对格式**：确保参数为偶数个，形成键值对
3. **路径配置**：生产环境建议通过 JVM 参数指定绝对路径
4. **框架选择**：推荐使用 Logback，性能最佳且功能完善