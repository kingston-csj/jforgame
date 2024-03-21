package jforgame.socket.netty.support.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jforgame.codec.MessageCodec;
import jforgame.socket.client.AbstractSocketClient;
import jforgame.socket.netty.NSession;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.netty.support.DefaultProtocolDecoder;
import jforgame.socket.netty.support.DefaultProtocolEncoder;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TcpSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);

    public TcpSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
    }

    @Override
    public IdSession openSession() throws IOException {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel arg0) throws Exception {
                    ChannelPipeline pipeline = arg0.pipeline();
                    pipeline.addLast(new DefaultProtocolDecoder(messageFactory, messageCodec));
                    pipeline.addLast(new DefaultProtocolEncoder(messageFactory, messageCodec));
                    pipeline.addLast((new CallbackHandler()));
                    pipeline.addLast((new ChannelIoHandler(ioDispatcher)));
                }

            });

            ChannelFuture f = b.connect(new InetSocketAddress(targetAddress.getHost(), targetAddress.getPort())).sync();
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
}