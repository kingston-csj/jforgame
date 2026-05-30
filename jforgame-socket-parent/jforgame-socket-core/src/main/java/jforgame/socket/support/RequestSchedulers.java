package jforgame.socket.support;

import jforgame.socket.dispatch.RequestActorSelector;
import jforgame.socket.dispatch.RequestDispatchKeyResolver;
import jforgame.socket.dispatch.RequestScheduler;
import jforgame.threadmodel.actor.ActorSystem;
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
 * 或者退化为按 key 路由到共享 Actor。
 */
public final class RequestSchedulers {

    private RequestSchedulers() {
    }

    public static RequestScheduler dispatch(DispatchThreadModel threadModel, RequestDispatchKeyResolver dispatchKeyResolver) {
        return new DispatchThreadRequestScheduler(threadModel, dispatchKeyResolver);
    }

    public static RequestScheduler actor(RequestActorSelector actorSelector) {
        return new ActorRequestScheduler(actorSelector);
    }

    public static RequestScheduler sharedActor(ActorSystem actorSystem, RequestDispatchKeyResolver dispatchKeyResolver) {
        return new SharedActorRequestScheduler(actorSystem, dispatchKeyResolver);
    }
}
