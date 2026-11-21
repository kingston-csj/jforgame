package jforgame.socket.threadmodel.actor;

import jforgame.socket.core.execution.RequestProcessor;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.RequestScheduler;
import jforgame.threadmodel.actor.Actor;
import jforgame.threadmodel.actor.mail.Mail;

/**
 * Request scheduler based on the Actor model.
 * <p>
 * Selects the target Actor through {@link RequestActorSelector} and delivers mail on the IO thread.
 * Once dequeued, the mail invokes the injected {@link RequestProcessor} on the Actor's business thread.
 * </p>
 */
public class ActorRequestScheduler implements RequestScheduler {

    private final RequestActorSelector actorSelector;

    private final RequestProcessor processor;

    public ActorRequestScheduler(RequestActorSelector actorSelector,
                                 RequestProcessor processor) {
        this.actorSelector = actorSelector;
        this.processor = processor;
    }

    @Override
    public void schedule(IdSession session, RequestContext context) {
        Actor actor = actorSelector.select(context);
        if (actor == null) {
            throw new IllegalStateException("target actor is null");
        }
        actor.tell(new Mail() {
            @Override
            public void action() {
                processor.process(session, context);
            }
        });
    }
}
