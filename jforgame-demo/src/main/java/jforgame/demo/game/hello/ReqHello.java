package jforgame.demo.game.hello;


import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd = 888)
public class ReqHello {

    @Protobuf
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
