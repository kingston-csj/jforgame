package jforgame.socket.support;

import jforgame.socket.session.IdSession;
import jforgame.socket.dispatch.RequestActorSelector;
import jforgame.socket.dispatch.RequestContext;
import jforgame.socket.dispatch.RequestExecutionInterceptor;
import jforgame.socket.dispatch.RequestResponseSender;
import jforgame.socket.dispatch.RequestScheduler;
import jforgame.threadmodel.actor.Actor;
import jforgame.threadmodel.actor.mail.Mail;

/**
 * 基于 Actor 的请求调度器，由业务方决定将请求投递到哪个 Actor。
 */
public class ActorRequestScheduler implements RequestScheduler {

    private final RequestActorSelector actorSelector;

    private final RequestExecutionSupport requestExecutionSupport;

    public ActorRequestScheduler(RequestActorSelector actorSelector, RequestExecutionInterceptor... interceptors) {
        this(actorSelector, SessionResponseSender.INSTANCE, interceptors);
    }

    public ActorRequestScheduler(RequestActorSelector actorSelector,
                                 RequestResponseSender responseSender,
                                 RequestExecutionInterceptor... interceptors) {
        this.actorSelector = actorSelector;
        this.requestExecutionSupport = new RequestExecutionSupport(responseSender, interceptors);
    }

    @Override
    public void schedule(IdSession session, RequestContext context) {
        Actor actor = actorSelector.select(session, context);
        if (actor == null) {
            throw new IllegalStateException("target actor is null");
        }
        actor.tell(new Mail() {
            @Override
            public void action() {
                requestExecutionSupport.execute(session, context);
            }
        });
    }
}
