# game_server
  game_server，一个用java编写的手游服务端框架。项目只使用简单的业务功能作为演示，最重要的是提供各种支持游戏快速开发的组件，以及对生产环境的服务进行管理的工具。
  该项目使用Mina作为IO网关，使用maven工具管理依赖及进行打包。  

  game包是游戏的业务模块, 其余包则是各种辅助模块。允许game包调用其他模块的接口，尽量避免其他模块对game包的调用。各个模块包括:
  * cache包，使用guava cache库，用于支持系统的缓存框架　　
  * orm包，使用自定义的orm框架，用于数据库表记录与程序pojo对象的相互转换　　　
  * db包，使用独立线程，异步处理玩家及公共数据的持久化　
  * logs包，日志系统　
  * monitor包，系统监控模块，包括使用jmx对程序进行监控 　　
  * net包，包括io网关模块，玩家消息自动映射到业务模块，异步处理玩家消息的线程模型　
  * listener包，事件驱动模型
  * utils包，各种工具类　　

  ## QuickStart  
  1. 分别下载服务端和客户端的代码，并导入到IDE;  
  2. 新建数据库game_data_001和game_user_001，并分别导入config下的同名sql文件;  
  3. 启动服务端，入口为ServerStartup类;  
  4. 启动客户端，入口为ClientStartup类;


  栏目教程请移步：--> [csdn博客](http://blog.csdn.net/column/details/16043.html)

  欢迎star/fork，欢迎学习/使用，期待一起贡献代码！！

  ## 跟我交流讨论
  QQ：641711541
  
