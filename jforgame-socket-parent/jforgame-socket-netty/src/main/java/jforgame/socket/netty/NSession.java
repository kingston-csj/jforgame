package jforgame.socket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.protocol.message.SocketDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Netty session.
 * This class encapsulates Netty's Channel object, providing session-related operation methods.
 * A session refers to a communication between client and server, containing connection information and session attributes.
 * The lifecycle of a session starts when the client connects to the server and ends when the client disconnects
 * or the server actively closes the session.
 * Sessions can also contain some extended attributes for storing session-related information.
 */
public class NSession implements IdSession {

    private static Logger logger = LoggerFactory.getLogger(IdSession.class);

    /**
     * socket io channel
     */
    protected Channel channel;

    /**
     * extension properties
     */
    protected Map<String, Object> attrs = new HashMap<>();

    public NSession(Channel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public void send(Object packet) {
        if (packet instanceof SocketDataFrame) {
            channel.writeAndFlush(packet);
        } else {
            channel.writeAndFlush(SocketDataFrame.withoutIndex(packet));
        }
    }

    @Override
    public void sendAndClose(Object packet) {
        ChannelFutureListener closeListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    try {
                        close();
                    } catch (IOException e) {
                        logger.info("", e);
                    }
                }
            }
        };
        if (packet instanceof SocketDataFrame) {
            channel.writeAndFlush(packet).addListener(closeListener);
        } else {
            channel.writeAndFlush(SocketDataFrame.withoutIndex(packet)).addListener(closeListener);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public String getRemoteIP() {
        return ChannelUtils.parseRemoteIP(channel);
    }

    @Override
    public int getRemotePort() {
        return ChannelUtils.parseRemotePort(channel);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    @Override
    public String getLocalIP() {
        return ChannelUtils.parseLocalIP(channel);
    }

    @Override
    public int getLocalPort() {
        return ChannelUtils.parseLocalPort(channel);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attrs.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attrs.get(key);
    }

    @Override
    public Channel getRawSession() {
        return this.channel;
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

}
