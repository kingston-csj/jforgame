package jforgame.demo.udp;

import jforgame.socket.core.protocol.annotation.MessageMeta;

@MessageMeta(cmd = 55555)
public class ReqLogin extends UdpMessage {

    private long playerId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
