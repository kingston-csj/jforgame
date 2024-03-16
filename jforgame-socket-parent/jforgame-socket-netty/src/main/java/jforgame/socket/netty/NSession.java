package jforgame.socket.netty;

import io.netty.channel.Channel;
import jforgame.socket.share.IdSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NSession implements IdSession {

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
        channel.writeAndFlush(packet);
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
