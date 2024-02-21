package jforgame.server.cross.core;

import jforgame.commons.TimeUtil;
import jforgame.socket.mina.MSession;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public class MSessionPlus extends MSession {

    private long lastWrittenTime = System.currentTimeMillis();

    public MSessionPlus(IoSession session) {
        super(session);
    }

    public void send(Object packet) {
        WriteFuture future = this.session.write(packet);
        future.addListener(new IoFutureListener<IoFuture>() {
            @Override
            public void operationComplete(IoFuture future) {
                MSessionPlus.this.lastWrittenTime = System.currentTimeMillis();
            }
        });
    }

    public long getLastWrittenTime() {
        return lastWrittenTime;
    }

    public void setLastWrittenTime(long lastWrittenTime) {
        this.lastWrittenTime = lastWrittenTime;
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        long diff = now - lastWrittenTime;
        return diff > 30 * TimeUtil.MILLIS_PER_SECOND;
    }

    public void sendMessage(Object message, Runnable sentCallback) {
        WriteFuture future = this.session.write(message);
        future.addListener(new IoFutureListener<IoFuture>() {
            @Override
            public void operationComplete(IoFuture future) {
                MSessionPlus.this.lastWrittenTime = System.currentTimeMillis();
                sentCallback.run();
            }
        });
    }
}
