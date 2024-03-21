package jforgame.demo.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jforgame.codec.MessageCodec;
import jforgame.socket.client.AbstractSocketClient;
import jforgame.socket.netty.NSession;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class UdpSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);

    public UdpSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
    }

    @Override
    public IdSession openSession() throws IOException {
        try {


            final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.group(nioEventLoopGroup);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
//        bootstrap.handler(new UdpClientChannelInitializer());
            bootstrap.handler(new UdpProtoBufClientChannelInitializer());
//            // 监听端口
//            int port = 9999;
//            ChannelFuture sync = bootstrap.bind(0).sync();
//            Channel udpChannel = sync.channel();


            ChannelFuture f = bootstrap.connect(new InetSocketAddress(targetAddress.getHost(), targetAddress.getPort())).sync();
            IdSession session = new NSession(f.channel());
            this.session = session;
            return session;
        } catch (Exception e) {
            group.shutdownGracefully();
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.session.close();
    }


     class UdpProtoBufClientChannelInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("protocolDecoder", new UdpProtocolDecoder(messageFactory, messageCodec));
            pipeline.addLast("protocolEncoder", new UdpProtocolEncoder(messageFactory, messageCodec));
            pipeline.addLast(new UdpChannelIoHandler(ioDispatcher));
        }
    }
}