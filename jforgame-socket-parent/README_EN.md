jforgame-socket is the core module of the jforgame framework, supporting both socket and websocket communication. Its main features are:

## 1. Seamless Component Switching
### 1.1. Netty/Mina Switching Without Changing Any Business Code
The two most famous NIO frameworks in the Java world are Netty and Mina. Although Mina is no longer actively maintained, it remains the underlying network library for some legacy projects.

Building a server with Mina:
```
import jforgame.socket.mina.support.server.TcpSocketServerBuilder;

socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();
```

Building a server with Netty:
```
jforgame.socket.netty.support.server.TcpSocketServerBuilder

socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();
```

If you look carefully, you can barely tell that these two code snippets differ in only ONE WORD in the import package path. No other changes needed!!

### 1.2. Socket/WebSocket Switching With Only a Few Letters Difference
The game project started with socket as the communication method, everything was going smoothly, until the project manager suddenly said "We need a mini-program version". My eyes nearly popped out: mini-games can't run native socket! I had to put on a difficult face in front of the boss: "Boss, this is tricky, the entire底层 has to be redesigned... it'll take at least a month to figure it out..." Turned back to my desk, typed one line of code, done! Smirk on my face: Now the client team has to worry about how to integrate!

Building a socket server with Netty:
```
    socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
            .setMessageFactory(GameMessageFactory.getInstance())
            .setMessageCodec(new StructMessageCodec())
            .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
            .build();
```

Building a websocket server with Netty (just two letters different):
```
    websocketServer = WepSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
            .setMessageFactory(GameMessageFactory.getInstance())
            .setMessageCodec(new StructMessageCodec())
            .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
            .build();
```

Note: Native Mina does not support websocket. If you need websocket, please use the jforgame-socket-netty module.

### 1.3. Message Codec Switching
Message encoding/decoding involves client communication. Generally, you choose a format that both parties find easy to work with. The simplest method is using JSON - although the communication data is somewhat verbose, the development lead time is shortest.

If you choose Protobuf, just new a ProtobufMessageCodec. If you want a custom protocol, just new a StructMessageCodec. If you use JSON, just new a JsonCodec.

It's that simple!!

## 2. Message Encoding/Decoding Principles

The framework's NIO implementation supports both Mina and Netty versions. You can choose based on your needs.

Message decoding actually consists of two steps:

1. Split a byte stream into a complete message packet (including header and body, where the header consists of length and message metadata)
2. Obtain message class information from header metadata, then deserialize into a Java entity

The first step's code logic is strongly related to the chosen NIO framework, involving packet splitting and complex buffer operations.

The second step is simpler since you already have the complete message body data. You can directly use Java NIO's ByteBuffer.

Encoding is the reverse of decoding, so we won't elaborate further here.

## 3. Embedded Cross-Server
These days, a game without cross-server gameplay is embarrassed to call itself an online game.

Conventional game servers require third-party libraries for cross-server development, such as grpc, rmi, protobuf-rpc, etc. This increases complexity and the learning curve for developers.

### 3.1. Unified API for Logic Servers and Cross-Servers
A cross-server is essentially a special game server - just new a socket server node directly.

Moreover, since cross-server belongs to internal server communication, there's no need to negotiate with the client. You can directly use your favorite message codec!
```
    if (config.getCrossPort() > 0) {
        // Start cross-server service
        crossServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getCrossPort()))
                .setMessageFactory(GameMessageFactory.getInstance())
                .setMessageCodec(new StructMessageCodec())
                .setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
                .build();

        crossServer.start();
    }
```

### 3.2. Powerful Client Communication API
Four methods are provided for cross-server communication - there's always one that suits your taste~~

#### Request-Blocking Mode
  ```
    ResHello response = (ResHello) RpcMessageClient.request(session, new ReqHello());
    System.out.println("rpc sync call");
    System.out.println(response);
```

#### Callback Mode
  ```
    RpcMessageClient.callBack(session, new ReqHello(), new RequestCallback() {
        @Override
        public void onSuccess(Object callBack) {
            System.err.println("rpc async call");
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

#### Future Mode
  ```
    // Future enables nested calls, avoiding "callback hell"
    RpcMessageClient.future(session, new ReqHello()).thenCompose(o -> {
        ResHello response2 = (ResHello) o;
        System.out.println("rpc future call");
        System.out.println(response2);
        return RpcMessageClient.future(session, new ReqHello());
    }).thenAccept(o -> {
        System.out.println("rpc future call, continue processing");
        ResHello response3 = (ResHello) o;
        System.out.println(response3);
    });
```

#### Method Annotation
You can even use the way game servers handle client messages:
```
    // Sender
    session.send(new ReqHello());

    // Receiver
	@RequestHandler
	public void onResHello(IdSession session, ResHello response) {
        // Processing logic
	}
```
