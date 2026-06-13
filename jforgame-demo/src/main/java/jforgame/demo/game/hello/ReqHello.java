package jforgame.demo.game.hello;


import jforgame.socket.core.protocol.annotation.MessageMeta;

@MessageMeta(cmd = 888)
public class ReqHello {

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
