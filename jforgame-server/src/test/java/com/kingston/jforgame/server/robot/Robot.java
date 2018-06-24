package com.kingston.jforgame.server.robot;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.game.login.message.ReqLoginMessage;
import com.kingston.jforgame.server.game.login.message.ReqSelectPlayerMessage;
import com.kingston.jforgame.server.game.login.message.ResLoginMessage;
import com.kingston.jforgame.server.game.player.message.ReqCreateNewPlayerMessage;
import com.kingston.jforgame.server.game.player.message.ResCreateNewPlayerMessage;
import com.kingston.jforgame.server.logs.LoggerUtils;
import com.kingston.jforgame.server.robot.handler.LoginSuccHandler;
import com.kingston.jforgame.socket.codec.SerializerHelper;
import com.kingston.jforgame.socket.message.Message;

public class Robot {

	private RobotSession session;

	private String name;

	public Robot(String name) {
		this.name = name;
	}

	public void doConnection() {
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(SerializerHelper.getInstance().getCodecFactory()));
		connector.setHandler(new ClientHandler());

		System.out.println("开始连接socket服务端");
		int serverPort = ServerConfig.getInstance().getServerPort();
		ConnectFuture future = connector.connect(new InetSocketAddress(serverPort));

		future.awaitUninterruptibly();

		IoSession session = future.getSession();
		this.session = new RobotSession(this, session);

		this.session.registerMessageHandler();

		this.login();
	}

	public void login() {
		ReqLoginMessage request = new ReqLoginMessage();
		request.setPassword("kingston");
		request.setAccountId(123L);
		this.session.sendMessage(request);
	}

	public String getName() {
		return this.name;
	}

	private class ClientHandler extends IoHandlerAdapter {

		@Override
		public void sessionOpened(IoSession session) throws Exception {

		}

		@Override
		public void messageReceived(IoSession session, Object data) {
			Message message = (Message)data;
			System.out.println("收到响应-->" + data);
			Robot.this.session.receiveMessage(message);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			LoggerUtils.error("client exception", cause);
		}
	}

}
