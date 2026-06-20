package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Request scheduler, used to deliver client request messages to specific thread models.
 * @since v4.0.0
 */
@FunctionalInterface
public interface RequestScheduler {

    /**
     * Schedule request
     *
     * @param session socket session
     * @param context request context
     */
    void schedule(IdSession session, RequestContext context);
}
