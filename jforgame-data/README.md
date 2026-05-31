# jforgame-data

`jforgame-data` 是配置数据功能模块，负责提供配置表读取、容器装载、通用常量注入和数据校验等核心能力。

## 模块定位

- 不依赖 Spring Boot，只依赖 Spring Framework 基础能力
- 不负责自动装配，自动装配由 `jforgame-data-spring-boot-starter` 提供
- 运行期配置通过 `ResourceOptions` 表达，而不是 `@ConfigurationProperties` 绑定对象

## 配置对象

`ResourceOptions` 是 `jforgame-data` 对外使用的普通配置对象，包含以下信息：

- `location`：资源根目录
- `suffix`：配置文件后缀
- `tableScanPath`：配置实体扫描路径
- `containerScanPath`：配置容器扫描路径
- `commonTableName`：通用常量表名称

在 Spring Boot 环境下，starter 模块中的 `ResourceProperties` 会负责读取 `jforgame.data.*` 配置，并转换为 `ResourceOptions` 注入到 `DataManager`。
