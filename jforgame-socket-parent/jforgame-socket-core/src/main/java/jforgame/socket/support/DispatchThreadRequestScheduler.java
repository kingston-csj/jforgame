package jforgame.socket.support;

import jforgame.socket.session.IdSession;
import jforgame.socket.dispatch.RequestContext;
import jforgame.socket.dispatch.RequestDispatchKeyResolver;
import jforgame.socket.dispatch.RequestScheduler;
import jforgame.threadmodel.dispatch.DispatchThreadModel;

/**
 * 基于 DispatchThreadModel 的请求调度器。
 */
public class DispatchThreadRequestScheduler implements RequestScheduler {

    private final DispatchThreadModel threadModel;

    private final RequestDispatchKeyResolver dispatchKeyResolver;

    public DispatchThreadRequestScheduler(DispatchThreadModel threadModel, RequestDispatchKeyResolver dispatchKeyResolver) {
        this.threadModel = threadModel;
        this.dispatchKeyResolver = dispatchKeyResolver;
    }

    @Override
    public void schedule(IdSession session, RequestContext context) {
        long dispatchKey = dispatchKeyResolver.resolve(session, context);
        threadModel.accept(ClientRequestTask.valueOf(session, dispatchKey, context));
    }
}
