package jforgame.demo.cross.demo;

import jforgame.demo.cross.core.CrossCommands;
import jforgame.demo.game.Modules;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.CROSS, cmd  = CrossCommands.F2G_HEART_BEAT)
public class F2GHeartBeat implements Message {

    private long time = System.currentTimeMillis();

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
