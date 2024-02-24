
package jforgame.demo.client;

import jforgame.codec.struct.StructMessageCodec;
import jforgame.demo.ServerConfig;
import jforgame.demo.ServerScanPaths;
import jforgame.demo.game.hello.ReqHello;
import jforgame.demo.game.hello.ResHello;
import jforgame.demo.utils.JsonUtils;
import jforgame.socket.client.RequestCallback;
import jforgame.socket.client.RpcMessageClient;
import jforgame.socket.client.SocketClient;
import jforgame.socket.mina.client.MSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.SocketIoDispatcherAdapter;
import jforgame.socket.support.DefaultMessageFactory;

/**
 * 客户端模拟器启动程序
 */
public class ClientStartup {

	public static void main(String[] args) throws Exception {
		//初始化协议池
		DefaultMessageFactory.getInstance().initMessagePool(ServerScanPaths.MESSAGE_PATH);
		//读取服务器配置
		int serverPort = ServerConfig.getInstance().getServerPort();
		HostAndPort hostPort = new HostAndPort();
		hostPort.setHost("127.0.0.1");
		hostPort.setPort(serverPort);

		SocketIoDispatcher msgDispatcher = new SocketIoDispatcherAdapter() {
			@Override
			public void dispatch(IdSession session, Object message) {
				System.err.println("收到消息<-- " + message.getClass().getSimpleName() + "=" + JsonUtils.object2String(message));
			}
			@Override
			public void exceptionCaught(IdSession session, Throwable cause) {
					cause.printStackTrace();
			}
		};

		SocketClient socketClient = new MSocketClient(msgDispatcher, DefaultMessageFactory.getInstance(), new StructMessageCodec(), hostPort);
		IdSession session = socketClient.openSession();
		ClientPlayer robot = new ClientPlayer(session);
		robot.login();
		robot.selectedPlayer(10000L);

		ResHello response = (ResHello) RpcMessageClient.request(session, new ReqHello());
		System.err.println("rpc 消息同步调用");
		System.out.println(response);

		RpcMessageClient.callBack(session, new ReqHello(), new RequestCallback() {
			@Override
			public void onSuccess(Object callBack) {
				System.err.println("rpc 消息异步调用");
				ResHello response = (ResHello) callBack;
				System.out.println("----"+response);
			}

			@Override
			public void onError(Throwable error) {
				System.out.println("----onError");
				error.printStackTrace();
			}
		});
	}

}
