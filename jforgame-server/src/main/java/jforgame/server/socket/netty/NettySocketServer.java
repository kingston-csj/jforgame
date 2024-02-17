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
import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.server.socket.MessageDispatcher;
import jforgame.socket.HostAndPort;
import jforgame.socket.ServerNode;
import jforgame.socket.codec.MessageCodec;
import jforgame.socket.codec.struct.StructMessageCodec;
import jforgame.socket.support.IoEventHandler;
import jforgame.socket.support.NettyProtocolDecoder;
import jforgame.socket.support.NettyProtocolEncoder;
import jforgame.socket.support.MessageFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class NettySocketServer implements ServerNode {

    private Logger logger = LoggerFactory.getLogger(NettySocketServer.class);

    // 避免使用默认线程数参数
    private EventLoopGroup bossGroup = new NioEventLoopGroup(4);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private List<HostAndPort> nodesConfig;
    private int maxReceiveBytes;


    public NettySocketServer(HostAndPort hostPort, int maxReceiveBytes) {
        this.nodesConfig = Arrays.asList(hostPort);
        this.maxReceiveBytes = maxReceiveBytes;
    }

    public NettySocketServer(List<HostAndPort> nodesConfig, int maxReceiveBytes) {
        this.nodesConfig = nodesConfig;
        this.maxReceiveBytes = maxReceiveBytes;
    }

    public NettySocketServer() {
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
            pipeline.addLast(new NettyProtocolDecoder(MessageFactoryImpl.getInstance(), messageCodec));
            pipeline.addLast(new NettyProtocolEncoder(MessageFactoryImpl.getInstance(), messageCodec));
            // 客户端300秒没收发包，便会触发UserEventTriggered事件到IdleEventHandler
            pipeline.addLast(new IdleStateHandler(0, 0, 300));
            pipeline.addLast(new IoEventHandler(new MessageDispatcher(ServerScanPaths.MESSAGE_PATH)));
        }
    }

}
