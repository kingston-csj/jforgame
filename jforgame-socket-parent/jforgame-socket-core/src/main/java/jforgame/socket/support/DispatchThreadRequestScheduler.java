package jforgame.socket.support;

import jforgame.socket.session.IdSession;
import jforgame.socket.dispatch.RequestContext;
import jforgame.socket.dispatch.RequestDispatchKeyResolver;
import jforgame.socket.dispatch.RequestExecutionInterceptor;
import jforgame.socket.dispatch.RequestResponseSender;
import jforgame.socket.dispatch.RequestScheduler;
import jforgame.threadmodel.dispatch.DispatchThreadModel;
import jforgame.threadmodel.dispatch.BaseDispatchTask;

/**
 * 基于 DispatchThreadModel 的请求调度器。
 */
public class DispatchThreadRequestScheduler implements RequestScheduler {

    private final DispatchThreadModel threadModel;

    private final RequestDispatchKeyResolver dispatchKeyResolver;

    private final RequestExecutionSupport requestExecutionSupport;

    public DispatchThreadRequestScheduler(DispatchThreadModel threadModel,
                                          RequestDispatchKeyResolver dispatchKeyResolver,
                                          RequestExecutionInterceptor... interceptors) {
        this(threadModel, dispatchKeyResolver, SessionResponseSender.INSTANCE, interceptors);
    }

    public DispatchThreadRequestScheduler(DispatchThreadModel threadModel,
                                          RequestDispatchKeyResolver dispatchKeyResolver,
                                          RequestResponseSender responseSender,
                                          RequestExecutionInterceptor... interceptors) {
        this.threadModel = threadModel;
        this.dispatchKeyResolver = dispatchKeyResolver;
        this.requestExecutionSupport = new RequestExecutionSupport(responseSender, interceptors);
    }

    @Override
    public void schedule(IdSession session, RequestContext context) {
        final long dispatchKey = dispatchKeyResolver.resolve(session, context);
        BaseDispatchTask task = new BaseDispatchTask() {
            @Override
            public void action() {
                requestExecutionSupport.execute(session, context);
            }

            @Override
            public String toString() {
                return "[" + context.getMethodExecutor().getHandler().getClass().getName()
                        + "@" + context.getMethodExecutor().getMethod().getName() + "]";
            }
        };

        task.setDispatchKey(dispatchKey);
        threadModel.accept(task);
    }
}
