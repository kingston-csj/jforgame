package jforgame.demo.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UdpSocketServer implements ServerNode {

    private static final Logger logger = LoggerFactory.getLogger("socketserver");


    private EventLoopGroup group = new NioEventLoopGroup();

    protected List<HostAndPort> nodesConfig;

   public SocketIoDispatcher socketIoDispatcher;

    public  MessageFactory messageFactory;

    public   MessageCodec messageCodec;

     ChannelInitializer<DatagramChannel> channelInitializer;

    @Override
    public void start() throws Exception {


        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        public void initChannel(DatagramChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("protocolDecoder", new UdpProtocolDecoder(messageFactory, messageCodec));
                            pipeline.addLast("protocolEncoder", new UdpProtocolEncoder(messageFactory, messageCodec));
//                            pipeline.addLast("UDPServerHandler", new UDPServerHandler());
                            pipeline.addLast(new UdpChannelIoHandler(socketIoDispatcher));

                        }
                    });

            for (HostAndPort node : nodesConfig) {
                logger.info("socket server is listening at " + node.getPort() + "......");
//                bootstrap.bind(new InetSocketAddress(node.getPort())).sync();
                bootstrap.bind(node.getPort()).sync().channel().closeFuture().sync();
            }

        } catch (Exception e) {
            logger.error("", e);
            group.shutdownGracefully();
        }
    }

    @Override
    public void shutdown() throws Exception {
        group.shutdownGracefully();
    }
}
