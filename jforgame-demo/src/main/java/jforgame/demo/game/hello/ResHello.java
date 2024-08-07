package jforgame.demo.game.hello;

import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd = 999)
public class ResHello {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ResHello{" +
                "content='" + content + '\'' +
                '}';
    }
}
