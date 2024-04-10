package jforgame.demo.udp;

import jforgame.socket.share.annotation.MessageMeta;

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
