package jforgame.demo.socket;

import jforgame.codec.struct.StructCodec;
import jforgame.socket.client.SocketClient;
import jforgame.socket.mina.server.TcpSocketServerBuilder;
import jforgame.socket.netty.client.TcpSocketClient;
import jforgame.socket.server.ServerNode;
import jforgame.socket.net.HostAndPort;

/**
 * 游戏服 socket 默认装配，屏蔽重复的 messageFactory / codec / dispatcher 组装逻辑。
 */
public final class GameSocketFactory {

    private GameSocketFactory() {
    }

    public static ServerNode createTcpServer(int port, String scanPath) {
        return TcpSocketServerBuilder.newBuilder()
                .bindingPort(HostAndPort.valueOf(port))
                .setMessageFactory(GameMessageFactory.getInstance())
                .setMessageCodec(new StructCodec())
                .setSocketIoDispatcher(new MessageIoDispatcher(scanPath))
                .build();
    }

    public static SocketClient createTcpClient(String ip, int port, String scanPath) {
        return new TcpSocketClient(
                new MessageIoDispatcher(scanPath),
                GameMessageFactory.getInstance(),
                new StructCodec(),
                HostAndPort.valueOf(ip, port)
        );
    }
}
