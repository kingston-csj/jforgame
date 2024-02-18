package jforgame.socket.netty;

import io.netty.channel.Channel;
import jforgame.socket.IdSession;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NSession implements IdSession {

    /**
     * socket io channel
     */
    private Channel channel;

    /**
     * extension properties
     */
    private Map<String, Object> attrs = new HashMap<>();

    public NSession(Channel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public void sendPacket(Object packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public long getOwnerId() {
        if (attrs.containsKey(ID)) {
            return (long) attrs.get(ID);
        }
        return 0;
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
    public Object setAttribute(String key, Object value) {
        attrs.put(key, value);
        return value;
    }

    @Override
    public Object getAttribute(String key) {
        return attrs.get(key);
    }

}
