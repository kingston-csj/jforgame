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
import jforgame.codec.struct.StructMessageCodec;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.server.ServerNode;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpSocketServer implements ServerNode {

    private static final Logger logger = LoggerFactory.getLogger("socketserver");

    private EventLoopGroup group = new NioEventLoopGroup();

    protected HostAndPort nodesConfig = HostAndPort.valueOf(8088);

    public SocketIoDispatcher socketIoDispatcher;

    public MessageFactory messageFactory;

    public MessageCodec messageCodec;

    @Override
    public void start() throws Exception {
        try {
            SessionManager.getInstance().schedule();
            
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
                            pipeline.addLast(new UdpChannelIoHandler(socketIoDispatcher));

                        }
                    });

            logger.info("socket server is listening at " + nodesConfig.getPort() + "......");
            bootstrap.bind(nodesConfig.getPort()).sync().channel().closeFuture().sync();

        } catch (Exception e) {
            logger.error("", e);
            group.shutdownGracefully();
        }
    }

    @Override
    public void shutdown() throws Exception {
        group.shutdownGracefully();
    }


    public static void main(String[] args) throws Exception {
        UdpSocketServer udpSocketServer = new UdpSocketServer();
        udpSocketServer.messageFactory = GameMessageFactory.getInstance();
        udpSocketServer.messageCodec = new StructMessageCodec();
        udpSocketServer.socketIoDispatcher = new MessageIoDispatcher();

        udpSocketServer.start();
    }
}
