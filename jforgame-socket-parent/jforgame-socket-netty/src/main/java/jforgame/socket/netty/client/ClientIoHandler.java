package jforgame.socket.netty.client;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.client.Traceable;
import jforgame.socket.netty.ChannelUtils;
import jforgame.socket.netty.NSession;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.IMessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientIoHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ClientIoHandler.class);

    private IMessageDispatcher messageDispatcher;


    public ClientIoHandler(IMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!ChannelUtils.addChannelSession(ctx.channel(),
                new NSession(ctx.channel()))) {
            ctx.channel().close();
            logger.error("Duplicate session,IP=[{}]", ChannelUtils.getIp(ctx.channel()));
            return;
        }

        IdSession session = ChannelUtils.getSessionBy(ctx.channel());
        messageDispatcher.onSessionCreated(session);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        logger.debug("receive pact, content is {}", packet.getClass().getSimpleName());

        final Channel channel = context.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        if (packet instanceof Traceable) {
            Traceable traceable = (Traceable) packet;
            RpcResponseData responseData = new RpcResponseData();
            responseData.setResponse(packet);
            CallBackService.getInstance().fillCallBack(traceable.getIndex(), responseData);
        }
        messageDispatcher.dispatch(session, packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel[{}] inactive", ctx.channel());
        IdSession session = ChannelUtils.getSessionBy(ctx.channel());
        messageDispatcher.onSessionClosed(session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (channel.isActive() || channel.isOpen()) {
            ctx.close();
        }
        if (!(cause instanceof IOException)) {
            logger.error("remote:" + channel.remoteAddress(), cause);
        }
    }
}