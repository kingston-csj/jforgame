# jforgame-logger

日志模块自动适配应用选择的 SLF4J 实现：Log4j 1.x、Log4j2 或 Logback。

## 纯代码初始化

首次调用 `LoggerUtil` 或 `LoggerBuilder.getLogger()` 时，模块检测当前 SLF4J 实现，直接在内存中配置控制台、普通日志和异常日志。

`LoggerBuilder` 按类名加载当前选中的适配器，不要求引入其他框架的运行依赖。应用仍需选择一种日志实现及其 SLF4J 绑定。

不支持自定义配置文件。初始化时以模块的代码规则替换框架之前安装的配置；使用其他 SLF4J 绑定时沿用其实现。

### 默认输出规则

| 输出 | 级别 | 当前文件 | 滚动 / 保留 |
|------|------|----------|-------------|
| 控制台 | INFO 及以上 | 标准输出 | — |
| 普通日志 | INFO 及以上 | `./logs/app.log` | 按天滚动，保留 30 天 |
| 异常日志 | 仅 ERROR | `./logs/exception/exception.log` | 按天滚动，保留 30 天 |

ERROR 同时写入普通日志和异常日志；传入的异常包含堆栈。归档文件名后缀为 `yyyy-MM-dd`。历史清理随框架的滚动触发；Log4j 1.x 使用模块提供的 `RetentionDailyRollingFileAppender`，在有日志写入时每天检查一次。

```java
LoggerUtil.info("Player {} logged in", playerId);
LoggerUtil.error("Player {} not found", playerId);
LoggerUtil.error("Failed to process request", exception);
```

如果业务直接使用 `LoggerFactory`，在应用入口、创建或使用日志器之前显式初始化：

```java
LoggerBuilder.initialize();
Logger logger = LoggerFactory.getLogger(PlayerService.class);
logger.info("Service started");
```

### 日志路径

通过启动参数设置日志根目录：

```bash
java -Djforgame.log.path=/data/logs/game -jar game-server.jar
```

此时日志写入 `/data/logs/game/`

## 分类日志

系统日志和业务日志仍通过代码创建专用 appender，不写入普通日志文件。

```java
// logs/login/login.log，按天滚动
LoggerUtil.logSystem("login", "playerId=10001");

// logs/business/business.log，按小时滚动
LoggerUtil.logBusiness("{'playerId':'10001','action':'reward'}");

// 获取并复用指定分类日志器
AppLogger logger = LoggerBuilder.getLogger("payment");
logger.info("playerId=10001|amount=100");
```

## 注意事项  
使用该工具库，项目无需创建额外任何logger配置文件，例如log4j.xml或logback.xml。
如果项目使用了springboot，需要在项目初始化前设置 System.setProperty("org.springframework.boot.logging.LoggingSystem", "none")，以防止springboot自动生成的logger覆盖了框架生成的logger