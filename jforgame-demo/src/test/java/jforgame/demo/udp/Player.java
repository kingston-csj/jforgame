package jforgame.demo.udp;

import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;

public class Player {

    private long id;

    private HostAndPort remoteAddr;

    public void receive(IdSession session, UdpMessage message) {
        message.setReceiverIp(remoteAddr.getHost());
        message.setReceiverPort(remoteAddr.getPort());
        session.send(message);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HostAndPort getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(HostAndPort remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
}
