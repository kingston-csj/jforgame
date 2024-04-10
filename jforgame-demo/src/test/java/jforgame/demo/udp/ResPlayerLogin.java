package jforgame.demo.udp;

import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd = 55556)
public class ResPlayerLogin extends UdpMessage {

    private long playerId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
