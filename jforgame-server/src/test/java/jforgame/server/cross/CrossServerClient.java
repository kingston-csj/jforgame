package jforgame.server.cross;

import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.server.cross.core.CrossServer;
import jforgame.server.cross.core.client.C2SSessionPoolFactory;
import jforgame.server.cross.core.client.CCSession;
import jforgame.server.game.cross.ladder.message.G2F_LadderTransfer;
import jforgame.server.net.mina.MinaSocketServer;
import jforgame.socket.message.MessageFactory;

public class CrossServerClient {

	private MinaSocketServer socketServer;

	private CrossServer crossServer;

	public CrossServerClient() throws Exception {
		// 初始化协议池
		MessageFactory.INSTANCE.initMessagePool(ServerScanPaths.MESSAGE_PATH);
		// 读取服务器配置
		ServerConfig config = ServerConfig.getInstance();
		config.setCrossPort(config.getCrossPort() + 1);
		config.setServerId(config.getServerId() + 1);
		config.setServerPort(config.getServerPort() + 1);

		socketServer = new MinaSocketServer();
		socketServer.start();

		crossServer = new CrossServer();
		crossServer.start();
	}

	public void request() {
		G2F_LadderTransfer req = new G2F_LadderTransfer();
		CCSession cSession = C2SSessionPoolFactory.getInstance().borrowSession("127.0.0.1", 9627);
		cSession.sendMessage(req);
	}

	public static void main(String[] args) throws Exception {
		CrossServerClient client = new CrossServerClient();
		client.request();
	}

}
