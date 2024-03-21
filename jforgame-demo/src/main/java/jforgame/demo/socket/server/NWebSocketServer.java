package jforgame.demo.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCounted;
import jforgame.commons.NumberUtil;
import jforgame.demo.ServerScanPaths;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.MessageIoDispatcher;
import jforgame.demo.utils.JsonUtils;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class NWebSocketServer implements ServerNode {

    private final Logger logger = LoggerFactory.getLogger(NWebSocketServer.class);

    private static ChannelIoHandler messageIoHandler = new ChannelIoHandler(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH));

    // 避免使用默认线程数参数
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    private List<HostAndPort> nodesConfig;

    public NWebSocketServer(HostAndPort hostPort) {
        this.nodesConfig = Arrays.asList(hostPort);
    }

    @Override
    public void start() throws Exception {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new WebSocketChannelInitializer());

            for (HostAndPort node : nodesConfig) {
                logger.info("socket server is listening at " + node.getPort() + "......");
                serverBootstrap.bind(new InetSocketAddress(node.getPort())).sync();
            }
        } catch (Exception e) {
            logger.error("", e);

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            throw e;
        }
    }

    @Override
    public void shutdown() throws Exception {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    private class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            ChannelPipeline pipeline = arg0.pipeline();

            // HttpServerCodec: 针对http协议进行编解码
            pipeline.addLast("httpServerCodec", new HttpServerCodec());
            // ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
            pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
            pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));

            // 用于处理websocket, /ws为访问websocket时的uri
            pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));

            pipeline.addLast("socketFrameToMessage", new MessageToMessageCodec<WebSocketFrame, Object>() {
                @Override
                protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
                    if (GameMessageFactory.getInstance().contains(o.getClass())) {
                        String json = JsonUtils.object2String(o);
                        TextFrame frame = new TextFrame();
                        frame.id = String.valueOf(GameMessageFactory.getInstance().getMessageId(o.getClass()));
                        frame.msg = json;
                        list.add(new TextWebSocketFrame(JsonUtils.object2String(frame)));
                    } else if (o instanceof ReferenceCounted) {
                        ((ReferenceCounted) o).retain();
                        list.add(o);
                    } else {
                        list.add(o);
                    }
                }

                @Override
                protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
                    if (frame instanceof TextWebSocketFrame) {
                        String json = ((TextWebSocketFrame) frame).text();
                        TextFrame textFrame = JsonUtils.string2Object(json, TextFrame.class);
                        Class clazz = GameMessageFactory.getInstance().getMessage(NumberUtil.intValue(textFrame.id));
                        Object realMsg = JsonUtils.string2Object(textFrame.msg, clazz);
                        out.add(realMsg);
                    } else if (frame instanceof BinaryWebSocketFrame) {
                        throw new UnsupportedOperationException("BinaryWebSocketFrame not supported");
                    }
                }
            });

            pipeline.addLast(messageIoHandler);
        }
    }

    static class TextFrame {
        // 消息id
        String id;
        // 消息内容
        String msg;
    }


    public static void main(String[] args) throws Exception {
        NWebSocketServer socketServer = new NWebSocketServer(HostAndPort.valueOf(8080));
        socketServer.start();
    }

}