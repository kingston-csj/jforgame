package jforgame.socket.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCounted;
import jforgame.codec.MessageCodec;
import jforgame.commons.util.JsonUtil;
import jforgame.commons.util.NumberUtil;
import jforgame.socket.share.WebSocketJsonFrame;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.MessageHeader;
import jforgame.socket.share.message.RequestDataFrame;
import jforgame.socket.share.message.SocketDataFrame;
import jforgame.socket.support.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * websocket 消息帧与框架数据帧之间的编解码器
 * 将 {@link WebSocketFrame} 转换为 {@link SocketDataFrame} 或其他类型的消息
 */
public class WebSocketFrameToSocketDataCodec extends MessageToMessageCodec<WebSocketFrame, Object> {

    Logger logger = LoggerFactory.getLogger(WebSocketFrameToSocketDataCodec.class);
    private final MessageCodec messageCodec;

    private final MessageFactory messageFactory;

    private static final int MIN_BUFF_THRESHOLD = 1024;

    // 编码buff本地缓存
    private static final ThreadLocal<ByteBuf> smallBufferCache = ThreadLocal.withInitial(
            () -> ByteBufAllocator.DEFAULT.buffer(MIN_BUFF_THRESHOLD) // 使用默认池化分配器，初始容量 1KB
    );

    // 数据帧类型
    private int frameType;

    public WebSocketFrameToSocketDataCodec(MessageCodec messageCodec, MessageFactory messageFactory) {
        this(WebSocketServer.FRAME_TYPE_TEXT, messageCodec, messageFactory);
    }

    public WebSocketFrameToSocketDataCodec(int frameType, MessageCodec messageCodec, MessageFactory messageFactory) {
        this.frameType = frameType;
        this.messageCodec = messageCodec;
        this.messageFactory = messageFactory;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof SocketDataFrame) {
            SocketDataFrame socketDataFrame = (SocketDataFrame) o;
            Object message = socketDataFrame.getMessage();
            // 文本格式
            if (frameType == WebSocketServer.FRAME_TYPE_TEXT) {
                String json = JsonUtil.object2String(message);
                WebSocketJsonFrame frame = new WebSocketJsonFrame();
                frame.index = socketDataFrame.getIndex();
                frame.cmd = this.messageFactory.getMessageId(message.getClass());
                frame.msg = json;
                list.add(new TextWebSocketFrame(JsonUtil.object2String(frame)));
            } else {
                // 二进制格式
                byte[] body = this.messageCodec.encode(message);
                MessageHeader header = new DefaultMessageHeader();
                header.setCmd(this.messageFactory.getMessageId(message.getClass()));
                header.setMsgLength(body.length + 12);
                header.setIndex(socketDataFrame.getIndex());
                int requiredSize = header.getMsgLength();
                ByteBuf buffer;
                boolean usedCacheBuff = false;
                if (requiredSize <= MIN_BUFF_THRESHOLD) { // 小消息使用缓存
                    buffer = smallBufferCache.get();
                    // 检查引用计数，若已释放则重新创建
                    if (buffer.refCnt() == 0) {
                        buffer = ctx.alloc().buffer(MIN_BUFF_THRESHOLD);
                        smallBufferCache.set(buffer); // 更新缓存
                    }
                    usedCacheBuff = true;
                    buffer.clear(); // 清空复用
                } else {
                    // 大消息按需分配
                    buffer = ctx.alloc().buffer(requiredSize);
                }

                if (usedCacheBuff) {
                    // 手动增加计数器，防止转成BinaryWebSocketFrame后被自动释放
                    buffer.retain();
                }

                buffer.writeBytes(header.write());
                buffer.writeBytes(body);
                list.add(new BinaryWebSocketFrame(buffer));
            }

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
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;
            String json = textWebSocketFrame.text();
            WebSocketJsonFrame textFrame = JsonUtil.string2Object(json, WebSocketJsonFrame.class);
            if (textFrame == null) {
                logger.error("json failed, data [{}]", json);
                return;
            }
            Class<?> clazz = messageFactory.getMessage(NumberUtil.intValue(textFrame.cmd));
            Object realMsg = JsonUtil.string2Object(textFrame.msg, clazz);
            MessageHeader header = new DefaultMessageHeader();
            header.setCmd(textFrame.cmd);
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
}