package com.kingston.jforgame.server.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.google.gson.Gson;
import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.game.login.message.req.ReqLoginMessage;
import com.kingston.jforgame.server.game.login.message.req.ReqSelectPlayerMessage;
import com.kingston.jforgame.server.game.player.message.ReqCreateNewPlayerMessage;
import com.kingston.jforgame.server.logs.LoggerUtils;
import com.kingston.jforgame.socket.codec.SerializerHelper;
import com.kingston.jforgame.socket.message.Message;

/**
 * 使用socket构建的机器人
 * @author kingston
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

		System.out.println("开始连接socket服务端");
		int serverPort = ServerConfig.getInstance().getServerPort();
		ConnectFuture future = connector.connect(new InetSocketAddress(serverPort));

		future.awaitUninterruptibly();

		IoSession session = future.getSession();
		this.session = session;
	}
	
	public void createNew() {
		ReqCreateNewPlayerMessage req = new ReqCreateNewPlayerMessage();
		req.setName("Happy");
		this.sendMessage(req);
	}

	public void login() {
		ReqLoginMessage request = new ReqLoginMessage();
		request.setPassword("kingston");
		request.setAccountId(123L);
		this.sendMessage(request);
	}


	public void selectedPlayer(long playerId) {
		ReqSelectPlayerMessage request = new ReqSelectPlayerMessage();
		request.setPlayerId(playerId);
		this.sendMessage(request);
	}

	/**
	 * 发送消息
	 * @param message
	 */
	public void sendMessage(Message message) {
		System.err.println("发送请求-->" + message);
		this.session.write(message);
	}

	private class ClientHandler extends IoHandlerAdapter {

		@Override
		public void messageReceived(IoSession session, Object message) {
			System.out.println("收到响应<--" + new Gson().toJson(message));
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			LoggerUtils.error("client exception", cause);
		}
	}

}
