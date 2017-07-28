package com.kingston.robot;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.kingston.ServerConfig;
import com.kingston.game.login.message.ReqLoginMessage;
import com.kingston.game.login.message.ReqSelectPlayerMessage;
import com.kingston.logs.LoggerUtils;
import com.kingston.net.Message;
import com.kingston.net.codec.MessageCodecFactory;

public class SocketRobot {

	private String name;

	private IoSession session;

	public SocketRobot(String name) {
		this.name = name;
	}

	public void buildConnection() {
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(MessageCodecFactory.getInstance()));
		connector.setHandler(new ClientHandler());

		System.out.println("开始连接socket服务端"); 
		int serverPort = ServerConfig.getInstance().getServerPort();
		ConnectFuture future = connector.connect(new InetSocketAddress(serverPort));

		future.awaitUninterruptibly();

		IoSession session = future.getSession();
		this.session = session;

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
		this.session.write(message);
	}

	private class ClientHandler extends IoHandlerAdapter {
		public void messageReceived(IoSession session, Object message) {
			System.out.println("收到响应-->" + message); 
		}

		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			LoggerUtils.error("client exception", cause);
		}
	}

}
