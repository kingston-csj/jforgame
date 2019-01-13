package com.kingston.jforgame.server.robot;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.game.chat.message.ReqPrivateChat;
import com.kingston.jforgame.server.game.core.SchedulerManager;
import com.kingston.jforgame.server.game.login.message.req.ReqLoginMessage;
import com.kingston.jforgame.server.game.player.message.ReqCreateNewPlayerMessage;
import com.kingston.jforgame.server.logs.LoggerUtils;
import com.kingston.jforgame.server.utils.JsonUtils;
import com.kingston.jforgame.socket.codec.SerializerHelper;
import com.kingston.jforgame.socket.message.Message;

public class Robot {

	private RobotSession session;

	private String name;

	private long accountId;

	public Robot(long accountId) {
		this.accountId = accountId;
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
		request.setAccountId(accountId);
		this.session.sendMessage(request);

//		ReqCreateNewPlayerMessage req = new ReqCreateNewPlayerMessage();
//		req.setName(name);
//		this.session.sendMessage(req);
	}

	public void runAi() {
		Runnable task = () -> {
			ReqPrivateChat reqChat = new ReqPrivateChat();
			reqChat.setContent("你好吗？");
			reqChat.setReceiverId(10000L);
			this.session.sendMessage(reqChat);
		};
		SchedulerManager.getInstance().scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
	}

	public String getName() {
		return this.name;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public void setName(String name) {
		this.name = name;
	}

	private class ClientHandler extends IoHandlerAdapter {

		@Override
		public void sessionOpened(IoSession session) throws Exception {

		}

		@Override
		public void messageReceived(IoSession session, Object data) {
			Message message = (Message) data;
			System.out.println("收到响应-->" + data.getClass().getSimpleName() + " " + JsonUtils.object2String(data));
			Robot.this.session.receiveMessage(message);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			LoggerUtils.error("client exception", cause);
		}
	}

}
