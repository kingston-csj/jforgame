package jforgame.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jforgame.socket.HostAndPort;
import jforgame.socket.IdSession;
import jforgame.socket.codec.SerializerFactory;
import jforgame.socket.message.IMessageDispatcher;
import jforgame.socket.netty.NettyProtocolDecoder;
import jforgame.socket.netty.NettyProtocolEncoder;
import jforgame.socket.netty.NettySession;

import java.net.InetSocketAddress;

public class RpcClientFactory {

    private IMessageDispatcher messageDispatcher;

    private SerializerFactory messageSerializer;

    private EventLoopGroup group = new NioEventLoopGroup(4);

    public RpcClientFactory(IMessageDispatcher messageDispatcher, SerializerFactory messageSerializer) {
        this.messageDispatcher = messageDispatcher;
        this.messageSerializer = messageSerializer;
    }

    public IdSession createSession(HostAndPort hostPort) throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel arg0) throws Exception {
                    ChannelPipeline pipeline = arg0.pipeline();
                    pipeline.addLast(new NettyProtocolDecoder(10240));
                    pipeline.addLast(new NettyProtocolEncoder());
                    pipeline.addLast((new MsgIoHandler(messageDispatcher, messageSerializer)));
                }

            });

            ChannelFuture f = b.connect(new InetSocketAddress(hostPort.getHost(), hostPort.getPort())).sync();
            IdSession session = new NettySession(f.channel());
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
            throw e;
        }
    }
}