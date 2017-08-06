# game_server
game_server是一个用java编写的手游服务端框架，使用Mina作为IO网关。除了简单的业务功能之外，也包含各种辅助组件。使用maven工具管理依赖及打包。\<br /\> 　
(A mobile game server socket frameworkd, including all the base functions)\<br /\> 
\<br /\> 　　
\<br /\>   
game包是游戏的业务模块\<br /\> 
此包之外的则是各种辅助模块，包括\<br /\> 　
cache包，使用guava cache,用于支持系统的缓存框架；\<br /\> 　　
orm包，使用自定义的orm框架，用于数据库表记录与程序pojo对象的相互转换；\<br /\> 　　　
db包，使用独立线程，异步处理玩家及公共数据的持久化；\<br /\> 　　
logs包，日志系统；\<br /\> 　　
monitor包，系统监控模块，包括使用jmx对程序的监控；\<br /\> 　　
net包，包括io网关模块，玩家消息自动映射到业务模块，异步处理玩家消息的线程模型；\<br /\> 　　
listener包，事件驱动模块（待完善）；\<br /\> 　　
utils包，各种工具类；\<br /\> 　　
\<br /\> 
\<br /\> 
\<br /\> 
ServerStartup为服务端的启动入口。\<br /\> 　　
ClientStartup为客户端机器人启动入口，放到test目录下。\<br /\> 
