package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;
import jforgame.threadmodel.actor.Actor;

/**
 * Request Actor selector.
 * Used to select the target Actor for delivery based on session and request context.
 * @since 4.0.0
 */
@FunctionalInterface
public interface RequestActorSelector {

    /**
     * Select target Actor
     *
     * @param session socket session
     * @param context request context
     * @return target Actor
     */
    Actor select(IdSession session, RequestContext context);
}
