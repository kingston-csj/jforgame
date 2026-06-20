package jforgame.socket.core.server;

/**
 * Service node, used to represent a node that provides services externally.
 * It can be a socket server node or an HTTP server node.
 */
public interface ServerNode {

    /**
     * Start the service node
     *
     * @throws Exception may be thrown when starting the service node
     */
    void start() throws Exception;

    /**
     * Shutdown the service node
     *
     * @throws Exception may be thrown when shutting down the service node
     */
    void shutdown() throws Exception;
}
