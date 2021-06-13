
package jforgame.server.client;

import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.server.utils.JsonUtils;
import jforgame.socket.HostAndPort;
import jforgame.socket.IdSession;
import jforgame.socket.client.RpcClientFactory;
import jforgame.socket.codec.SerializerFactory;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.IMessageDispatcher;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageFactory;

/**
 * 客户端模拟器启动程序
 */
public class ClientStartup {

	public static void main(String[] args) throws Exception {
		//初始化协议池
		MessageFactory.INSTANCE.initMessagePool(ServerScanPaths.MESSAGE_PATH);
		//读取服务器配置
		ServerConfig.getInstance();

		int serverPort = ServerConfig.getInstance().getServerPort();
		HostAndPort hostPort = new HostAndPort();
		hostPort.setHost("127.0.0.1");
		hostPort.setPort(serverPort);

		SerializerFactory serializerFactory = SerializerHelper.getInstance().getSerializerFactory();
		IMessageDispatcher msgDispatcher = new IMessageDispatcher() {
			@Override
			public void onSessionCreated(IdSession session) {

			}

			@Override
			public void dispatch(IdSession session, Message message) {
				System.err.println("收到消息<-- " + message.getClass().getSimpleName() + "=" + JsonUtils.object2String(message));
			}

			@Override
			public void onSessionClosed(IdSession session) {

			}
		};

		RpcClientFactory clientFactory = new RpcClientFactory(msgDispatcher, serializerFactory);
		IdSession session = clientFactory.createSession(hostPort);
		ClientPlayer robot = new ClientPlayer(session);
		robot.login();
		robot.selectedPlayer(10000L);
	}

}
