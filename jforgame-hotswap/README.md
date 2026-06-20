# jforgame-hotswap

`jforgame-hotswap` 是热更新功能模块，负责在 JVM 运行期对已加载的类进行热替换（hot-swap），无需重启进程即可让业务代码改动即时生效。

## 模块定位

- 不依赖 Spring，可作为独立的类热更工具嵌入任何 Java 应用
- 基于 JVM Instrumentation（`java.lang.instrument`）与 Attach API 实现
- 分为 `jforgame-doctor`（宿主侧 API）与 `jforgame-hotswap-agent`（agent 侧执行器）两个子模块

## 工作原理

整个热更流程分为宿主侧准备和 agent 侧执行两个阶段：

1. **宿主侧**（`jforgame-doctor`）：扫描目标目录下的 `.class` 文件，解析每个类的全限定名与字节码，序列化后通过 Attach API 将 agent 加载进当前 JVM。
2. **Agent 侧**（`jforgame-hotswap-agent`）：agent 被加载后，通过反射读取宿主类 `JavaDoctor` 中的字节码数据，再调用 `Instrumentation.redefineClasses()` 完成类重定义。

```
[业务代码] --调用--> JavaDoctor.hotSwap(path)
                        │
            [扫描 .class] [序列化字节码]
                        │
            [Attach 当前 JVM + loadAgent]
                        │
                        ▼
                    MyAgent.agentmain()
                        │
            [读取 fixData] [redefineClasses]
                        │
                        ▼
                  类热更新完成
```

## 模块组成

| 子模块 | 作用 | 说明 |
|--------|------|------|
| `jforgame-doctor` | 宿主侧 API | 扫描 class 文件、管理 agent 加载，是业务调用的入口 |
| `jforgame-hotswap-agent` | Agent 侧执行器 | 通过 `premain`/`agentmain` 接管实际的类重定义操作 |

## 核心类说明

### JavaDoctor

宿主侧对外暴露的唯一入口，负责驱动整个热更流程：

```java
// 对指定目录下的 class 文件执行热更
boolean success = JavaDoctor.hotSwap("/path/to/classes");
```

- `hotSwap(filePath)` - 扫描目录、加载新类、序列化字节码，并触发 agent 执行
- `setAgentPath(path)` - 自定义 agent jar 路径（默认为 `agent/jforgame-hotswap-agent.jar`）
- 执行结果通过静态字段 `log`（日志信息）与 `exception`（异常对象）回传，便于业务感知成败

### MyAgent

Agent 侧核心，通过 `agentmain` 被 JVM 加载。其执行流程：

1. 多策略获取宿主类加载器（优先从 `Instrumentation` 已加载的类中提取，兜底使用 `SystemClassLoader`），并缓存以避免重复遍历
2. 用宿主类加载器加载 `JavaDoctor`，通过反射读取 `fixData` 字段中的类字节码
3. 解析字节码数据，逐个执行 `Instrumentation.redefineClasses()`
4. 将结果（日志或异常）回写到 `JavaDoctor` 的 `log`/`exception` 字段

### DynamicClassLoader

遵循双亲委派机制的动态类加载器：

- `loadClass(name)` - 严格遵循委派机制，已加载的类不会重复加载，仅加载新类
- `findClass(name)` - 不走委派机制，直接从字节码定义类；若类已被 AppClassLoader 加载则用当前加载器重定义，否则委托 AppClassLoader 定义
- 显式使用 `AppClassLoader` 而非上下文加载器，避免 Spring MVC 场景下被 `TomcatEmbeddedWebappClassLoader` 加载导致业务侧 `ClassNotFoundException`

## 使用方式

### 1. 引入依赖

```xml
<dependency>
    <groupId>io.github.jforgame</groupId>
    <artifactId>jforgame-doctor</artifactId>
    <version>${revision}</version>
</dependency>
```

### 2. 执行热更

```java
// 扫描指定目录下的 .class 文件并热更
String classDir = "/path/to/compiled/classes";
boolean success = JavaDoctor.hotSwap(classDir);

```

### 3. JDK 版本要求

- **JDK8**：需要额外引入 `tools.jar`（对应 `com.sun.tools.attach.VirtualMachine`）
- **JDK9+**：启动前需添加以下 VM 参数：

```
-Djdk.attach.allowAttachSelf
--add-opens java.base/java.lang=ALL-UNNAMED
```

> JDK9+ 不再需要手动引入 `tools.jar`，Attach API 已内置在 JDK 中。

## 注意事项

- `jforgame-hotswap-agent` 必须以独立 jar 形式存在，默认查找路径为工作目录下的 `agent/jforgame-hotswap-agent.jar`，如需自定义可通过 `JavaDoctor.setAgentPath()` 指定
- 热更仅支持方法体的修改（受 JVM `redefineClasses` 限制），新增/删除字段或方法签名变更需要重启进程
- agent 与宿主通过 `JavaDoctor` 的静态字段（`fixData`、`log`、`exception`）通信，请勿在热更过程中修改这些字段

