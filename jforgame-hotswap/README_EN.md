# jforgame-hotswap

`jforgame-hotswap` is a hot-swap module that performs in-place hot-replacement of already-loaded classes at JVM runtime, allowing business code changes to take effect immediately without restarting the process.

## Module Positioning

- Does not depend on Spring; can be embedded into any Java application as a standalone class hot-swap tool
- Built on JVM Instrumentation (`java.lang.instrument`) and the Attach API
- Split into two sub-modules: `jforgame-doctor` (host-side API) and `jforgame-hotswap-agent` (agent-side executor)

## How It Works

The whole hot-swap flow is split into a host-side preparation phase and an agent-side execution phase:

1. **Host side** (`jforgame-doctor`): scans the `.class` files under the target directory, resolves the fully qualified name and bytecode of each class, serializes them, and loads the agent into the current JVM via the Attach API.
2. **Agent side** (`jforgame-hotswap-agent`): once loaded, the agent reads the bytecode data from the host class `JavaDoctor` via reflection, then invokes `Instrumentation.redefineClasses()` to complete the class redefinition.

```
[Business code] --calls--> JavaDoctor.hotSwap(path)
                        |
            [scan .class] [serialize bytecode]
                        |
            [Attach current JVM + loadAgent]
                        |
                        v
                  MyAgent.agentmain()
                        |
            [read fixData] [redefineClasses]
                        |
                        v
                class hot-swap finished
```

## Module Composition

| Sub-module | Role | Description |
|------------|------|-------------|
| `jforgame-doctor` | Host-side API | Scans class files, manages agent loading; the entry point for business calls |
| `jforgame-hotswap-agent` | Agent-side executor | Takes over the actual class redefinition via `premain`/`agentmain` |

## Core Classes

### JavaDoctor

The only entry point exposed on the host side, responsible for driving the whole hot-swap flow:

```java
// Hot-swap the class files under a given directory
boolean success = JavaDoctor.hotSwap("/path/to/classes");
```

- `hotSwap(filePath)` - scans the directory, loads new classes, serializes the bytecode, and triggers the agent
- `setAgentPath(path)` - customizes the agent jar path (defaults to `agent/jforgame-hotswap-agent.jar`)
- The result is reported back via the static fields `log` (log message) and `exception` (exception object), so the business code can tell success from failure

### MyAgent

The agent-side core, loaded by the JVM via `agentmain`. Its execution flow:

1. Obtains the host classloader through multiple strategies (prefers extracting it from classes already loaded by `Instrumentation`, falling back to `SystemClassLoader`), and caches it to avoid repeated iteration
2. Loads `JavaDoctor` with the host classloader and reads the class bytecode from the `fixData` field via reflection
3. Parses the bytecode data and invokes `Instrumentation.redefineClasses()` for each class
4. Writes the result (log or exception) back to the `log`/`exception` fields of `JavaDoctor`

### DynamicClassLoader

A dynamic classloader that follows the parent-delegation model:

- `loadClass(name)` - strictly follows delegation; classes already loaded are not loaded again, only new classes are loaded
- `findClass(name)` - bypasses delegation and defines the class directly from bytecode; if the class has already been loaded by the AppClassLoader it is redefined with the current loader, otherwise it is delegated to the AppClassLoader
- Explicitly uses the `AppClassLoader` instead of the context classloader to avoid being loaded by `TomcatEmbeddedWebappClassLoader` in a Spring MVC scenario, which would cause a `ClassNotFoundException` on the business side

### ClassFileMeta

Reads the bytecode data and the fully qualified class name of a class file with the help of ASM. ASM is used instead of `com.sun.tools.classfile` for JDK9+ compatibility (the latter is no longer open to external code).

## Usage

### 1. Add the dependency

```xml
<dependency>
    <groupId>io.github.jforgame</groupId>
    <artifactId>jforgame-doctor</artifactId>
    <version>${revision}</version>
</dependency>
```

### 2. Run a hot-swap

```java
// Scan the .class files under a given directory and hot-swap them
String classDir = "/path/to/compiled/classes";
boolean success = JavaDoctor.hotSwap(classDir);

```

### 3. JDK version requirements

- **JDK8**: requires the extra `tools.jar` (provides `com.sun.tools.attach.VirtualMachine`)
- **JDK9+**: add the following VM options before startup:

```
-Djdk.attach.allowAttachSelf
--add-opens java.base/java.lang=ALL-UNNAMED
```

> On JDK9+ there is no need to add `tools.jar` manually; the Attach API is already bundled with the JDK.

## Notes

- `jforgame-hotswap-agent` must exist as a standalone jar. The default lookup path is `agent/jforgame-hotswap-agent.jar` under the working directory; customize it with `JavaDoctor.setAgentPath()` if needed
- Hot-swap only supports changes to a method body (limited by the JVM `redefineClasses`); adding/removing fields or changing method signatures requires a process restart
- The agent and the host communicate through the static fields of `JavaDoctor` (`fixData`, `log`, `exception`); do not modify these fields during a hot-swap



