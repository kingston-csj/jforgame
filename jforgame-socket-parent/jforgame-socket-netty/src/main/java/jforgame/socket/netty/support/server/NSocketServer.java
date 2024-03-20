package jforgame.socket.netty.support.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NSocketServer implements ServerNode {

    private final Logger logger = LoggerFactory.getLogger("socketserver");

    protected List<HostAndPort> nodesConfig;
    protected ChannelInitializer<SocketChannel> childChannelInitializer;

    private final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
    // 避免使用默认线程数参数
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    boolean useEpollForLinux;

    boolean usePooledBuff;

    @Override
    public void start() throws Exception {
        try {
            bossGroup = useEpoll() ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
            workerGroup = useEpoll() ? new EpollEventLoopGroup(CORE_SIZE) : new NioEventLoopGroup(CORE_SIZE);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(childChannelInitializer);

            if (usePooledBuff) {
                bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            } else {
                bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                        .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            }

            for (HostAndPort node : nodesConfig) {
                logger.info("socket server is listening at " + node.getPort() + "......");
                bootstrap.bind(new InetSocketAddress(node.getPort())).sync();
            }
        } catch (Exception e) {
            logger.error("", e);

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            throw e;
        }
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpollForLinux = useEpoll;
    }

    private boolean useEpoll() {
        return useEpollForLinux && "linux".equalsIgnoreCase(System.getProperty("os.name"))
                && Epoll.isAvailable();
    }

    @Override
    public void shutdown() throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("socket server stopped successfully");
    }

}
