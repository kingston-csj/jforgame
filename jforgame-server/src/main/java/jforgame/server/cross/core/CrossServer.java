package jforgame.server.cross.core;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jforgame.server.cross.core.server.BaseCrossMessageDispatcher;
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

import jforgame.server.ServerConfig;
import jforgame.socket.ServerNode;
import jforgame.socket.codec.SerializerHelper;

public class CrossServer implements ServerNode {
	
	private Logger logger = LoggerFactory.getLogger(CrossServer.class);

	private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final Executor executor = Executors.newCachedThreadPool();

	private static final SimpleIoProcessorPool<NioSession> pool =
			new SimpleIoProcessorPool<NioSession>(NioProcessor.class, executor, CPU_CORE_SIZE);

	private SocketAcceptor acceptor;

	/**
	 * start Mina serversocket
	 * @throws Exception
	 */
	@Override
	public void start() throws Exception {
		final int serverPort = ServerConfig.getInstance().getCrossPort();
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());

		acceptor = new NioSocketAcceptor(pool);
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setAll(getSessionConfig());

		logger.info("cross server start at port:{},正在监听服务器点对点的连接...", serverPort);
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec",
				new ProtocolCodecFilter(SerializerHelper.getInstance().getCodecFactory()));
		//指定业务逻辑处理器
		acceptor.setHandler(new Game2GameIoHandler(BaseCrossMessageDispatcher.getInstance()));
		//设置端口号
		acceptor.setDefaultLocalAddress(new InetSocketAddress(serverPort));
		//启动监听
		acceptor.bind();
	}

	private SocketSessionConfig getSessionConfig() {
		SocketSessionConfig config = new DefaultSocketSessionConfig();
		config.setKeepAlive(true);
		config.setReuseAddress(true);

		return config;
	}

	@Override
	public void shutdown() {
		if (acceptor != null) {
			acceptor.unbind();
			acceptor.dispose();
		}
		logger.error("---------> cross server stop successfully");
	}

}
