package jforgame.socket.threadmodel.dispatch;

import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.RequestScheduler;
import jforgame.socket.core.execution.RequestProcessor;
import jforgame.socket.core.session.IdSession;
import jforgame.threadmodel.dispatch.BaseDispatchTask;
import jforgame.threadmodel.dispatch.DispatchThreadModel;

/**
 * Request scheduler based on a sharded thread model.
 * <p>
 * Resolves the dispatch key, wraps the request in a task, and submits it to {@link DispatchThreadModel}
 * on the IO thread. Once dequeued, the task invokes the injected {@link RequestProcessor}
 * on the corresponding shard's business thread.
 * </p>
 */
public class DispatchThreadRequestScheduler implements RequestScheduler {

    private final DispatchThreadModel threadModel;

    private final RequestDispatchKeyResolver dispatchKeyResolver;

    private final RequestProcessor processor;

    public DispatchThreadRequestScheduler(DispatchThreadModel threadModel,
                                          RequestDispatchKeyResolver dispatchKeyResolver,
                                          RequestProcessor processor) {
        this.threadModel = threadModel;
        this.dispatchKeyResolver = dispatchKeyResolver;
        this.processor = processor;
    }

    @Override
    public void schedule(IdSession session, RequestContext context) {
        final long dispatchKey = dispatchKeyResolver.resolve(context);
        BaseDispatchTask task = new BaseDispatchTask() {
            @Override
            public void action() {
                processor.process(session, context);
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
