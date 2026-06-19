# jforgame-logger

`jforgame-logger` is a logging module that provides classified logging functionality with automatic adaptation to multiple logging frameworks.

## Module Positioning

- Automatically detects the logging framework used by the application (Log4j, Log4j2, Logback)
- Provides classified logging capability, each log type outputs to a separate directory
- Supports JVM parameter customization for log path
- No additional configuration required, automatically adapts to underlying logging framework

## Core Features

### 1. Automatic Framework Detection

`LoggerBuilder` automatically selects the corresponding logger adapter by detecting the SLF4J binding implementation:

| Detected Framework | Implementation Class |
|-------------------|---------------------|
| Logback | `LogbackAppLogger` |
| Log4j2 | `Log4J2AppLogger` |
| Log4j (1.x) | `Log4JAppLogger` |
| Other SLF4J bindings | `Slf4JAppLogger` |

### 2. Classified Log Output

Logs recorded via `LoggerUtil.info()` are automatically classified to separate directories:

```java
// Log outputs to logs/login/login.log
LoggerUtil.info("login", "userId", 10001, "time", System.currentTimeMillis());

// Log outputs to logs/reward/reward.log
LoggerUtil.info("reward", "playerId", 10001, "itemId", 5001, "count", 10);
```

Log format is key-value pairs separated by `|`, convenient for third-party parsing:

```
userId|10001|time|1698765432100
playerId|10001|itemId|5001|count|10
```

### 3. Log Path Configuration

Default log path is `./logs`, customizable via JVM parameter:

```bash
java -Djforgame.log.path=/var/log/myapp -jar app.jar
```

### 4. Log Rolling Strategy

All log files use daily rolling strategy:

| Framework | Rolling Strategy | File Naming |
|-----------|-----------------|-------------|
| Logback | TimeBasedRollingPolicy | `name.log.%d{yyyy-MM-dd}` |
| Log4j2 | TimeBasedTriggeringPolicy | `name.log.%d{yyyy-MM-dd}` |
| Log4j | DailyRollingFileAppender | `name.log.yyyy-MM-dd` |

Logback keeps 15 days of history logs by default.

## Core Classes

### AppLogger

Logger interface defining three core methods:

```java
public interface AppLogger {
    void info(String msg);                      // Info log
    void error(String msg, Throwable throwable); // Error log with stack trace
    void error(String format, Object... arguments); // Formatted error log
}
```

### LoggerBuilder

Logger factory responsible for:

- Automatically detecting logging framework type
- Creating corresponding `AppLogger` instances
- Caching created Logger instances

### LoggerUtil

Logger utility class providing static methods:

```java
// Error log with stack trace
LoggerUtil.error("Failed to process request", e);

// Formatted error log
LoggerUtil.error("Player {} not found", playerId);

// Classified info log
LoggerUtil.info("login", "userId", 10001, "ip", "192.168.1.1");
```

## Usage Examples

### Basic Usage

```java
public class PlayerService {
    
    public void login(int userId, String ip) {
        // Record login log, automatically outputs to logs/login/login.log
        LoggerUtil.info("login", 
            "userId", userId, 
            "ip", ip, 
            "time", System.currentTimeMillis());
    }
    
    public void reward(int playerId, int itemId, int count) {
        try {
            // Business logic...
            
            // Record reward log, automatically outputs to logs/reward/reward.log
            LoggerUtil.info("reward",
                "playerId", playerId,
                "itemId", itemId,
                "count", count);
        } catch (Exception e) {
            // Error log, outputs to standard error log
            LoggerUtil.error("Reward failed for player " + playerId, e);
        }
    }
}
```

### Custom Log Path

```bash
# Specify log path at startup
java -Djforgame.log.path=/data/logs/game -jar game-server.jar
```

## Best Practices

1. **Classification Naming**: Use meaningful log type names like `login`, `payment`, `reward`
2. **Key-Value Format**: Ensure parameters are even number forming key-value pairs
3. **Path Configuration**: For production, specify absolute path via JVM parameter
4. **Framework Selection**: Recommend using Logback for best performance and complete features