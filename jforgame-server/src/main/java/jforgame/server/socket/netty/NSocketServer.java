package jforgame.server.socket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jforgame.codec.MessageCodec;
import jforgame.codec.struct.StructMessageCodec;
import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.server.socket.MessageIoDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import jforgame.socket.netty.support.DefaultSocketIoHandler;
import jforgame.socket.netty.support.DefaultProtocolDecoder;
import jforgame.socket.netty.support.DefaultProtocolEncoder;
import jforgame.socket.support.DefaultMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class NSocketServer implements ServerNode {

    private Logger logger = LoggerFactory.getLogger(NSocketServer.class);

    // 避免使用默认线程数参数
    private EventLoopGroup bossGroup = new NioEventLoopGroup(4);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private List<HostAndPort> nodesConfig;
    private int maxReceiveBytes;

    public NSocketServer(HostAndPort hostPort) {
        this.nodesConfig = Arrays.asList(hostPort);
    }

    public NSocketServer(List<HostAndPort> nodesConfig) {
        this.nodesConfig = nodesConfig;
    }

    @Override
    public void start() throws Exception {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            for (HostAndPort node : nodesConfig) {
                logger.info("socket server is listening at " + node.getPort() + "......");
                b.bind(new InetSocketAddress(node.getPort())).sync();
            }
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
            MessageCodec messageCodec = new StructMessageCodec();
            pipeline.addLast(new DefaultProtocolDecoder(DefaultMessageFactory.getInstance(), messageCodec));
            pipeline.addLast(new DefaultProtocolEncoder(DefaultMessageFactory.getInstance(), messageCodec));
            // 客户端300秒没收发包，便会触发UserEventTriggered事件到IdleEventHandler
            pipeline.addLast(new IdleStateHandler(300, 300, 300));
            pipeline.addLast(new DefaultSocketIoHandler(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH)));
        }
    }

}
