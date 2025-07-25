## English | [中文](README.md)

## Project Introduction

jforgame is a lightweight, high-performance mobile game server framework written in Java. The project provides various components to support rapid secondary development, as well as tools for managing production environment services. Additionally, to help users get started quickly, the project provides several common business functions as demonstrations.

## Project Features

- Comprehensive framework blog tutorials for quick understanding of project module principles
- Supports socket/WebSocket integration, perfectly adapting to mobile game/web game/H5/mini-game server architecture
- Communication protocol supports protobuf or regular JavaBeans, providing multiple choices for clients
- Powerful client async/sync APIs for easy cross-process communication
- Uses custom lightweight ORM tool library, supports multiple data sources, automatic table creation and field addition, supports full/incremental field updates
- Framework provides various components that can be directly used for secondary business logic development
- Hot code updates without server restart, hot configuration updates, operations and maintenance stay online
- Like Go language? We also support it --> [Go Game Server](https://github.com/kingston-csj/gforgame)
- Has independent HTTP admin backend website for game operations/maintenance support --> [Admin Management System](https://github.com/kingston-csj/gamekeeper)
- Besides games, can also be used to develop other network applications, such as real-time chat --> [QQ-like Chat Application](https://github.com/kingston-csj/im)

## Module Directory Structure

```
jforgame
├── jforgame-commons --base common services
├── jforgame-runtime --application runtime monitoring data, including memory, threads, classes, etc.
├── jforgame-socket-parent --Tcp socket communication, including io gateway module, message routing, session management, includes netty and mina versions
    ├── jforgame-socket-api --server/client basic API interfaces
    ├── jforgame-socket-netty --netty implementation, includes WebSocket server and client
    ├── jforgame-socket-mina --mina implementation, no WebSocket
├── jforgame-orm --custom ORM library specifically designed for game servers, for conversion between database table records and program POJO objects
├── jforgame-spring-boot-starter-data --encapsulates configuration data reading in springboot starter mode, supports csv, excel and other file formats. Supports hot configuration data updates and secondary caching.
├── jforgame-hotswap --supports hot game business updates
├── jforgame-codec-parent --data codec for socket communication
    ├── jforgame-codec-api --message codec API interfaces
    ├── jforgame-codec-protobuf --protobuf implementation
    ├── jforgame-codec-struct --regular JavaBeans, reflection implementation
├── jforgame-demo --game basic components and business logic modules
    ├── cache package, uses guava cache library for system caching framework support
    ├── db package, implements async processing of player and public data persistence based on commons-persist and orm
    ├── listener package, event-driven model
    ├── doctor package, uses Groovy to execute arbitrary dynamic code, or JDK's instrument mechanism to modify class method bodies
    ├── cross package, cross-server event communication foundation
    ├── game/gm package, game internal cheat commands
    ├── game/admin package, game operations/maintenance backend commands
    ├── redis package, cross-server communication (such as cross-server rankings)
    ├── tools package, auxiliary small tools to simplify project development
    └── utils package, various utility classes
```

## Quick Start

1. Module demo tutorials --> [wiki](https://github.com/kingston-csj/jforgame/wiki/Examples)
2. Use git to download code: git clone https://github.com/kingston-csj/jforgame
3. Import the code into an IDE with maven plugin (select the pom.xml file in the root directory)
4. Create new databases game_data_001 and game_user_001, and import the corresponding sql files from test/resources
5. Start the server, entry point is ServerStartup class
   (If importing all project modules, you also need to set up the working directory. For example, in IDEA: run->EditConfigurations->Working directory, set to \*\*\jforgame\jforgame-demo)
6. Start the client, entry point is ClientStartup class
   (If importing all project modules, you also need to set up the working directory. For example, in IDEA: run->EditConfigurations->Working directory, set to \*\*\jforgame\jforgame-demo)
7. Import as dependencies

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

One line of code to start server (socket/websocket)

```
TcpSocketServerBuilder.newBuilder()
    .bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
    .setMessageFactory(GameMessageFactory.getInstance())
    .setMessageCodec(new StructMessageCodec())
    .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
    .build()
    .start();
```

## Join the Community

Welcome to star/fork, welcome to learn/use, looking forward to contributing code together!!
If you find bugs or have any questions, please submit an issue!!

## Contributing

We are always looking for people to join us. If you have an issue, feature request, or pull request, let us know!
Many thanks for your stars!!

## Disclaimer

This project is only for learning and research purposes, and is prohibited from being used for illegal profit and commercial activities. Any legal disputes arising therefrom are not related to the author!!
