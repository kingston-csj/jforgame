
package jforgame.server.client;

import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.server.game.hello.ReqHello;
import jforgame.server.game.hello.ResHello;
import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RequestCallback;
import jforgame.socket.client.RpcMessageClient;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.client.Traceable;
import jforgame.codec.struct.StructMessageCodec;
import jforgame.socket.support.DefaultMessageFactory;
import jforgame.server.utils.JsonUtils;
import jforgame.socket.HostAndPort;
import jforgame.socket.IdSession;
import jforgame.socket.netty.client.RpcClientFactory;
import jforgame.socket.share.message.IMessageDispatcher;

/**
 * 客户端模拟器启动程序
 */
public class ClientStartup {

	public static void main(String[] args) throws Exception {
		//初始化协议池
		DefaultMessageFactory.getInstance().initMessagePool(ServerScanPaths.MESSAGE_PATH);
		//读取服务器配置
		ServerConfig.getInstance();

		int serverPort = ServerConfig.getInstance().getServerPort();
		HostAndPort hostPort = new HostAndPort();
		hostPort.setHost("127.0.0.1");
		hostPort.setPort(serverPort);

		IMessageDispatcher msgDispatcher = new IMessageDispatcher() {
			@Override
			public void onSessionCreated(IdSession session) {

			}

			@Override
			public void dispatch(IdSession session, Object message) {
				System.err.println("收到消息<-- " + message.getClass().getSimpleName() + "=" + JsonUtils.object2String(message));
				if (message instanceof Traceable) {
					Traceable traceable = (Traceable) message;
					RpcResponseData responseData = new RpcResponseData();
					responseData.setResponse(message);
					CallBackService.getInstance().fillCallBack(traceable.getIndex(), responseData);
				}

			}

			@Override
			public void onSessionClosed(IdSession session) {

			}
		};

		RpcClientFactory clientFactory = new RpcClientFactory(msgDispatcher, DefaultMessageFactory.getInstance(), new StructMessageCodec());
		IdSession session = clientFactory.createSession(hostPort);
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
			}
		});
	}

}
