package jforgame.demo.utils;

import java.util.concurrent.atomic.AtomicLong;

import jforgame.demo.ServerConfig;

/**
 * 分布式id生成器
 */
public class IdGenerator {

    private static AtomicLong generator = new AtomicLong(0);

    /**
     * 生成全局唯一id
     */
    public static long getNextId() {
        //----------------id格式 -------------------------
        //----------long类型8个字节64个比特位----------------
        // 高16位          	| 中32位          |  低16位
        // serverId        系统秒数          自增长号

        long serverId = ServerConfig.getInstance().getServerId();
        return (serverId << 48)
                | (((System.currentTimeMillis() / 1000) & 0xFFFFFFFF) << 16)
                | (generator.getAndIncrement() & 0xFFFF);
    }

}
