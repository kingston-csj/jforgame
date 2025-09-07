package jforgame.demo.game.hello;


import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd = 888)
@ProtobufClass
public class ReqHello {

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
