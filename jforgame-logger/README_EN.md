# jforgame-logger

This module adapts to the application's SLF4J backend: Log4j 1.x, Log4j2, or Logback.

## In-memory initialization

The first call to `LoggerUtil` or `LoggerBuilder.getLogger()` detects the active SLF4J backend and configures console, application, and exception appenders directly in memory. No XML configuration files or `logs/config/` directory are generated.

`LoggerBuilder` loads only the selected adapter by class name. Applications need one logging implementation and its SLF4J binding, without dependencies on the other backends.

Custom configuration files are not supported. Initialization replaces the backend's existing configuration with the module's code-defined rules. Other SLF4J bindings retain their own implementation.

### Default output

| Destination | Level | Active file | Rotation / retention |
|-------------|-------|-------------|----------------------|
| Console | INFO and above | Standard output | — |
| Application log | INFO and above | `./logs/app.log` | Daily / 30 days |
| Exception log | ERROR only | `./logs/exception/exception.log` | Daily / 30 days |

ERROR events appear in both log files, including stack traces when an exception is supplied. Archive names use a `yyyy-MM-dd` suffix. Cleanup runs as part of rollover; the supplied `RetentionDailyRollingFileAppender` for Log4j 1.x checks once per day when an event is written.

```java
LoggerUtil.info("Player {} logged in", playerId);
LoggerUtil.error("Player {} not found", playerId);
LoggerUtil.error("Failed to process request", exception);
```

When using SLF4J directly, initialize the module at application startup before creating or using loggers:

```java
LoggerBuilder.initialize();
Logger logger = LoggerFactory.getLogger(PlayerService.class);
logger.info("Service started");
```

The demo's `ServerStartup` includes this initialization. Its former XML files are preserved in this module's `examples/` directory as configuration structure references, outside the demo classpath.

### Log directory

Set the root directory at startup:

```bash
java -Djforgame.log.path=/data/logs/game -jar game-server.jar
```

Logs are written to `/data/logs/game/`. No configuration files are generated.

## Classified logs

System and business loggers still create dedicated appenders programmatically. Their events do not propagate to the application log.

```java
// logs/login/login.log, daily rotation
LoggerUtil.logSystem("login", "playerId=10001");

// logs/business/business.log, hourly rotation
LoggerUtil.logBusiness("{'playerId':'10001','action':'reward'}");

AppLogger logger = LoggerBuilder.getLogger("payment");
logger.info("playerId=10001|amount=100");
```

Classified logs write supplied text directly. `info()` and `error()` use SLF4J `{}` placeholder formatting.
