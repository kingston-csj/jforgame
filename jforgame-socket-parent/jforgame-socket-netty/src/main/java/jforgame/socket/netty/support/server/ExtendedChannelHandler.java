package jforgame.socket.netty.support.server;

import io.netty.channel.ChannelHandler;

import java.util.List;

/**
 * support the addition of extended handler into the channel pipeline.
 */
public interface ExtendedChannelHandler {

    /**
     * Netty ChannelHandlers to be added before built-in Handler.
     */
    List<ChannelHandler> frontChannelHandlers();

    /**
     * Netty ChannelHandlers to be added after built-in Handler.
     */
    List<ChannelHandler> backChannelHandlers();
}
