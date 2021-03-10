package jforgame.server.net.mina;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jforgame.server.ServerScanPaths;
import jforgame.server.net.MessageDispatcher;
import jforgame.server.net.mina.filter.FloodFilter;
import jforgame.server.net.mina.filter.MessageTraceFilter;
import jforgame.server.net.mina.filter.ModuleEntranceFilter;
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
import jforgame.socket.mina.ServerSocketIoHandler;

public class MinaSocketServer implements ServerNode {

	private Logger logger = LoggerFactory.getLogger(MinaSocketServer.class);

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
		int serverPort = ServerConfig.getInstance().getServerPort();
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());

		acceptor = new NioSocketAcceptor(pool);
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setAll(getSessionConfig());

		logger.info("mina socket server start at port:{},正在监听客户端的连接...", serverPort);
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec",
				new ProtocolCodecFilter(SerializerHelper.getInstance().getCodecFactory()));
		filterChain.addLast("moduleEntrance", new ModuleEntranceFilter());
		filterChain.addLast("msgTrace", new MessageTraceFilter());
		filterChain.addLast("flood", new FloodFilter());
		//指定业务逻辑处理器
		acceptor.setHandler(new ServerSocketIoHandler(new MessageDispatcher(ServerScanPaths.MESSAGE_PATH)));
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
		logger.error("---------> socket server stop successfully");
	}

}