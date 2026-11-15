package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * IO thread message filter interface, used for pre-filtering received socket messages.
 * Controls the execution chain by return value: return false to intercept and terminate subsequent processing flow.
 */
@FunctionalInterface
public interface IoMessageFilter {

    /**
     * Filter received socket message on network IO thread
     * <p>
     * This method runs strictly on the network framework IO thread (Netty/Mina IO thread).
     * Only lightweight check, route and dispatch logic is allowed here.
     * Any blocking IO, database query, heavy computation must be dispatched to business thread pool/Actor,
     * otherwise network read/write throughput will be severely blocked.
     * </p>
     *
     * @param session socket session wrapper
     * @param context unified request message context
     * @return true = pass current filter, execute next filter node in chain;
     * false = intercept this message, break the whole processing chain immediately
     * @throws Exception throwable during message pre-filter process
     */
    boolean filter(IdSession session, RequestContext context) throws Exception;

}