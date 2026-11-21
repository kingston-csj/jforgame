package jforgame.socket.core.dispatch;

import jforgame.socket.core.execution.RequestProcessor;
import jforgame.socket.core.session.IdSession;

/**
 * Request scheduler that delivers preprocessed requests to a business thread model from the network IO thread.
 * <p>
 * Scheduling only selects the target Actor or shard, wraps the request in a task, and enqueues it.
 * These operations must remain lightweight and must not block the IO thread.
 * Once dequeued, the task invokes {@link RequestProcessor} on a business thread
 * to run interceptors, invoke the handler, send the response, and handle execution errors.
 * </p>
 * <p>
 * The scheduler determines where a request runs; the processor determines how it is processed.
 * Implementations must not execute the request processing flow directly inside
 * {@link #schedule(IdSession, RequestContext)}.
 * </p>
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
