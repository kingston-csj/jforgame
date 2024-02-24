package jforgame.demo.cross.core;

import jforgame.commons.TimeUtil;
import jforgame.socket.mina.MSession;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public class MSessionPlus extends MSession {

    public MSessionPlus(IoSession session) {
        super(session);
    }

    public void send(Object packet) {
        WriteFuture future = this.session.write(packet);
    }

    public long getLastWriteTime() {
        return this.session.getLastWriteTime();
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        long diff = now - getLastWriteTime();
        return diff > 30 * TimeUtil.MILLIS_PER_SECOND;
    }

    public void sendMessage(Object message, Runnable sentCallback) {
        WriteFuture future = this.session.write(message);
        future.addListener(new IoFutureListener<IoFuture>() {
            @Override
            public void operationComplete(IoFuture future) {
                sentCallback.run();
            }
        });
    }
}
