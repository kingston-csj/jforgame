package jforgame.demo.cross.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import jforgame.commons.TimeUtil;
import jforgame.socket.netty.NSession;

public class NSessionPlus extends NSession {

    private long lastWrittenTime = System.currentTimeMillis();

    public NSessionPlus(Channel session) {
        super(session);
    }

    public void send(Object packet) {
        ChannelFuture future = this.channel.writeAndFlush(packet);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                NSessionPlus.this.lastWrittenTime = System.currentTimeMillis();
            }
        });
    }

    public long getLastWriteTime() {
        return lastWrittenTime;
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        long diff = now - lastWrittenTime;
        return diff > 30 * TimeUtil.MILLIS_PER_SECOND;
    }

    public void sendMessage(Object message, Runnable sentCallback) {
        ChannelFuture future = this.channel.write(message);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                NSessionPlus.this.lastWrittenTime = System.currentTimeMillis();
                sentCallback.run();
            }
        });
    }
}
