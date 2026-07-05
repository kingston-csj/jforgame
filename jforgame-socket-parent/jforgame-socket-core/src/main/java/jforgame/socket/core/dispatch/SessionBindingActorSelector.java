package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;
import jforgame.threadmodel.actor.Actor;
import jforgame.threadmodel.actor.ActorSystem;

import java.util.function.Function;

/**
 * 会话绑定型请求 Actor 选择器。
 * <p>
 * 典型场景：已登录会话的消息路由到玩家专属 Actor，未绑定（如登录请求本身）
 * 的会话消息落到 {@link ActorSystem#bindingSharedActor(long)} 提供的共享 Actor，
 * 既保证线程安全又避免无主请求无 Actor 可投。
 * <p>
 *
 * @since 4.0.0
 */
public class SessionBindingActorSelector implements RequestActorSelector {

    private final ActorSystem actorSystem;

    private final Function<RequestContext, Actor> boundActorResolver;

    /**
     * @param actorSystem        Actor 系统，用于在会话未绑定 Actor 时提供兜底共享 Actor
     * @param boundActorResolver 会话 → 绑定 Actor 的解析函数，返回 null 表示未绑定
     */
    public SessionBindingActorSelector(ActorSystem actorSystem,
                                       Function<RequestContext, Actor> boundActorResolver) {
        this.actorSystem = actorSystem;
        this.boundActorResolver = boundActorResolver;
    }

    @Override
    public Actor select(RequestContext context) {
        Actor bound = boundActorResolver.apply(context);
        if (bound != null) {
            return bound;
        }
        IdSession session = context.getSession();
        // 未绑定会话（如登录请求）落到共享 Actor，保证线程安全
        return actorSystem.bindingSharedActor(session.hashCode());
    }
}
