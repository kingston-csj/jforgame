package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Request response sender.
 * When a routed method has a return value, the framework automatically sends this return value as a response to the client (including index field).
 * When the return value is void, the framework ignores the return result.
 * Used to define how the framework should send results to the client or gateway after a routed method returns a response object.
 * When logic server uses direct client connection mode, directly use {@link IdSession#send(int,Object)};
 * If logic server uses gateway mode, need to use a sending method that can adapt to the gateway, such as wrapping into a forward message with actual content.
 * @since 4.0.0
 */
@FunctionalInterface
public interface RequestResponseSender {

    /**
     * Send response
     * @see RequestContext#getRequest()
     * @param session        socket session
     * @param requestContext request context
     */
    void send(IdSession session, RequestContext requestContext);
}
