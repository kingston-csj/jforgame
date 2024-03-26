package jforgame.socket.share;

import jforgame.socket.share.task.BaseGameTask;

/**
 * when nio socket server receive a full message from client,
 * it will be wrapped to a task and passed to the {@link ThreadModel} to execute
 * in independent thread
 */
public interface ThreadModel {


    /**
     * Executes the given command at some time in the future
     * @param task command task
     */
    void accept(BaseGameTask task);


    void shutDown();
}
