package jforgame.demo.udp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.socket.netty.ChannelUtils;
import jforgame.socket.netty.NSession;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ChannelHandler.Sharable
public class UdpChannelIoHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger("socketserver");

    /** 消息分发器 */
    private final SocketIoDispatcher messageDispatcher;

    public UdpChannelIoHandler(SocketIoDispatcher messageDispatcher) {
        super();
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ChannelUtils.duplicateBindingSession(ctx.channel(), new NSession(channel));
        SessionManager.getInstance().buildSession(ChannelUtils.getSessionBy(channel));
        System.out.println("socket register " + channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        logger.debug("receive pact, content is {}", packet.getClass().getSimpleName());

        final Channel channel = context.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        messageDispatcher.dispatch(session, packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("socket inactive " + channel);
        IdSession userSession = ChannelUtils.getSessionBy(channel);
        messageDispatcher.onSessionClosed(userSession);
    }

}
