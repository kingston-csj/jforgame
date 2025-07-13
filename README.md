  ## 中文 | [English](README_EN.md)  

  ## 项目介绍　　
  jforgame，是一个用java编写的轻量级高性能手游服务端框架。项目提供各种支持快速二次开发的组件，以及对生产环境的服务进行管理的工具。同时，为了使用户能够快速上手，项目提供了若干常用业务功能作为演示。

  ## 项目特点  
  * 搭配框架博客栏目教程，快速理解项目模块原理  
  * 支持socket/webSocket接入，完美适配手游/页游/H5/小游戏服务端架构  
  * 通信协议支持protobuf或普通javabean，为客户端提供多种选择  
  * 强大的客户端异步/同步api，轻松实现跨进程通信
  * 使用自定义的轻量级orm工具库，支持多数据源，自动建表增加字段，支持表字段全量/增量更新
  * 框架提供多种组件，可以直接二次开发业务逻辑  
  * 不停机热更代码，不停机热更配置，运维，运营不掉线
  * 喜欢Go语言，这里也支持  --> [Go版游戏服务器](https://github.com/kingston-csj/gforgame)  
  * 有独立http管理后台网站，为游戏运维/运营提供支持  --> [后台管理系统](https://github.com/kingston-csj/gamekeeper)  
  * 除了做游戏，也可以用来开发其他网络应用，例如实时聊天  --> [仿QQ聊天应用](https://github.com/kingston-csj/im)  


  ## 模块组织结构  
  ``` git
  jforgame
  ├── jforgame-commons --基础公共服务  
  ├── jforgame-runtime --应用运行时监控数据，包括内存，线程，类等等
  ├── jforgame-socket-parent     --Tcp socket通信，包括io网关模块，消息路由，会话管理，包含netty和mina版本      
      ├── jforgame-socket-api    --服务端/客户端基础API接口
      ├── jforgame-socket-netty  --netty版实现，包含简易WebSocket
      ├── jforgame-socket-mina   --mina版实现
  ├── jforgame-orm     --使用自定义精心定制的orm库，用于数据库表记录与程序pojo对象的相互转换        
  ├── jforgame-spring-boot-starter-data    --以springboot的starter模式封装对配置数据的读取，支持csv，excel等文件格式。支持配置数据热更新,支持二级缓存。       
  ├── jforgame-hotswap  --支持游戏业务热更新
  ├── jforgame-codec-parent         --用于socket通信的数据编解码  
      ├── jforgame-codec-api        --消息编解码API接口
      ├── jforgame-codec-protobuf   --protobuf实现
      ├── jforgame-codec-struct     --普通javabean，反射实现  
  ├── jforgame-demo  --游戏基础组件以及业务逻辑模块  
  |    ├──  cache包，使用guava cache库，用于支持系统的缓存框架    
  |    ├──  db包，使用独立线程，异步处理玩家及公共数据的持久化  
  |    ├──  monitor包，系统监控模块，包括使用jmx对程序进行监控  
  |    ├──  listener包，事件驱动模型  
  |    ├──  doctor包，采用Groovy执行任意动态代码，或JDK的instrument机制修改类方法体 
  |    ├──  cross包，跨服赛事的通信基础 
  |    ├──  game/gm包，游戏内部金手指命令
  |    ├──  game/admin包，游戏运营/运维后台命令  
  |    ├──  redis包，跨服通信（比如跨服排行榜）  
  |    ├──  tools包，简化项目开发的辅助小工具  
  |    └──  utils包，各种工具类    
  ```

  ## 快速开始  
  1. 各模块demo教程 --> [wiki](https://github.com/kingston-csj/jforgame/wiki/Examples)  
  2. 使用git下载代码 git clone https://github.com/kingston-csj/jforgame;  
  3. 将代码导入带有maven插件的IDE(选择根目录下的pom.xml文件);  
  4. 新建数据库game_data_001和game_user_001，并分别导入test/resources下的同名sql文件;  
  5. 启动服务端，入口为ServerStartup类;    
  （如果导入项目所有模块，还需要设置好工作区间。例如idea设置：run->EditConfirations->Workingdirectory,设置为，**\jforgame\jforgame-demo。）;  
  6. 启动客户端，入口为ClientStartup类;  
  （如果导入项目所有模块，还需要设置好工作区间。例如idea设置：run->EditConfirations->Workingdirectory,设置为，**\jforgame\jforgame-demo。)  
  7. 作为组件导入
  ```
    <dependency>
        <groupId>io.github.jforgame</groupId>
        <artifactId>jforgame-socket-netty</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.jforgame</groupId>
        <artifactId>jforgame-codec-struct</artifactId>
        <version>2.3.0</version>
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

  ## 本栏目详细教程   
  [从零开始搭建游戏服务器框架](https://blog.csdn.net/littleschemer/category_9269220.html)  
  [漫谈游戏服务器](https://blog.csdn.net/littleschemer/category_12576391.html)


  ## 一起交流  
  欢迎star/fork，欢迎学习/使用，期待一起贡献代码！！
  如果您发现bug，或者有任何疑问，请提交issue !!  
  mysql合服工程，基于jforgame的分布式五子棋源代码，私聊获取。  
  合作/咨询：+Q 641711541  
  我刚开通了知识星球，快来瞧一瞧吧~~  
  定时更新基础业务模块开发，付费用户可向星主索取整套可运行源码~~  
  ![](/screenshots/zsxq.jpg "知识星球")

   ## 免责申明
   本项目只用于学习研究，禁止用于非法获利和商业活动。如产生法律纠纷与作者无关！！
