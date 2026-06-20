package jforgame.threadmodel;


import jforgame.threadmodel.actor.ActorSystem;

/**
 * System task underlying execution model.
 * <p>
 * This abstraction only concerns basic concurrency issues such as:
 * task queuing, which thread executes tasks, graceful shutdown, concurrency conflict avoidance,
 * and how to do hash-based dispatch or actor scheduling by key.
 * <p>
 * {@link ThreadModel} does not understand business semantics like socket, session, message, etc.
 * It only receives a {@link Runnable} and executes it according to its own model.
 * Upper layers wanting to express "which execution unit a network request should be dispatched to"
 * should use request dispatch abstractions in the socket module, not directly embedding business routing rules here.
 * <p>
 * Common implementations:
 * {@link jforgame.threadmodel.dispatch.DispatchThreadModel} is suitable for dispatching to fixed worker threads by keyword;
 * {@link ActorSystem} is suitable for serial task processing through Actor mailbox, reducing thread hot-cold imbalance.
 *
 * @since 3.0.0
 */
public interface ThreadModel {

    /**
     * Accept new task
     *
     * @param task command task
     */
    void accept(Runnable task);


    /**
     * Shutdown the thread model, no longer accepts new tasks
     */
    void shutDown();

    /**
     * Check if the thread pool is shutdown
     *
     * @return true if the thread pool is shutdown
     */
    boolean isShutdown();
}
