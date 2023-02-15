package jforgame.server.game.hello;

import jforgame.socket.client.Traceful;
import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd= 999)
public class ResHello implements Traceful {

    private int index;
    private String content;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
