package jforgame.server.game.hello;


import jforgame.socket.client.Traceful;
import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd= 888)
public class ReqHello implements Traceful {

    private int index;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
