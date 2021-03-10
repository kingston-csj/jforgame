package jforgame.server.net.netty;

import java.net.InetSocketAddress;

import jforgame.server.ServerScanPaths;
import jforgame.server.net.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.server.ServerConfig;
import jforgame.socket.ServerNode;
import jforgame.socket.netty.NettyProtocolDecoder;
import jforgame.socket.netty.NettyProtocolEncoder;
import jforgame.socket.netty.IoEventHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NettySocketServer implements ServerNode {

	private Logger logger = LoggerFactory.getLogger(NettySocketServer.class);

	// 避免使用默认线程数参数
	private EventLoopGroup bossGroup = new NioEventLoopGroup(4);
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private int maxReceiveBytes;

	public NettySocketServer(int maxReceiveBytes) {
		this.maxReceiveBytes = maxReceiveBytes;
	}

	@Override
	public void start() throws Exception {
		int serverPort = ServerConfig.getInstance().getServerPort();
		logger.info("netty socket服务已启动，正在监听用户的请求@port:" + serverPort + "......");
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChildChannelHandler());
			b.bind(new InetSocketAddress(serverPort)).sync();
//			f.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("", e);

			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();

			throw e;
		}
	}

	@Override
	public void shutdown() {

	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			ChannelPipeline pipeline = arg0.pipeline();
			pipeline.addLast(new NettyProtocolDecoder(maxReceiveBytes));
			pipeline.addLast(new NettyProtocolEncoder());
			// 客户端300秒没收发包，便会触发UserEventTriggered事件到IdleEventHandler
			pipeline.addLast(new IdleStateHandler(0, 0, 300));
			pipeline.addLast(new IoEventHandler(new MessageDispatcher(ServerScanPaths.MESSAGE_PATH)));
		}
	}

}
