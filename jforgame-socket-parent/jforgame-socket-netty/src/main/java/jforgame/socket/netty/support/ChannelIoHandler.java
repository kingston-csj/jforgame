package jforgame.socket.netty.support;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.commons.JsonUtil;
import jforgame.socket.netty.ChannelUtils;
import jforgame.socket.netty.NSession;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.RequestDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 通道io处理器，把netty各种事件都转发给框架内置的消息分发器
 */
@ChannelHandler.Sharable
public class ChannelIoHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger("socketserver");

    /**
     * 消息分发器
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
        RequestDataFrame dataFrame = (RequestDataFrame) frame;
        if (logger.isDebugEnabled()) {
            logger.debug("receive pact, content is {}", JsonUtil.object2String(dataFrame.getMessage()));
        }

        final Channel channel = context.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        messageDispatcher.dispatch(session, frame);
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
