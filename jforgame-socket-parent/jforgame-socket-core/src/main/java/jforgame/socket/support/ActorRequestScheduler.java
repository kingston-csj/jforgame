package jforgame.socket.support;

import jforgame.socket.session.IdSession;
import jforgame.socket.dispatch.RequestActorSelector;
import jforgame.socket.dispatch.RequestContext;
import jforgame.socket.dispatch.RequestScheduler;
import jforgame.threadmodel.actor.Actor;

/**
 * 基于 Actor 的请求调度器，由业务方决定将请求投递到哪个 Actor。
 */
public class ActorRequestScheduler implements RequestScheduler {

    private final RequestActorSelector actorSelector;

    public ActorRequestScheduler(RequestActorSelector actorSelector) {
        this.actorSelector = actorSelector;
    }

    @Override
    public void schedule(IdSession session, RequestContext context) {
        Actor actor = actorSelector.select(session, context);
        if (actor == null) {
            throw new IllegalStateException("target actor is null");
        }
        actor.tell(ClientRequestMail.valueOf(session, context));
    }
}
