package com.kingston.jforgame.server.cross.core.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.cross.core.server.CMessageDispatcher;
import com.kingston.jforgame.socket.codec.SerializerHelper;
import com.kingston.jforgame.socket.message.Message;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

public class CCSession {
	
	private int id;
	
	private static AtomicInteger idFactory = new AtomicInteger();
	
	private String ipAddr;
	/**
	 * remote 端口
	 */
	private int port;
	
	private CMessageDispatcher dispatcher;
	
	private IoSession wrapper;
	
	public static CCSession valueOf(String ip, int port, CMessageDispatcher dispatcher) {
		CCSession cSession = new CCSession();
		cSession.ipAddr = ip;
		cSession.port = port;
		cSession.id = idFactory.getAndIncrement();
		
		return cSession;
	}
	
	public void buildConnection() {
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(SerializerHelper.getInstance().getCodecFactory()));
		connector.setHandler(new IoHandlerAdapter() {
			@Override
			public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			}
			@Override
			public void messageReceived(IoSession session, Object data) throws Exception {
				Message message = (Message)data;
				dispatcher.clientDispatch(CCSession.this, message);
			}
		});

		System.out.println("开始连接跨服服务器端口" + port);
		ConnectFuture future = connector.connect(new InetSocketAddress(port));
		
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		this.wrapper = session;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public int getPort() {
		return port;
	}

	public IoSession getWrapper() {
		return wrapper;
	}
	
	public int getId() {
		return id;
	}

	public void sendMessage(Message message) {
		this.wrapper.write(message);
	}

}
