package jforgame.socket.netty.support.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import jforgame.socket.netty.ChannelUtils;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerIdleHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger("socketserver");

    private SocketIoDispatcher socketIoDispatcher;

    public ServerIdleHandler(SocketIoDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            try {
                logger.warn("session [{}] idle, close it from the server side",
                        ChannelUtils.parseRemoteAddress(ctx.channel()));
                Channel channel = ctx.channel();
                IdSession userSession = ChannelUtils.getSessionBy(channel);
                socketIoDispatcher.onSessionClosed(userSession);
                ctx.close();
            } catch (Exception e) {
                logger.error("close session failed", e);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}