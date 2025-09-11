jforgame-socket，作为jforgame框架的核心模块，支持socket和websocket两种方式。该框架最大的特点是：
## 1.组件无缝切换
### 1.1.netty/mina切换，不用改一句业务代码
java领域最著名的两个NIO框架，一个是Netty，一个是Mina。尽管Mina不再更新维护，它仍是一些旧项目的底层网络库。  
使用mina构建的服务器  
```
import jforgame.socket.mina.support.server.TcpSocketServerBuilder;

socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();
```
使用Netty构建的服务器  
```
jforgame.socket.netty.support.server.TcpSocketServerBuilder

socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();
```
不细心观察，很难看出，这两段代码，仅仅只有导入的包路径有一个单词的差异。其他地方无需改动一行代码！！

### 1.2.socket/websocket切换，仅有几个字母的差异
一个游戏项目，立项的时候定位选择了socket作为网络通信方式，后来，项目经理决定增加小程序版本。由于小游戏不支持原生socket，项目经理问了，要多久搞得来。你嘴里说着，怕是没个把月，调研不出来，整个底层都要改个透。转身，一行代码搞定！（压力给到客户端了）  
使用netty构建的socket服务器  
```
		socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();
```

使用netty构建的websocket服务器(两个英文字母的差异)
```
		websocketServer = WepSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();
```

### 1.3.消息编解码的切换
消息编解码，涉及客户端通信，一般是选择双方都易于接受的方式。最简单的方式，就是使用json，虽然通信数据有点冗长，但开发前摇最短。  
如果选择Protobuf，那么就new一个ProtobufMessageCodec。如果希望用自定义的协议，就new一个StructMessageCodec。如果采用json，就new一个JsonCodec。  
就是这么简单!!

## 2.嵌入式跨服
现在的游戏，没有一个跨服玩法，都不好意思说这是个网络游戏了。  
常规的游戏服务器，开发跨服，需要使用到第三方库，例如什么grpc，rmi，protobuf-rpc等等。增加了复杂度和开发人员的理解成本。  

### 2.1.逻辑服与跨服统一API
跨服，其实就是一个特殊的游戏服，直接new一个新的socket服务器节点即可  
而且，由于跨服属于服务器内部通信，完全不需要与客户端协商，直接用你最喜欢的消息编解码方式即可！
```
    if (config.getCrossPort() > 0) {
        // 启动跨服服务
        crossServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getCrossPort()))
                .setMessageFactory(GameMessageFactory.getInstance())
                .setMessageCodec(new StructMessageCodec())
                .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
                .build();

        crossServer.start();
    }
```

### 2.2.强大的客户端通信API
提供三种方式处理跨服通信，总有一种，符合您的口味~~  
#### 请求-阻塞模式
  ```
    ResHello response = (ResHello) RpcMessageClient.request(session, new ReqHello());
    System.out.println("rpc 消息同步调用");
    System.out.println(response);
```

#### callback模式
  ```
    RpcMessageClient.callBack(session, new ReqHello(), new RequestCallback() {
    @Override
    public void onSuccess(Object callBack) {
        System.err.println("rpc 消息异步调用");
        ResHello response = (ResHello) callBack;
        System.err.println(response);
    }

    @Override
    public void onError(Throwable error) {
        System.out.println("----onError");
        error.printStackTrace();
    }
});
```
#### 方法注解
你甚至可以采用游戏服务器处理客户端消息的方式  
```
    // 发送方
    session.send(new ReqHello());
    // 接收方

	@RequestHandler
	public void reqSelectPlayer(IdSession session, ResHello response) {
        // 处理逻辑
	}
```
