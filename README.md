  ## Project introduction　　
  jforgame, is a lightweight online game framework for java. The project just shows some simple logic examples, the most imporant is to provide most of the base functions for rapid game development. You only need to focus on game logic then.  
  The framework uses maven tool to manager dependencies and project's build.  


  ## Module Directory
  game package is game logic module, other packages belong to engine framework.  
  Each package has its purpose:
  * cache package，use guava cache to support player cache system　　
  * orm package, use a lightweight tool for conversion between pojo and database relation　　　
  * db package，使用独立线程，异步处理玩家及公共数据的持久化　　
  * monitor包，系统监控模块，包括使用jmx对程序进行监控 　　
  * net包，包括io网关模块，玩家消息自动映射到业务模块，异步处理玩家消息的线程模型　
  * listener包，事件驱动模型  
  * doctor包，采用基于类替换的方式实现代码热更新  
  * game/gm包，游戏内部金手指命令  
  * game/http包，游戏运营/运维后台命令  
  * redis包，跨服通信（比如跨服排行榜）   
  * tools包，简化项目开发的辅助小工具
  * utils包，各种工具类　
  
  ## Third Party 
  Name | Purpose | Official website  
  ----|------|----     
  Mina | nio socket framework | [http://mina.apache.org/](http://mina.apache.org/)  
  jprotobuf | message codec | [https://github.com/jhunters/jprotobuf](https://github.com/jhunters/jprotobuf)  
  Guava | pojo memory cache | [https://github.com/google/guava](https://github.com/google/guava)  
  Jedis | redis memory cache | [https://redis.io](https://redis.io/)  
  quartz | job task scheduler | [http://www.quartz-scheduler.org/](http://www.quartz-scheduler.org/) 
  groovy | class hotswap | [http://www.groovy-lang.org/](http://www.groovy-lang.org/)　　  
  proxool | mysql data pool | [http://proxool.sourceforge.net/](http://proxool.sourceforge.net/)  
  orm | lightweight orm | [https://github.com/kingston-csj/orm](https://github.com/kingston-csj/orm) 
  slf4j+log4j | logging system | [https://www.slf4j.org/](https://www.slf4j.org/)  
  maven | manage jar dependency, build project| [http://maven.apache.org/](http://maven.apache.org/)  


  ## QuickStart  
  1. Use git to download codes, git clone https://github.com/kingston-csj/jforgame  
  2. Import maven project to your ide  
  3. Create new database named game_data_001 and import sql from resources/game_data_001.sql file. Create new databse named game_user_001 and import sql from resources/game_user_001.sql file
  4. Start game server，entrance is ServerStartup.java  
  5. Start robot client，entrance is ClientStartup.java


  Chinese wiki --> [wiki](https://github.com/kingston-csj/jforgame/wiki)  
  
  ## Contributing  
  We are always looking for people to join us. If you have an issue, feature request, or pull request, let us know!  
  Many thanks for your star!!
  
