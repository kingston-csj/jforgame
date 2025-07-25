package jforgame.socket.client;

import jforgame.socket.share.IdSession;

import java.io.IOException;

/**
 * socket客户端接口，用于连接服务器
 */
public interface SocketClient {

    IdSession openSession() throws IOException;

    void close() throws IOException;

    IdSession getSession();

}
