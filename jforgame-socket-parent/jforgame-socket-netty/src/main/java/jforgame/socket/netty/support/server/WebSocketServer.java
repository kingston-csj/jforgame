package jforgame.socket.netty.support.server;

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
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCounted;
import jforgame.commons.JsonUtil;
import jforgame.commons.NumberUtil;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * simple web socket server
 * support {@link TextWebSocketFrame} only
 * unsupported {@link BinaryWebSocketFrame}
 */
public class WebSocketServer implements ServerNode {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static ChannelIoHandler messageIoHandler;

    // 避免使用默认线程数参数
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    private final HostAndPort nodeConfig;

    private final MessageFactory messageFactory;

    private String
            websocketPath = "/ws";

    public WebSocketServer(HostAndPort hostPort, MessageFactory messageFactory, ChainedMessageDispatcher messageDispatcher) {
        this.nodeConfig = hostPort;
        this.messageFactory = messageFactory;
        messageIoHandler = new ChannelIoHandler(messageDispatcher);
    }

    @Override
    public void start() throws Exception {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new WebSocketChannelInitializer());

            logger.info("socket server is listening at " + nodeConfig.getPort() + "......");
            serverBootstrap.bind(new InetSocketAddress(nodeConfig.getPort())).sync();
        } catch (Exception e) {
            logger.error("", e);

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            throw e;
        }
    }

    @Override
    public void shutdown() throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            ChannelPipeline pipeline = arg0.pipeline();

            pipeline.addLast("httpServerCodec", new HttpServerCodec());
            pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
            pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
            pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(websocketPath));

            // WebSocketFrame vs Message codec
            pipeline.addLast("socketFrameToMessage", new MessageToMessageCodec<WebSocketFrame, Object>() {
                @Override
                protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
                    if (messageFactory.contains(o.getClass())) {
                        String json = JsonUtil.object2String(o);
                        TextFrame frame = new TextFrame();
                        frame.id = String.valueOf(messageFactory.getMessageId(o.getClass()));
                        frame.msg = json;
                        list.add(new TextWebSocketFrame(JsonUtil.object2String(frame)));
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
                        TextFrame textFrame = JsonUtil.string2Object(json, TextFrame.class);
                        if (textFrame == null) {
                            logger.error("json failed, data [{}]", json);
                            return;
                        }
                        Class<?> clazz = messageFactory.getMessage(NumberUtil.intValue(textFrame.id));
                        Object realMsg = JsonUtil.string2Object(textFrame.msg, clazz);
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

}
