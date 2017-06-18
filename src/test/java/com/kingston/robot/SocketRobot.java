package com.kingston.robot;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.kingston.game.login.message.ReqLoginMessage;
import com.kingston.net.codec.MessageCodecFactory;

public class SocketRobot {
	
	private String name;
	
	private IoSession session;
	
	public SocketRobot(String name) {
		
	}
	
	public void buildConnection() {
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(MessageCodecFactory.getInstance()));
		connector.setHandler(new ClientHandler());
		
		ConnectFuture future = connector.connect(new InetSocketAddress(9527));
		future.awaitUninterruptibly();
		
		initConfig(connector);
		IoSession session = future.getSession();
		this.session = session;
	
	}
	
	public void sendMessage() {
		ReqLoginMessage message = new ReqLoginMessage();
		message.setPassword("tom");
		message.setPlayerId(123L);
		this.session.write(message);
	}

	private void initConfig(NioSocketConnector connector) {
		SocketSessionConfig config = connector.getSessionConfig();
		config.setKeepAlive(true);
		config.setReuseAddress(true);

	}
	
	private class ClientHandler extends IoHandlerAdapter {
		
		public void messageReceived(IoSession session, Object message) {
			System.err.println(message.getClass().getName());
		}
	}
	
}
