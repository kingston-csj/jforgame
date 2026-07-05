package jforgame.socket.core.dispatch;

import jforgame.socket.core.support.ActorRequestScheduler;
import jforgame.socket.core.support.DispatchThreadRequestScheduler;
import jforgame.socket.core.support.SessionResponseSender;
import jforgame.threadmodel.actor.Actor;
import jforgame.threadmodel.actor.ActorSystem;
import jforgame.threadmodel.dispatch.DispatchThreadModel;

import java.util.function.Function;

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

    /**
     * <p>
     * 已绑定 Actor 的会话（如登录玩家）路由到解析结果，未绑定的会话
     * （如登录请求本身）落到 {@link ActorSystem#bindingSharedActor(long)} 兜底。
     *
     * @param actorSystem        Actor 系统，提供兜底共享 Actor
     * @param boundActorResolver 会话 → 绑定 Actor 的解析函数，返回 null 表示未绑定
     */
    public static RequestScheduler newActorScheduler(ActorSystem actorSystem,
                                                     Function<RequestContext, Actor> boundActorResolver,
                                                     RequestResponseSender responseSender,
                                                     RequestExecutionInterceptor... interceptors) {
        return new ActorRequestScheduler(new SessionBindingActorSelector(actorSystem, boundActorResolver),
                responseSender, interceptors);
    }

    public static RequestScheduler newActorScheduler(ActorSystem actorSystem,
                                                     Function<RequestContext, Actor> boundActorResolver,
                                                     RequestExecutionInterceptor... interceptors) {
        return newActorScheduler(actorSystem, boundActorResolver, SessionResponseSender.INSTANCE, interceptors);
    }
}
