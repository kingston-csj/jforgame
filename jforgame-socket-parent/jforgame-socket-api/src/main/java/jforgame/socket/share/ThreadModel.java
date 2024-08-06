package jforgame.socket.share;

import jforgame.socket.share.task.BaseGameTask;

/**
 * when nio socket server receive a full message from a client,
 * it will be wrapped to a task and passed to the {@link ThreadModel} to execute in independent thread.
 * By using an appropriate threading model, you can reduce the focus on thread concurrency issues and achieve better execution performance.
 * It's important to decide how to choose a dispatch key {@link BaseGameTask#getDispatchKey()}
 */
public interface ThreadModel {


    /**
     * Executes the given command asynchronously
     * task with the same {@link BaseGameTask#getDispatchKey()} will then dispatch to a same thread worker
     * @param task command task
     */
    void accept(BaseGameTask task);


    /**
     * shutdown the thread group, do not accept new task
     */
    void shutDown();
}
