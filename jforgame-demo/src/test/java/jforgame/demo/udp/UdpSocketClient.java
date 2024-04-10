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
import jforgame.codec.struct.Codec;
import jforgame.codec.struct.StructMessageCodec;
import jforgame.commons.JsonUtil;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.client.AbstractSocketClient;
import jforgame.socket.netty.NSession;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.SocketIoDispatcherAdapter;
import jforgame.socket.share.message.MessageFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class UdpSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);

    private HostAndPort nativeHostPort;


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
            bootstrap.handler(new UdpProtoBufClientChannelInitializer());
            ChannelFuture f = bootstrap.connect(new InetSocketAddress(targetAddress.getHost(), targetAddress.getPort()),
                    new InetSocketAddress(nativeHostPort.getHost(), nativeHostPort.getPort())).sync();
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


    public void send(UdpMessage message) {
        message.setSenderIp(nativeHostPort.getHost());
        message.setSenderPort(nativeHostPort.getPort());

        message.setReceiverIp(targetAddress.getHost());
        message.setReceiverPort(targetAddress.getPort());
        session.send(message);
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

    private static AtomicLong idFactory = new AtomicLong(1000);

    public static void main(String[] args) throws Exception {
        MessageCodec messageCodec = new StructMessageCodec();
        GameMessageFactory.getInstance().registeredClassTypes().forEach(Codec::getSerializer);
        for (int i = 0; i < 10; i++) {
            System.out.println("----------i=" + i);
            UdpSocketClient socketClient = new UdpSocketClient(new SocketIoDispatcherAdapter() {
                @Override
                public void dispatch(IdSession session, Object message) {
                    System.out.println("receive package ---------" + JsonUtil.object2String(message));
                }

            }, GameMessageFactory.getInstance(), messageCodec, HostAndPort.valueOf(8088));

            socketClient.nativeHostPort = HostAndPort.valueOf(8099 + i);
            socketClient.openSession();
            for (int j = 0; j < 1; j++) {
                ReqLogin req = new ReqLogin();
                req.setPlayerId(idFactory.getAndIncrement());
                socketClient.send(req);
            }
        }

    }

}