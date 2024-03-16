package jforgame.demo.socket.server;

import jforgame.codec.struct.StructMessageCodec;
import jforgame.demo.ServerScanPaths;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.MessageIoDispatcher;
import jforgame.socket.mina.support.DefaultProtocolCodecFactory;
import jforgame.socket.mina.support.DefaultSocketIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
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

public class MSocketServer implements ServerNode {

	private Logger logger = LoggerFactory.getLogger(MSocketServer.class);

	private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final Executor executor = Executors.newCachedThreadPool();

	private static final SimpleIoProcessorPool<NioSession> pool =
			new SimpleIoProcessorPool<>(NioProcessor.class, executor, CPU_CORE_SIZE);

	private SocketAcceptor acceptor;

	private List<HostAndPort> nodesConfig;

	public MSocketServer(HostAndPort hostPort) {
		this.nodesConfig = Arrays.asList(hostPort);
	}

	public MSocketServer(List<HostAndPort> nodesConfig) {
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
				new ProtocolCodecFilter(new DefaultProtocolCodecFactory(GameMessageFactory.getInstance(), new StructMessageCodec())));
		//指定业务逻辑处理器
		acceptor.setHandler(new DefaultSocketIoHandler(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH)));

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
	public void shutdown() throws Exception{
		if (acceptor != null) {
			acceptor.unbind();
			acceptor.dispose();
		}
		logger.error("---------> socket server stop successfully");
	}

}