package jforgame.socket.share;

/**
 * 请求分发键解析器。
 * 用于从会话和请求上下文中提取线程模型需要的路由键。
 */
@FunctionalInterface
public interface RequestDispatchKeyResolver {

    /**
     * 解析分发键
     *
     * @param session socket session
     * @param context 请求上下文
     * @return 分发键
     */
    long resolve(IdSession session, RequestContext context);
}
