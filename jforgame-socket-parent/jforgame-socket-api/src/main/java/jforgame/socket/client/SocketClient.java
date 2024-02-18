package jforgame.socket.client;

import jforgame.socket.share.IdSession;

public interface SocketClient {

    IdSession openSession() throws Exception;

    void close() throws Exception;

    IdSession getSession();

}
