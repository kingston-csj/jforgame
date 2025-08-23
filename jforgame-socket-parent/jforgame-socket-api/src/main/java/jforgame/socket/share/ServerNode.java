package jforgame.socket.share;

/**
 * 服务节点，用于表示一个对外提供服务的节点，可以是一个socket服务器节点，也可以是一个http服务器节点
 */
public interface ServerNode {

    /**
     * 启动服务节点
     *
     * @throws Exception 启动服务节点时可能抛出的异常
     */
    void start() throws Exception;

    /**
     * 关闭服务节点
     *
     * @throws Exception 关闭服务节点时可能抛出的异常
     */
    void shutdown() throws Exception;
}
