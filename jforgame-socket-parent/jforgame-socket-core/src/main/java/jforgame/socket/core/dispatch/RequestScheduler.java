package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Request scheduler, used to deliver client request messages to specific business thread models.
 * This interface wraps request into independent task entity on IO thread and enqueues it;
 * real business logic runs in isolated business threads(Actor/sharded thread pool).
 *
 * @since v4.0.0
 */
@FunctionalInterface
public interface RequestScheduler {

    /**
     * Wrap request data into business task object, then enqueue task to dedicated business thread model.
     * <p>
     * Invoked on network IO thread, only performs lightweight task encapsulation and enqueue operation,
     * returns instantly without any blocking.
     * All heavy logic including reflection method invoke, database access, business computation
     * will be executed later on independent business threads via wrapped task action.
     * </p>
     *
     * @param session socket session wrapper
     * @param context unified request message context
     */
    void schedule(IdSession session, RequestContext context);
}