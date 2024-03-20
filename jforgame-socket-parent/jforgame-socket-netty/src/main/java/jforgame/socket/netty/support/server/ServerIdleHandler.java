package jforgame.socket.netty.support.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import jforgame.socket.netty.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerIdleHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger("socketserver");

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            try {
                logger.warn("session [{}] idle, close it from the server side",
                        ChannelUtils.parseRemoteAddress(ctx.channel()));
                ctx.close();
            } catch (Exception e) {
                logger.error("close session failed", e);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}