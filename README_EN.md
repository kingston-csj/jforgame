  ## English | [中文](README.md)  
  
  ## jforgame　　
  A lightweight online game framework written in Java. The project just shows some simple logic examples, the most imporant is to provide most of the base functions for rapid game development. You only need to focus on game logic then. The project use maven to manage jar dependency and build project  


  ## Module Directory  
  ``` 
  jforgame  
  ├── jforgame-common --base common service  
  ├── jforgame-admin  --game admin web tool  
  ├── jforgame-net    --including io message codec, message request mapper, message thread model    
  ├── jforgame-orm   --use a lightweight tool for conversion between pojo and database asynchronously 
  ├── hotswap-agent  --support hotswap without restarting server
  ├── jforgame-server   
  |    ├──  cache package, use guava cache to support players cache system   
  |    ├──  db package, use a independent thread pool to save players' and common data asynchronously  
  |    ├──  monitor package, to monitor game service，currently, we'll use jmx to manage game server    
  |    ├──  listener package, event driver model  
  |    ├──  doctor package, there are two ways for u to hotswap class  
  |    ├──  game/gm pakcage, gm command for test  
  |    ├──  game/admin package, http admin command for system  
  |    ├──  redis package, cross-server communication, such as multiserver ranking list  
  |    ├──  tools pakcage, little tool to support project's development  
  |    └──  utils package, code utils    
  ``` 
  
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
  slf4j+log4j | logging system | [https://www.slf4j.org/](https://www.slf4j.org/)  
  maven | manage jar dependency, build project| [http://maven.apache.org/](http://maven.apache.org/)  


  ## QuickStart  
  1. Use git to download codes, git clone https://github.com/kingston-csj/jforgame  
  2. Import maven project to your ide  
  3. Create new database named game_data_001 and import resources/game_data_001.sql into it. Similarly, create new databse named game_user_001 and import resources/game_user_001.sql into it
  4. Start game server，entrance is ServerStartup.java  
  5. Start robot client，entrance is ClientStartup.java


  Chinese wiki --> [wiki](https://github.com/kingston-csj/jforgame/wiki)  
  
  ## Contributing  
  We are always looking for people to join us. If you have an issue, feature request, or pull request, let us know!  
  Many thanks for your stars!!
  
