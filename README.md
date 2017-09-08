# jforgame
  jforgame，一个用java编写的手游服务端框架。项目只使用简单的业务功能作为演示，主要提供各种支持游戏快速开发的组件，以及对生产环境的服务进行管理的工具。
  该项目使用maven工具管理依赖及进行打包。 
  
  ## 主要使用的第三方技术  
  *  Mina，通信NIO网关  
  *  jprotobuf, 通信消息编解码方式  
  *  proxool，mysql数据库连接源  
  *  slf4j+log4j，日志系统  

  ## 模块目录
  game包是游戏的业务模块, 其余包则是各种辅助模块。允许game包调用其他模块的接口，尽量避免其他模块对game包的调用。各个模块包括:
  * cache包，使用guava cache库，用于支持系统的缓存框架　　
  * orm包，使用自定义的orm框架，用于数据库表记录与程序pojo对象的相互转换　　　
  * db包，使用独立线程，异步处理玩家及公共数据的持久化　
  * logs包，日志系统　
  * monitor包，系统监控模块，包括使用jmx对程序进行监控 　　
  * net包，包括io网关模块，玩家消息自动映射到业务模块，异步处理玩家消息的线程模型　
  * listener包，事件驱动模型  
  * game/gm包，游戏内部金手指命令  
  * game/http包，游戏运营/运维后台命令
  * utils包，各种工具类　　

  ## QuickStart  
  1. 使用git下载代码 git clone https://github.com/kingston-csj/jforgame;  
  2. 将代码导入带有maven插件的IDE;  
  3. 新建数据库game_data_001和game_user_001，并分别导入config下的同名sql文件;  
  4. 启动服务端，入口为ServerStartup类;  
  5. 启动客户端，入口为ClientStartup类;



  栏目教程请移步：--> [csdn博客](http://blog.csdn.net/column/details/16043.html)

  欢迎star/fork，欢迎学习/使用，期待一起贡献代码！！

  ## 与我交流讨论
  QQ：641711541
  
