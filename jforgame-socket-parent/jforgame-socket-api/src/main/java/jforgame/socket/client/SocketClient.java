package jforgame.socket.client;

import jforgame.socket.share.IdSession;

import java.io.IOException;

/**
 * socket客户端接口，用于连接服务器
 */
public interface SocketClient {

    /**
     * 打开一个会话
     * @return 会话
     * @throws IOException 连接失败
     */
    IdSession openSession() throws IOException;

    /**
     * 关闭会话
     * @throws IOException 关闭失败
     */
    void close() throws IOException;

    /**
     * 获取会话
     * @return 会话
     */
    IdSession getSession();

}
