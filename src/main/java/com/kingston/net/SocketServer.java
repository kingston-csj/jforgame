package com.kingston.net;

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

import com.kingston.net.codec.MessageCodecFactory;

public class SocketServer {
	
	private Logger logger = LoggerFactory.getLogger(SocketServer.class);

	private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();
	
	private static final Executor executor = Executors.newCachedThreadPool();
	
	private static final SimpleIoProcessorPool<NioSession> pool = 
			new SimpleIoProcessorPool<NioSession>(NioProcessor.class, executor, CPU_CORE_SIZE);

	private SocketAcceptor acceptor;
	
	/**
	 * 开始启动mina serversocket
	 * @throws Exception
	 */
	public void start() throws Exception {
		
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());
		
		acceptor = new NioSocketAcceptor(pool);
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setAll(getSessionConfig());
		
		//暂时写死在代码里，后期使用独立配置文件
		int port = 9527;
		logger.info("socket启动端口为{},正在监听客户端的连接", port);
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec", new ProtocolCodecFilter(MessageCodecFactory.getInstance())); 
		acceptor.setHandler( new IOHandler() );//指定业务逻辑处理器 
		acceptor.setDefaultLocalAddress(new InetSocketAddress(port) );//设置端口号 
		acceptor.bind();//启动监听 
		
	}
	
	
	private SocketSessionConfig getSessionConfig() {
		SocketSessionConfig config = new DefaultSocketSessionConfig();
		config.setKeepAlive(true);
		config.setReuseAddress(true);

		return config;
	}
}
