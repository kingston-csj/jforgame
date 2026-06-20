package jforgame.socket.netty.server;


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
 * WebSocket frame aggregator, handles large data requests, aggregates multiple frames from client into one complete frame
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

        // Check if exceeding maximum length
        if (composite.readableBytes() > maxFrameSize) {
            throw new TooLongFrameException("WebSocket frame length exceeded " + maxFrameSize + " bytes");
        }

        // Append data
        composite.addComponent(true, frame.content().retain());

        // If it's the last frame, aggregation is complete
        if (frame.isFinalFragment()) {
            ByteBuf content = composite;
            composite = null; // Reset state

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