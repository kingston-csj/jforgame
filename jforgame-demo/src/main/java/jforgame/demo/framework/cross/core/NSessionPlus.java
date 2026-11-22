package jforgame.demo.framework.cross.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import jforgame.commons.util.TimeUtil;
import jforgame.socket.core.protocol.message.SocketDataFrame;
import jforgame.socket.netty.NSession;

public class NSessionPlus extends NSession {
    // 静态复用监听器，全局只实例化一次，无频繁GC分配

    private static final AttributeKey<NSessionPlus> SESSION_PLUS_KEY = AttributeKey.valueOf("NSessionPlus");
    private static final ChannelFutureListener WRITE_SUCCESS_LISTENER = future -> {
        // 仅TCP缓冲区写入成功才更新时间，失败/断连直接跳过
        if (future.isSuccess()) {
            NSessionPlus sessionPlus = (NSessionPlus) future.channel().attr(SESSION_PLUS_KEY).get();
            if (sessionPlus != null) {
                sessionPlus.lastWrittenTime = System.currentTimeMillis();
            }
        }
    };

    private volatile long lastWrittenTime;

    public NSessionPlus(Channel channel) {
        super(channel);
        this.lastWrittenTime = System.currentTimeMillis();
        // 将当前会话存入Channel附件，供静态Listener获取实例
        channel.attr(SESSION_PLUS_KEY).set(this);
    }

    @Override
    public void send(Object packet) {
        // 复用父类统一封包逻辑，和基础NSession行为对齐
        Object frame;
        if (packet instanceof SocketDataFrame) {
            frame = packet;
        } else {
            frame = SocketDataFrame.withoutIndex(packet);
        }
        // 绑定复用的静态监听器
        this.channel.writeAndFlush(frame).addListener(WRITE_SUCCESS_LISTENER);
    }

    public long getLastWriteTime() {
        return lastWrittenTime;
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        long diff = now - lastWrittenTime;
        return diff > 30 * TimeUtil.MILLIS_PER_SECOND;
    }

}
