package jforgame.socket.core.support;

import jforgame.socket.core.dispatch.RequestContext;
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
     * @param context request context
     * @return target Actor
     */
    Actor select(RequestContext context);
}
