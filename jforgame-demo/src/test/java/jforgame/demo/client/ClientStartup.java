
package jforgame.demo.client;

import jforgame.codec.struct.StructMessageCodec;
import jforgame.commons.util.JsonUtil;
import jforgame.demo.ServerConfig;
import jforgame.demo.game.hello.ReqHello;
import jforgame.demo.game.hello.ResHello;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.client.RequestCallback;
import jforgame.socket.client.RpcMessageClient;
import jforgame.socket.client.SocketClient;
import jforgame.socket.netty.support.client.TcpSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.SocketIoDispatcherAdapter;
import jforgame.socket.share.message.RequestDataFrame;

/**
 * 客户端模拟器启动程序
 */
public class ClientStartup {

    public static void main(String[] args) throws Exception {
        int serverPort = ServerConfig.getInstance().getServerPort();
        HostAndPort hostPort = new HostAndPort();
        hostPort.setHost("127.0.0.1");
        hostPort.setPort(serverPort);

        SocketIoDispatcher msgDispatcher = new SocketIoDispatcherAdapter() {
            @Override
            public void dispatch(IdSession session, Object frame) {
                RequestDataFrame dataFrame = (RequestDataFrame) frame;
                Object message = dataFrame.getMessage();
                System.err.println("收到消息<-- " + message.getClass().getSimpleName() + "=" + JsonUtil.object2String(message));
            }

            @Override
            public void exceptionCaught(IdSession session, Throwable cause) {
                cause.printStackTrace();
            }
        };

        SocketClient socketClient = new TcpSocketClient(msgDispatcher, GameMessageFactory.getInstance(), new StructMessageCodec(), hostPort);
        IdSession session = socketClient.openSession();

        ClientPlayer robot = new ClientPlayer(session);
        robot.login();
        robot.selectedPlayer(10000L);

        ResHello response = (ResHello) RpcMessageClient.request(session, new ReqHello());
        System.out.println("rpc 消息同步调用");
        System.out.println(response);

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

        // future可以实现嵌套调用，避免“回调地狱”
        RpcMessageClient.future(session, new ReqHello()).thenCompose(o -> {
            ResHello response2 = (ResHello) o;
            System.out.println("rpc 消息future调用");
            System.out.println(response2);
            return RpcMessageClient.future(session, new ReqHello());
        }).thenAccept(o -> {
            System.out.println("rpc 消息future调用，继续处理");
            ResHello response3 = (ResHello) o;
            System.out.println(response3);
        });
    }

}
