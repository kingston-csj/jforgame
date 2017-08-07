# game_server
  game_server是一个用java编写的手游服务端框架，使用Mina作为IO网关。除了简单的业务功能之外，也包含各种辅助组件。使用maven工具管理依赖及打包。
  
  (A mobile game server socket framework, including all the base functions)
  
  game包是游戏的业务模块, 其余包则是各种辅助模块，包括
  * cache包，使用guava cache,用于支持系统的缓存框架　　
  * orm包，使用自定义的orm框架，用于数据库表记录与程序pojo对象的相互转换　　　
  * db包，使用独立线程，异步处理玩家及公共数据的持久化　
  * logs包，日志系统　
  * monitor包，系统监控模块，包括使用jmx对程序的监控 　　
  * net包，包括io网关模块，玩家消息自动映射到业务模块，异步处理玩家消息的线程模型　　
  * listener包，事件驱动模块 　　
  * utils包，各种工具类　　


  ServerStartup为服务端的启动入口。

  ClientStartup为客户端机器人启动入口，放到test目录下。


  栏目教程请移步：--> [csdn博客](http://blog.csdn.net/column/details/16043.html)
