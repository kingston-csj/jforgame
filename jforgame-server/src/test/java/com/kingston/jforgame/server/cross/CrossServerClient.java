package com.kingston.jforgame.server.cross;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.ServerScanPaths;
import com.kingston.jforgame.server.cross.core.CrossServer;
import com.kingston.jforgame.server.cross.core.client.C2SSessionPoolFactory;
import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.server.game.cross.ladder.message.Req_G2F_LadderTransfer;
import com.kingston.jforgame.server.net.mina.SocketServer;
import com.kingston.jforgame.socket.message.MessageFactory;

public class CrossServerClient {

	private SocketServer socketServer;

	private CrossServer crossServer;

	public CrossServerClient() throws Exception {
		// 初始化协议池
		MessageFactory.INSTANCE.initMeesagePool(ServerScanPaths.MESSAGE_PATH);
		// 读取服务器配置
		ServerConfig config = ServerConfig.getInstance();
		config.init();
		config.setCrossPort(config.getCrossPort() + 1);
		config.setServerId(config.getServerId() + 1);
		config.setServerPort(config.getServerPort() + 1);

		socketServer = new SocketServer();
		socketServer.start(config.getServerPort());

		crossServer = new CrossServer();
		crossServer.start(config.getCrossPort());
	}

	public void request() {
		Req_G2F_LadderTransfer req = new Req_G2F_LadderTransfer();
		CCSession cSession = C2SSessionPoolFactory.getInstance().borrowSession("127.0.0.1", 9627);
		cSession.sendMessage(req);
	}

	public static void main(String[] args) throws Exception {
		CrossServerClient client = new CrossServerClient();
		client.request();
	}

}
