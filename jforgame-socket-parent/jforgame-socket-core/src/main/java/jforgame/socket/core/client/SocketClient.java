package jforgame.socket.core.client;

import jforgame.socket.core.session.IdSession;

import java.io.IOException;

/**
 * Socket client interface, used to connect to server
 */
public interface SocketClient {

    /**
     * Opens a session, this method blocks until connection is successful, ensuring the returned IdSession is usable
     * @return session
     * @throws IOException connection failed
     */
    IdSession openSession() throws IOException;

    /**
     * Closes the session
     * @throws IOException close failed
     */
    void close() throws IOException;

    /**
     * Gets the session
     * @return session
     */
    IdSession getSession();

}
