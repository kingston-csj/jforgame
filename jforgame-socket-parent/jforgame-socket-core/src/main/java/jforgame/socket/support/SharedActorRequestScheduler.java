package jforgame.socket.support;

import jforgame.socket.dispatch.RequestDispatchKeyResolver;
import jforgame.threadmodel.actor.ActorSystem;

/**
 * 基于共享 Actor 的请求调度器。
 * 适用于没有稳定业务 Actor 时，按分发键路由到共享 Actor。
 */
public class SharedActorRequestScheduler extends ActorRequestScheduler {

    public SharedActorRequestScheduler(ActorSystem actorSystem, RequestDispatchKeyResolver dispatchKeyResolver) {
        super((session, context) -> actorSystem.bindingSharedActor(dispatchKeyResolver.resolve(session, context)));
    }
}
