package com.kingston.jforgame.net.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.net.socket.codec.SerializerHelper;

public class SocketServer {

	private Logger logger = LoggerFactory.getLogger(SocketServer.class);

	private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final Executor executor = Executors.newCachedThreadPool();

	private static final SimpleIoProcessorPool<NioSession> pool =
			new SimpleIoProcessorPool<NioSession>(NioProcessor.class, executor, CPU_CORE_SIZE);

	private SocketAcceptor acceptor;

//	private int serverPort = ServerConfig.getInstance().getServerPort();

	/**
	 * start Mina serversocket
	 * @throws Exception
	 */
	public void start() throws Exception {
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());

		acceptor = new NioSocketAcceptor(pool);
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setAll(getSessionConfig());

		int serverPort = GateServerConfig.serverPort;
		logger.info("socket server start at port:{},正在监听客户端的连接...", serverPort);
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec",
				new ProtocolCodecFilter(SerializerHelper.getInstance().getCodecFactory()));
		acceptor.setHandler( new ServerSocketIoHandler() );//指定业务逻辑处理器
		acceptor.setDefaultLocalAddress(new InetSocketAddress(serverPort) );//设置端口号
		acceptor.bind();//启动监听

	}

	private SocketSessionConfig getSessionConfig() {
		SocketSessionConfig config = new DefaultSocketSessionConfig();
		config.setKeepAlive(true);
		config.setReuseAddress(true);

		return config;
	}

	public void shutdown() {
		if (acceptor != null) {
			acceptor.unbind();
			acceptor.dispose();
		}
		logger.error("---------> socket server stop at port:{}", GateServerConfig.serverPort);
	}

}
