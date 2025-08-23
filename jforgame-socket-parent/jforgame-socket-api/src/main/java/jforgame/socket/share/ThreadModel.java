package jforgame.socket.share;

import jforgame.socket.share.task.BaseGameTask;

/**
 * 处理系统任务的线程模型
 * 当socket服务器接收来自客户端的消息包，
 * 会将消息包封装为一个任务，然后分配到具体的线程执行
 * 合理使用线程模型可以提高系统的并发性能，尽可能抑制并发的出现
 *
 * @author kinson
 */
public interface ThreadModel {

    /**
     * 接收新任务
     *
     * @param task command task
     */
    void accept(BaseGameTask task);


    /**
     * 关闭线程模型，不接受新任务
     */
    void shutDown();
}
