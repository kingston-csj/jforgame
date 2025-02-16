package jforgame.socket.netty.support.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCounted;
import jforgame.codec.MessageCodec;
import jforgame.commons.JsonUtil;
import jforgame.commons.NumberUtil;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.*;
import jforgame.socket.support.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * simple web socket server
 * support {@link TextWebSocketFrame} and  {@link BinaryWebSocketFrame}
 */
public class WebSocketServer implements ServerNode {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    SocketIoDispatcher socketIoDispatcher;

    ChannelIoHandler messageIoHandler;

    // 避免使用默认线程数参数
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    HostAndPort nodeConfig;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    String websocketPath;


    /**
     * In the server side, the connection will be closed if it is idle for a certain period of time.
     */
    int idleMilliSeconds;

//    SslContext sslContext;

    private ServerIdleHandler serverIdleHandler;

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
    public  byte[] toByteArray(ByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }
    private class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
//            if (sslContext != null) {
//                pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
//            }
            pipeline.addLast("httpServerCodec", new HttpServerCodec());
            pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
            pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(websocketPath, null, true));
            // WebSocketFrame vs Message codec
            pipeline.addLast("socketFrameToMessage", new MessageToMessageCodec<WebSocketFrame, Object>() {
                @Override
                protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
                    if (o instanceof SocketDataFrame) {
                        SocketDataFrame socketDataFrame = (SocketDataFrame) o;
                        Object message = socketDataFrame.getMessage();
                        if (message instanceof SCMessage) {
                            SCMessage scMessage = (SCMessage) message;
                            int errorCode = scMessage.getErrorCode();
                            int cmd = scMessage.getCmd();
                            int totalSize = DefaultMessageHeader.SIZE + 4;
                            byte[] encode = null;
                            if (errorCode == 0) {
                                encode = messageCodec.encode(scMessage);
                                totalSize += encode.length;
                            }
                            // Write header
                            ByteBuf header = ctx.alloc().buffer(totalSize);
                            header.writeInt(totalSize);
                            header.writeInt(socketDataFrame.getIndex());
                            header.writeInt(cmd);
                            header.writeInt(errorCode);
                            if (errorCode == 0) {
                                header.writeBytes(encode);
                            }
                            list.add(new BinaryWebSocketFrame(header));
                        }
                    } else if (o instanceof ReferenceCounted) {
                        ((ReferenceCounted) o).retain();
                        list.add(o);
                    } else {
                        list.add(o);
                    }
                }

                @Override
                protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws
                        Exception {
                    if (frame instanceof TextWebSocketFrame) {
                        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;
                        String json = textWebSocketFrame.text();
                        TextFrame textFrame = JsonUtil.string2Object(json, TextFrame.class);
                        if (textFrame == null) {
                            logger.error("json failed, data [{}]", json);
                            return;
                        }
                        Class<?> clazz = messageFactory.getMessage(NumberUtil.intValue(textFrame.cmd));
                        Object realMsg = JsonUtil.string2Object(textFrame.msg, clazz);
                        MessageHeader header = new DefaultMessageHeader();
                        header.setCmd(textFrame.cmd);
                        // TextWebSocketFrame的byte长度近似取值即可
                        header.setMsgLength(textWebSocketFrame.content().readableBytes());
                        header.setIndex(textFrame.index);
                        RequestDataFrame requestDataFrame = new RequestDataFrame(header, realMsg);
                        out.add(requestDataFrame);
                    } else if (frame instanceof BinaryWebSocketFrame) {
                        BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
                        ByteBuf in = binaryFrame.content();
                        byte[] headerData = new byte[DefaultMessageHeader.SIZE];
                        in.readBytes(headerData);
                        MessageHeader headerMeta = new DefaultMessageHeader();
                        headerMeta.read(headerData);
                        int length = headerMeta.getMsgLength();
                        int bodySize = length - DefaultMessageHeader.SIZE;
                        int cmd = headerMeta.getCmd();
                        byte[] body = new byte[bodySize];
                        in.readBytes(body);
                        Class<?> clazz = messageFactory.getMessage(NumberUtil.intValue(cmd));
                        Object message = messageCodec.decode(clazz, body);
                        RequestDataFrame requestDataFrame = new RequestDataFrame(headerMeta, message);
                        out.add(requestDataFrame);
                    }
                }
            });

            if (idleMilliSeconds > 0) {
                // 客户端XXX没收发包，便会触发UserEventTriggered事件到IdleEventHandler
                pipeline.addLast(new IdleStateHandler(0, 0, idleMilliSeconds,
                        TimeUnit.MILLISECONDS));
                pipeline.addLast("serverIdleHandler", new ServerIdleHandler(socketIoDispatcher));
            }

            pipeline.addLast(messageIoHandler);
        }
    }

    static class TextFrame {
        // 消息包序号
        int index;
        // 消息id
        int cmd;
        // 消息内容
        String msg;
    }

}
