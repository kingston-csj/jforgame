package jforgame.socket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.SocketDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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
        if (null == channel) {
            return "";
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostAddress();
        }
        return "";
    }

    @Override
    public int getRemotePort() {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getPort();
        }
        return -1;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    @Override
    public String getLocalIP() {
        if (null == channel) {
            return "";
        }
        final InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        if (local != null) {
            return local.getAddress().getHostAddress();
        }
        return "";
    }

    @Override
    public int getLocalPort() {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        if (local != null) {
            return local.getPort();
        }
        return -1;
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
