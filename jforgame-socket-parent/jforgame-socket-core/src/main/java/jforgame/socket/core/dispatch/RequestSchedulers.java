package jforgame.socket.core.dispatch;

import jforgame.socket.core.support.ActorRequestScheduler;
import jforgame.socket.core.support.DispatchThreadRequestScheduler;
import jforgame.socket.core.support.SessionResponseSender;
import jforgame.threadmodel.dispatch.DispatchThreadModel;

/**
 * Common request scheduler factory.
 * <p>
 * This layer belongs to the socket request processing flow, responsible for mapping request semantics like {@code IdSession + RequestContext}
 * to specific execution carriers.
 * <p>
 * Compared with {@code ThreadModel}, it doesn't care about how the thread pool queues or shuts down,
 * but cares about "where should this request be sent":
 * whether to enter {@link DispatchThreadModel} by dispatch key,
 * or select target Actor according to business rules.
 * @since 4.0.0
 */
public final class RequestSchedulers {

    private RequestSchedulers() {
    }

    public static RequestScheduler newDispatchScheduler(DispatchThreadModel threadModel,
                                                        RequestDispatchKeyResolver dispatchKeyResolver) {
        return newDispatchScheduler(threadModel, dispatchKeyResolver, SessionResponseSender.INSTANCE);
    }

    public static RequestScheduler newDispatchScheduler(DispatchThreadModel threadModel,
                                                        RequestDispatchKeyResolver dispatchKeyResolver,
                                                        RequestExecutionInterceptor... interceptors) {
        return newDispatchScheduler(threadModel, dispatchKeyResolver, SessionResponseSender.INSTANCE, interceptors);
    }

    public static RequestScheduler newDispatchScheduler(DispatchThreadModel threadModel,
                                                        RequestDispatchKeyResolver dispatchKeyResolver,
                                                        RequestResponseSender responseSender,
                                                        RequestExecutionInterceptor... interceptors) {
        return new DispatchThreadRequestScheduler(threadModel, dispatchKeyResolver, responseSender, interceptors);
    }

    public static RequestScheduler newActorScheduler(RequestActorSelector actorSelector) {
        return newActorScheduler(actorSelector, SessionResponseSender.INSTANCE);
    }

    public static RequestScheduler newActorScheduler(RequestActorSelector actorSelector,
                                                     RequestExecutionInterceptor... interceptors) {
        return newActorScheduler(actorSelector, SessionResponseSender.INSTANCE, interceptors);
    }

    public static RequestScheduler newActorScheduler(RequestActorSelector actorSelector,
                                                     RequestResponseSender responseSender,
                                                     RequestExecutionInterceptor... interceptors) {
        return new ActorRequestScheduler(actorSelector, responseSender, interceptors);
    }
}
