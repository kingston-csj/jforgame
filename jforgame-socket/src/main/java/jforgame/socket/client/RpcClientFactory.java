package jforgame.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jforgame.codec.MessageCodec;
import jforgame.socket.HostAndPort;
import jforgame.socket.IdSession;
import jforgame.socket.share.message.IMessageDispatcher;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.netty.NSession;
import jforgame.socket.support.NettyProtocolDecoder;
import jforgame.socket.support.NettyProtocolEncoder;

import java.net.InetSocketAddress;

public class RpcClientFactory {

    private IMessageDispatcher messageDispatcher;

    private MessageFactory messageFactory;

    private MessageCodec messageCodec;

    private EventLoopGroup group = new NioEventLoopGroup(4);

    public RpcClientFactory(IMessageDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }

    public IdSession createSession(HostAndPort hostPort) throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel arg0) throws Exception {
                    ChannelPipeline pipeline = arg0.pipeline();
                    pipeline.addLast(new NettyProtocolDecoder(messageFactory, messageCodec));
                    pipeline.addLast(new NettyProtocolEncoder(messageFactory, messageCodec));
                    pipeline.addLast((new MsgIoHandler(messageDispatcher)));
                }

            });

            ChannelFuture f = b.connect(new InetSocketAddress(hostPort.getHost(), hostPort.getPort())).sync();
            IdSession session = new NSession(f.channel());
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
            throw e;
        }
    }
}