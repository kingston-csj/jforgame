package jforgame.socket.netty.support.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCounted;
import jforgame.codec.MessageCodec;
import jforgame.commons.JsonUtil;
import jforgame.commons.NumberUtil;
import jforgame.socket.netty.WebSocketJsonFrame;
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

    public WebSocketFrameToSocketDataCodec(MessageCodec messageCodec, MessageFactory messageFactory) {
        this.messageCodec = messageCodec;
        this.messageFactory = messageFactory;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof SocketDataFrame) {
            SocketDataFrame socketDataFrame = (SocketDataFrame) o;
            Object message = socketDataFrame.getMessage();
            String json = JsonUtil.object2String(message);
            WebSocketJsonFrame frame = new WebSocketJsonFrame();
            frame.cmd = messageFactory.getMessageId(message.getClass());
            frame.msg = json;
            frame.index = socketDataFrame.getIndex();
            list.add(new TextWebSocketFrame(JsonUtil.object2String(frame)));
        } else if (o instanceof ReferenceCounted) {
            ((ReferenceCounted) o).retain();
            list.add(o);
        } else {
            // 其他类型的对象（如HTTP请求）直接传递，不做处理
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