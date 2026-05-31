package jforgame.socket.dispatch;

import jforgame.socket.support.ActorRequestScheduler;
import jforgame.socket.support.DispatchThreadRequestScheduler;
import jforgame.socket.support.SessionResponseSender;
import jforgame.threadmodel.dispatch.DispatchThreadModel;

/**
 * 常用请求调度器工厂。
 * <p>
 * 这一层属于 socket 请求处理流程，负责把 {@code IdSession + RequestContext}
 * 这样的请求语义映射到具体执行载体。
 * <p>
 * 与 {@code ThreadModel} 相比，它不关心线程池如何排队或如何关闭，
 * 而是关心“这个请求该投到哪”：
 * 是按分发键进入 {@link DispatchThreadModel}，
 * 还是根据业务规则选择目标 Actor，
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
