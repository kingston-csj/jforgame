package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;
import jforgame.threadmodel.actor.Actor;

/**
 * 请求 Actor 选择器。
 * 用于根据会话和请求上下文选择最终投递的目标 Actor。
 * @since 4.0.0
 */
@FunctionalInterface
public interface RequestActorSelector {

    /**
     * 选择目标 Actor
     *
     * @param session socket session
     * @param context 请求上下文
     * @return 目标 Actor
     */
    Actor select(IdSession session, RequestContext context);
}
