package jforgame.threadmodel;


import jforgame.threadmodel.actor.ActorSystem;

/**
 * 处理系统任务的线程模型
 * 当socket服务器接收来自客户端的消息包，
 * 会将消息包封装为一个任务，然后分配到具体的线程执行
 * 合理使用线程模型可以提高系统的并发性能，尽可能抑制并发的出现
 * 使用按关键字分发模型，可将不同的任务分发到不同的线程执行 {@link jforgame.threadmodel.dispatch.DispatchThreadModel}，
 * 但无论使用哪种hash算法，都无法避免线程“冷热不均”问题;
 * 使用actor模型，可有效避免线程“冷热不均”问题 {@link ActorSystem}
 */
public interface ThreadModel {

    /**
     * 接收新任务
     *
     * @param task command task
     */
    void accept(Runnable task);


    /**
     * 关闭线程模型，不接收新任务
     */
    void shutDown();
}
