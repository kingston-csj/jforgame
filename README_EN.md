 ## English | [中文](README.md)

 ## Project Introduction
jforgame is a lightweight, high-performance mobile game server framework written in Java. It provides a variety of components for rapid secondary development, as well as tools for managing services in production environments. Meanwhile, practical business demos are offered to help developers get started quickly.

 ## Project Features
 * Paired with tutorial articles in the framework blog to help you quickly understand the principles of each module
* Supports Socket/WebSocket access, perfectly adapting to server architecture for mobile games, web games, H5 games and mini-program games
* Comes with a C# network framework counterpart, compatible with Socket/WebSocket and JSON/struct protocols
* Communication protocols support JSON, Protobuf and standard JavaBeans, offering multiple options for client integration
* One-click export of communication protocols for client-side languages such as C# and TypeScript (refer to CSharpProtocolGenerator tool)
* Built-in inter-process communication, with powerful asynchronous/synchronous client APIs to easily implement cross-server business logic
* Adopts a custom lightweight ORM library, supporting multiple data sources, automatic table creation and field addition, as well as full/incremental table field updates
* Rich built-in framework components ready for direct secondary business development
* Supports hot code replacement and hot configuration updates without server shutdown, ensuring uninterrupted game operation and maintenance
* A Go version for game server development is also available → [gforgame](https://github.com/kingston-csj/gforgame)
* SpringBoot-based practical MMORPG server project → [mmorpg Game Server](https://github.com/kingston-csj/mmorpg)
Independent backend management website for game operation and maintenance → [gGamekeeper Admin System](https://github.com/kingston-csj/gamekeeper)
Beyond game development, it can also build other network applications such as real-time chat systems → [QQ-like IM Application](https://github.com/kingston-csj/im)

Module Structure
``` git
jforgame
├── jforgame-commons -- Basic common utilities
├── jforgame-threadmodel -- Thread model; two implementations: Actor model & keyword-based dispatch. Actor is recommended to avoid uneven thread workload
├── jforgame-runtime -- Application runtime monitoring metrics (memory, threads, classes, etc.)
├── jforgame-socket-parent     -- TCP Socket communication module, including IO gateway, message routing and session management; provides Netty and Mina implementations
├── jforgame-socket-api    -- Base API interfaces for server & client
├── jforgame-socket-netty  -- Netty implementation with WebSocket server & client support
├── jforgame-socket-mina   -- Mina implementation (without WebSocket support)
├── jforgame-orm     -- Game-server-oriented custom ORM library for conversion between database table records and POJO objects
├── jforgame-spring-boot-starter-data -- SpringBoot starter for loading configuration files (CSV, Excel, etc.), supporting config hot update and secondary cache
├── jforgame-hotswap  -- Supports business code hot replacement
├── jforgame-codec-parent         -- Codec component for Socket communication
├── jforgame-codec-api        -- Codec API interfaces
├── jforgame-codec-protobuf   -- Protobuf codec implementation
├── jforgame-codec-struct     -- Standard JavaBean reflection codec; advanced version supports heterogeneous collection element types
├── jforgame-demo  -- Demo of basic game components and business modules
|    ├── cache -- Guava-based cache framework
|    ├── db -- Async data persistence for player and global data based on commons-persist and ORM
|    ├── listener -- Event-driven model
|    ├── doctor -- Dynamic code execution via Groovy or JDK Instrumentation for class method modification
|    ├── cross -- Basic communication foundation for cross-server matches/events
|    ├── game/gm -- In-game GM command system
|    ├── game/admin -- Operation & maintenance backend commands
|    ├── redis -- Cross-server communication (e.g., cross-server leaderboards)
|    ├── tools -- Helper utilities to simplify development
|    └── utils -- General utility classes
  ```
jforgame follows a component-based design. Each module is independent, allowing developers to import only required components.For example, if only Socket communication is needed, simply introduce jforgame-socket-netty or jforgame-socket-mina.

 ## Quick Start
 1. Tutorials for each module → Wiki
 2. Clone the project: git clone https://github.com/kingston-csj/jforgame
 3. Import the project into an IDE with Maven support by loading the root pom.xml
 4. Create databases game_data_001 and game_user_001, then import the corresponding SQL files under test/resources
 5. Start the server via the ServerStartup entry class
 (When importing all modules, configure the IDE working directory: Run -> Edit Configurations -> Working Directory, set to **\jforgame\jforgame-demo)
 6. Start the client via the ClientStartup entry class
 (Working directory configuration is the same as the server)
 7. Run the project demo

Maven Dependency
  ```xml
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
One-line Server Startup (Socket/WebSocket)

  ```java
    TcpSocketServerBuilder.newBuilder()
    .bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
    .setMessageFactory(GameMessageFactory.getInstance())
    .setMessageCodec(new StructMessageCodec())
    .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
    .build()
    .start();
  ```  
Message Protocol Definition
  ```java
    @MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.REQ_LOGIN)
    public class ReqAccountLogin implements Message {
    /** Account serial number */
    private long accountId;
    private String password;
    }
  ```
Message Route Handling
  ```java
    @MessageRoute
    public class LoginController {
    
        @RequestHandler
        public void reqAccountLogin(IdSession session, ReqAccountLogin request) {
            GameContext.loginManager.handleAccountLogin(session, request.getAccountId(), request.getPassword());
        }
    }
  ```
Development is straightforward — just follow the demo pattern and start coding your business logic directly!

## Join the Community

Welcome to star/fork, welcome to learn/use, looking forward to contributing code together!!
If you find bugs or have any questions, please submit an issue!!

## Contributing

We are always looking for people to join us. If you have an issue, feature request, or pull request, let us know!
Many thanks for your stars!!

## Disclaimer

This project is only for learning and research purposes, and is prohibited from being used for illegal profit and commercial activities. Any legal disputes arising therefrom are not related to the author!!
