package jforgame.server.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.google.gson.Gson;
import jforgame.server.ServerConfig;
import jforgame.server.game.login.message.req.ReqAccountLogin;
import jforgame.server.game.login.message.req.ReqSelectPlayer;
import jforgame.server.game.player.message.req.ReqCreateNewPlayer;
import jforgame.server.logs.LoggerUtils;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;

/**
 * 使用socket构建的机器人
 * @author kinson
 *
 */
public class ClientPlayer {

	private String name;

	private IoSession session;

	public ClientPlayer(String name) {
		this.name = name;
	}

	public void buildConnection() {
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(SerializerHelper.getInstance().getCodecFactory()));
		connector.setHandler(new ClientHandler());

		int serverPort = ServerConfig.getInstance().getServerPort();
		System.out.println("开始连接游戏服务器端口" + serverPort);
		ConnectFuture future = connector.connect(new InetSocketAddress(serverPort));
		
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		this.session = session;
	}
	
	public void createNew() {
		ReqCreateNewPlayer req = new ReqCreateNewPlayer();
		req.setName("Happy");
		this.sendMessage(req);
	}

	public void login() {
		ReqAccountLogin request = new ReqAccountLogin();
		request.setPassword("admin");
		request.setAccountId(123L);
		this.sendMessage(request);
	}


	public void selectedPlayer(long playerId) {
		ReqSelectPlayer request = new ReqSelectPlayer();
		request.setPlayerId(playerId);
		this.sendMessage(request);
	}

	/**
	 * 发送消息
	 * @param message
	 */
	public void sendMessage(Message message) {
		System.err.println(String.format("发送请求-->  %s %s", message.getClass().getSimpleName(), new Gson().toJson(message)));
		this.session.write(message);
	}

	private class ClientHandler extends IoHandlerAdapter {

		@Override
		public void messageReceived(IoSession session, Object message) {
			System.err.println(String.format("收到响应<--  %s %s", message.getClass().getSimpleName(), new Gson().toJson(message)));
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			LoggerUtils.error("client exception", cause);
		}
	}

}
