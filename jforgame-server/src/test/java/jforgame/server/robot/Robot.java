package jforgame.server.robot;

import java.net.InetSocketAddress;

import jforgame.server.thread.SchedulerManager;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import jforgame.server.ServerConfig;
import jforgame.server.game.chat.message.ReqPrivateChat;
import jforgame.server.game.login.message.req.ReqAccountLogin;
import jforgame.server.logs.LoggerUtils;
import jforgame.server.utils.JsonUtils;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;

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
		ReqAccountLogin request = new ReqAccountLogin();
		request.setPassword("admin");
		request.setAccountId(accountId);
		this.session.sendMessage(request);
	}

	public void runAi() {
		Runnable task = () -> {
			ReqPrivateChat reqChat = new ReqPrivateChat();
			reqChat.setContent("你好吗？");
			reqChat.setReceiverId(10000L);
			this.session.sendMessage(reqChat);
		};
		SchedulerManager.scheduleAtFixedRate(task, 0, 1000);
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
