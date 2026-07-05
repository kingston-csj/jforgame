package jforgame.socket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.commons.util.JsonUtil;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.protocol.message.RequestDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel IO handler, forwards various Netty events to the built-in message dispatcher of the framework.
 */
@ChannelHandler.Sharable
public class ChannelIoHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger("socketserver");

    /**
     * Message dispatcher
     */
    private final SocketIoDispatcher messageDispatcher;

    public ChannelIoHandler(SocketIoDispatcher messageDispatcher) {
        super();
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (ChannelUtils.duplicateBindingSession(ctx.channel(), new NSession(channel))) {
            ctx.channel().close();
            logger.error("Duplicate session,IP=[{}]", ChannelUtils.parseRemoteAddress(channel));
            return;
        }
        IdSession userSession = ChannelUtils.getSessionBy(channel);
        messageDispatcher.onSessionCreated(userSession);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object frame) throws Exception {
        assert frame instanceof RequestDataFrame;
        RequestDataFrame requestDataFrame = (RequestDataFrame) frame;
        if (logger.isDebugEnabled()) {
            logger.debug("receive pact, content is {}", JsonUtil.object2String(requestDataFrame.getMessage()));
        }

        final Channel channel = context.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        RequestContext requestContext = new RequestContext();
        requestContext.setSession(session);
        requestContext.setRequest(requestDataFrame.getMessage());
        requestContext.setHeader(requestDataFrame.getHeader());
        messageDispatcher.dispatch(session, requestContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        IdSession userSession = ChannelUtils.getSessionBy(channel);
        messageDispatcher.onSessionClosed(userSession);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        IdSession userSession = ChannelUtils.getSessionBy(channel);
        messageDispatcher.exceptionCaught(userSession, cause);
    }
}
