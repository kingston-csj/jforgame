package jforgame.server.socket.mina;

import jforgame.server.ServerScanPaths;
import jforgame.server.socket.MessageDispatcher;
import jforgame.server.socket.mina.filter.FloodFilter;
import jforgame.server.socket.mina.filter.MessageTraceFilter;
import jforgame.server.socket.mina.filter.ModuleEntranceFilter;
import jforgame.socket.HostAndPort;
import jforgame.socket.ServerNode;
import jforgame.socket.mina.MinaMessageCodecFactory;
import jforgame.socket.mina.ServerSocketIoHandler;
import jforgame.socket.support.MessageFactoryImpl;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MinaSocketServer implements ServerNode {

	private Logger logger = LoggerFactory.getLogger(MinaSocketServer.class);

	private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final Executor executor = Executors.newCachedThreadPool();

	private static final SimpleIoProcessorPool<NioSession> pool =
			new SimpleIoProcessorPool<NioSession>(NioProcessor.class, executor, CPU_CORE_SIZE);

	private SocketAcceptor acceptor;

	private List<HostAndPort> nodesConfig;

	public MinaSocketServer(HostAndPort hostPort) {
		this.nodesConfig = Arrays.asList(hostPort);
	}

	public MinaSocketServer(List<HostAndPort> nodesConfig) {
		this.nodesConfig = nodesConfig;
	}

	/**
	 * start Mina serversocket
	 * @throws Exception
	 */
	@Override
	public void start() throws Exception {
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());

		acceptor = new NioSocketAcceptor(pool);
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setAll(new DefaultSocketSessionConfig());

		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec",
				new ProtocolCodecFilter(new MinaMessageCodecFactory(MessageFactoryImpl.getInstance())));
		filterChain.addLast("moduleEntrance", new ModuleEntranceFilter());
		filterChain.addLast("msgTrace", new MessageTraceFilter());
		filterChain.addLast("flood", new FloodFilter());
		//指定业务逻辑处理器
		acceptor.setHandler(new ServerSocketIoHandler(new MessageDispatcher(ServerScanPaths.MESSAGE_PATH)));

		for (HostAndPort node : nodesConfig) {
			logger.info("socket server is listening at " + node.getPort() + "......");
			acceptor.bind(new InetSocketAddress(node.getPort()));
		}
//		//设置端口号
//		acceptor.setDefaultLocalAddress(new InetSocketAddress(serverPort));
//		//启动监听
//		acceptor.bind();
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