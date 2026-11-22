  ## 中文 | [English](README_EN.md)  

  ## 项目介绍　　
  jforgame，是一个用java编写的轻量级高性能手游服务端框架。项目提供各种支持快速二次开发的组件，以及对生产环境的服务进行管理的工具。同时，为了使用户能够快速上手，项目提供了若干常用业务功能作为演示。

  ## 项目特点  
  * 搭配框架博客栏目教程，快速理解项目模块原理  
  * 支持socket/webSocket接入，完美适配手游/页游/H5/小游戏服务端架构  
  * 配套c#版本的网络框架，兼容websocket/socket,兼容json/struct。
  * 通信协议支持json/protobuf或普通javabean，为客户端提供多种选择 
  * 一键导出csharp/typescript等客户端语言的通信协议，参考CSharpProtocolGenerator工具
  * 内置跨进程通信，强大的客户端异步/同步api，轻松实现跨服业务
  * 使用自定义的轻量级orm工具库，支持多数据源，自动建表增加字段，支持表字段全量/增量更新
  * 框架提供多种组件，可以直接二次开发业务逻辑  
  * 不停机热更代码，不停机热更配置，运维，运营不掉线
  * 喜欢Go语言，这里也支持  --> [Go版游戏服务器](https://github.com/kingston-csj/gforgame)  
  * 基于springboot的jforgame实操项目  --> [mmorpg游戏服务器](https://github.com/kingston-csj/mmorpg)  
  * 有独立http管理后台网站，为游戏运维/运营提供支持  --> [后台管理系统](https://github.com/kingston-csj/gamekeeper)  
  * 除了做游戏，也可以用来开发其他网络应用，例如实时聊天  --> [仿QQ聊天应用](https://github.com/kingston-csj/im)  


  ## 模块组织结构  
  ``` git
  jforgame
  ├── jforgame-commons --基础公共服务  
  ├── jforgame-threadmodel --线程模型，分为actor和按关键字分发两种实现。推荐actor，可避免线程“冷热不均”
  ├── jforgame-runtime --应用运行时监控数据，包括内存，线程，类等等
  ├── jforgame-socket-parent     --Tcp socket通信，包括io网关模块，消息路由，会话管理，包含netty和mina版本      
      ├── jforgame-socket-core    --服务端/客户端基础核心库
      ├── jforgame-socket-netty  --netty版实现，包含Socket/WebSocket，包含服务端与客户端
      ├── jforgame-socket-mina   --mina版实现， 只包含Socket服务端与客户端， 不支持WebSocket相关
  ├── jforgame-orm     --专门为游戏服务器定制的orm库，用于数据库表记录与程序pojo对象的相互转换        
  ├── jforgame-cache   --实体缓存 API，为实现了 Entity 接口的实体提供「缓存 + 持久化」的统一读写入口，不绑定任何容器或 ORM
  ├── jforgame-cache-spring-boot-starter --jforgame-cache 的 Spring Boot 集成，提供 @EntityCacheAutowired 自动注入、SpringCacheStore、JpaDatabaseAccessor 自动装配
  ├── jforgame-persist --异步数据持久化模块，基于队列提供高吞吐、低延迟的数据落库能力，支持死信队列机制
  ├── jforgame-data    --配置数据功能模块，支持csv/excel/json/数据库多种数据源、包含数据校验、热更新和二级缓存能力       
  ├── jforgame-data-spring-boot-starter    --jforgame-data 的 Spring Boot starter，负责属性绑定和自动装配       
  ├── jforgame-hotswap  --支持游戏业务热更新
  ├── jforgame-codec-parent         --用于socket通信的数据编解码  
      ├── jforgame-codec-api        --消息编解码API接口
      ├── jforgame-codec-protobuf   --protobuf实现
      ├── jforgame-codec-struct     --普通javabean，反射实现，启用高级版本可支持集合元素类型异构  
  ├── jforgame-demo  --游戏业务示例  
  ```
jforgame采用组件化的方式，每个模块都是一个独立的组件，用户可以根据自己的需求选择导入目标模块。 
例如：如果只需要使用socket通信，那么只需要导入jforgame-socket-netty模块或者jforgame-socket-mina。


  ## 核心模块功能简介

  ### jforgame-cache —— 实体缓存 API

  为实现了 `jforgame.commons.persist.Entity` 的实体提供「缓存 + 持久化」的统一读写入口。核心模块不绑定任何容器或 ORM，通过 SPI 接口与具体实现解耦。

  **核心 API**

  | 接口/类 | 说明 |
  | --- | --- |
  | `EntityCacheService<E>` | 实体缓存服务顶层接口：`getEntity` / `putEntity` / `removeEntity` |
  | `BaseEntityCacheService<E>` | 默认实现，构造器注入依赖；额外提供分段锁的 `getOrCreate` |
  | `DatabaseAccessor` | 数据库访问 SPI，由业务层实现；约定 `load` / `saveOrUpdate` / `remove` |
  | `CacheStore` | 缓存存储 SPI，可替换为任意缓存实现（ConcurrentHashMap、Spring Cache、LRU 等） |
  | `EntityCacheServiceFactory` | 工厂接口，按实体类型复用服务实例 |

  **并发行为**：`getOrCreate` 使用 10 个分段 `ReentrantLock`（按 `id.hashCode()` 取模）做双重检查，避免同一主键并发创建。

  详细用法与 SPI 约定见 [jforgame-cache/README.md](jforgame-cache/README.md)。

  ### jforgame-cache-spring-boot-starter —— Spring Boot 集成

  在 Spring Boot 环境下使用 jforgame-cache，提供自动装配与注解注入能力。

  **核心特性**

  - `@EntityCacheAutowired` —— 标注在 `BaseEntityCacheService<E>` 字段上，按泛型自动注入对应实体的缓存服务
  - `SpringCacheStore` —— 把实体缓存委托给 Spring `CacheManager`，可与 Caffeine、Redis 等共用一套配置
  - `JpaDatabaseAccessor` —— 类路径存在 JPA + Hibernate 且容器中有唯一 `EntityManagerFactory` 时自动注册
  - 自动配置 —— `EntityCacheAutoConfiguration` 按条件注册 `EntityCacheServiceFactory` / `SpringCacheStore` / `LocalCacheStore`

  **CacheStore 选择优先级**：应用自定义 Bean → 类路径存在 `CacheManager` 则用 `SpringCacheStore` → 回退 `LocalCacheStore`（仅演示）。生产环境推荐 `SpringCacheStore` 或 `LruSoftCacheStore`。

  详细用法见 [jforgame-cache-spring-boot-starter/README.md](jforgame-cache-spring-boot-starter/README.md)。

  ### jforgame-persist —— 异步数据持久化

  基于队列的异步数据持久化机制，支持高吞吐、低延迟的数据落库场景（如游戏玩家数据存档）。

  **容器类型**

  | 容器 | 说明 | 适用场景 |
  | --- | --- | --- |
  | `QueueContainer` | 基于 `BlockingQueue` 的 FIFO 容器 | 通用异步持久化，单线程消费 |
  | `DelayContainer` | 带延迟时间的容器 | 非实时数据（如玩家离线后延迟存档） |
  | `CronContainer` | 基于 Quartz Cron 表达式定时容器 | 周期性批量持久化 |
  | `PersistContainerGroup` | 容器组 + `ShardingStrategy` 分片 | 高并发场景，多线程并行持久化 |

  **死信队列机制**

  持久化失败时引入死信队列（DLQ），避免无限重试占用线程资源：

  - 可配置 `maxRetryCount`，超过重试次数的实体移入死信队列
  - `DeadLetterListener` 接收死信事件（可接入告警系统）
  - 支持单个恢复 `reprocess(key)`、批量恢复 `batchReprocess()`、清空 `reset()`

  ```java
  DeadLetterQueue dlq = new DeadLetterQueue(3);
  dlq.addListener(deadLetter -> log.error("持久化失败: {}", deadLetter.getKey()));
  QueueContainer container = new QueueContainer("player", strategy, dlq);
  container.receive(playerEntity);
  ```

  完整流程图与最佳实践见 [jforgame-persist/README.md](jforgame-persist/README.md)。


  ## 快速开始  
  1. 各模块demo教程 --> [wiki](https://github.com/kingston-csj/jforgame/wiki/Examples)  
  2. 使用git下载代码 git clone https://github.com/kingston-csj/jforgame;  
  3. 将代码导入带有maven插件的IDE(选择根目录下的pom.xml文件);  
  4. 新建数据库game_data_001和game_user_001，并分别导入test/resources下的同名sql文件;  
  5. 启动服务端，入口为ServerStartup类;    
  （如果导入项目所有模块，还需要设置好工作区间。例如idea设置：run->EditConfirations->Workingdirectory,设置为，**\jforgame\jforgame-demo。）;  
  6. 启动客户端，入口为ClientStartup类;  
  （如果导入项目所有模块，还需要设置好工作区间。例如idea设置：run->EditConfirations->Workingdirectory,设置为，**\jforgame\jforgame-demo。)  
  7. 项目demo

  导入组件
  ```
    <dependency>
        <groupId>io.github.jforgame</groupId>
        <artifactId>jforgame-socket-netty</artifactId>
        <version>latest</version>
    </dependency>
    <dependency>
        <groupId>io.github.jforgame</groupId>
        <artifactId>jforgame-codec-struct</artifactId>
        <version>latest</version>
    </dependency>
  ```  
 
  一行代码启动服务器(socket/websocket)
  ```
   TcpSocketServerBuilder.newBuilder()
    .bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
    .setMessageFactory(GameMessageFactory.getInstance())
    .setMessageCodec(new StructMessageCodec())
    .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
    .build()
    .start();
  ```  

  消息协议
  ```
  @MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.REQ_LOGIN)
  public class ReqAccountLogin implements Message {

    /**
     * 账号流水号
     */
    private long accountId;

    private String password;
    
    }
  ```

  消息路由
  ```
  @MessageRoute
  public class LoginController {

	@RequestHandler
	public void reqAccountLogin(IdSession session, ReqAccountLogin request) {
        GameContext.loginManager.handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}
  }
  ```
  就是这么简单，写业务只需要依样画葫芦，撸起袖子就是干！！

  ## 本栏目详细教程   
  [从零开始搭建游戏服务器框架](https://blog.csdn.net/littleschemer/category_9269220.html)  
  [漫谈游戏服务器](https://blog.csdn.net/littleschemer/category_12576391.html)    

  ## 一起交流  
  欢迎star/fork，欢迎学习/使用，期待一起贡献代码！！
  如果您发现bug，或者有任何疑问，请提交issue !! bug会被及时修复，并及时发布新的补丁release。  
  承诺：持续迭代，永久维护！！  

  mysql合服工程，基于jforgame的分布式五子棋源代码(cocos+h5客户端)，私聊获取。  
  
  合作/咨询：+Q 641711541  
  我刚开通了知识星球，快来瞧一瞧吧~~  
  (加入星主游戏开发微信群，随时随地交流提问~~)  
  ![](/screenshots/zsxq.jpg "知识星球")


   ##  贡献代码
    - 非常欢迎大家一起来维护这个项目，提交代码的时候请按以下规范
    ```
    feat[模块]：新增功能
    fix[模块]：修复缺陷/BUG
    test[模块]：新增/调整测试用例
    doc[模块]：补充、更新文档
    refactor[模块]：代码重构/结构调整
    upgrade[模块]：升级依赖/插件版本
    clean[模块]：移除废弃工具方法与冗余代码
    ```
e
   ## 特别鸣谢
   感谢网友[CarsonQizhao](https://github.com/CarsonQizhao)提供的commons多个优秀的工具类;  
   感谢网友[wupeng0528](https://github.com/wupeng0528)为threadmodel模块引入Akka风格的Actor配置系统;    
   感谢网友[fagarine](https://github.com/fagarine)解决hotswap在jdk17环境不生效的问题;    
   以及其他所有参与项目的网友（包括并不限于提出issue，在聊天讨论提出建议或bug），万分感谢！！ 

   ## 免责申明
   本项目只用于学习研究，禁止用于非法获利和商业活动。如产生法律纠纷与作者无关！！
