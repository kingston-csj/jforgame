
package jforgame.server.client;

import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.socket.support.DefaultMessageCodecFactory;
import jforgame.socket.support.MessageFactoryImpl;
import jforgame.server.utils.JsonUtils;
import jforgame.socket.HostAndPort;
import jforgame.socket.IdSession;
import jforgame.socket.client.RpcClientFactory;
import jforgame.socket.share.message.IMessageDispatcher;

/**
 * 客户端模拟器启动程序
 */
public class ClientStartup {

	public static void main(String[] args) throws Exception {
		//初始化协议池
		MessageFactoryImpl.getInstance().initMessagePool(ServerScanPaths.MESSAGE_PATH);
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
			}

			@Override
			public void onSessionClosed(IdSession session) {

			}
		};

		RpcClientFactory clientFactory = new RpcClientFactory(msgDispatcher, MessageFactoryImpl.getInstance(), DefaultMessageCodecFactory.getMessageCodecFactory());
		IdSession session = clientFactory.createSession(hostPort);
		ClientPlayer robot = new ClientPlayer(session);
		robot.login();
		robot.selectedPlayer(10000L);
	}

}
