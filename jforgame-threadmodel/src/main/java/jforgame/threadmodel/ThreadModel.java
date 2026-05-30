package jforgame.threadmodel;


import jforgame.threadmodel.actor.ActorSystem;

/**
 * 系统任务的底层执行模型。
 * <p>
 * 该抽象只关心并发执行层面的基础问题，例如：
 * 任务如何排队、任务在哪个线程执行、如何关闭、如何避免并发冲突，
 * 以及如何基于 key 做 hash 分发或 actor 调度。
 * <p>
 * {@link ThreadModel} 不感知 socket、session、message 等业务语义，
 * 它只负责接收一个 {@link Runnable} 并按自身模型执行。
 * 上层若要表达“一个网络请求应该被投递到哪个执行单元”，
 * 应该使用 socket 模块中的请求调度抽象，而不是直接把业务路由规则塞进这里。
 * <p>
 * 常见实现：
 * {@link jforgame.threadmodel.dispatch.DispatchThreadModel} 适合按关键字分发到固定工作线程；
 * {@link ActorSystem} 适合通过 Actor 邮箱串行处理任务，减轻线程冷热不均问题。
 *
 * @since 3.0.0
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

    /**
     * 线程池是否已关闭
     *
     * @return true 如果线程池已关闭
     */
    boolean isShutdown();
}
