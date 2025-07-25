package jforgame.socket.netty.support.server;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * websocket帧聚合器，处理大数据请求，将客户端多个帧为一个完整的帧
 * @since 2.4.0
 */
public class WebSocketFrameAggregator extends MessageToMessageDecoder<WebSocketFrame> {
    private final int maxFrameSize;
    private CompositeByteBuf composite;

    public WebSocketFrameAggregator(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (composite == null) {
            composite = ctx.alloc().compositeBuffer();
        }

        // 检查是否超过最大长度
        if (composite.readableBytes() > maxFrameSize) {
            throw new TooLongFrameException("WebSocket frame length exceeded " + maxFrameSize + " bytes");
        }

        // 追加数据
        composite.addComponent(true, frame.content().retain());

        // 如果是最后一个帧，聚合完成
        if (frame.isFinalFragment()) {
            ByteBuf content = composite;
            composite = null; // 重置状态

            if (frame instanceof TextWebSocketFrame) {
                out.add(new TextWebSocketFrame(content));
            } else if (frame instanceof BinaryWebSocketFrame) {
                out.add(new BinaryWebSocketFrame(content));
            } else {
                content.release();
                throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (composite != null) {
            composite.release();
            composite = null;
        }
    }
}