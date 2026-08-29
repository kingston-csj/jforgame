package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Unified socket IO event dispatcher, abstracts and shields underlying IO framework differences (Mina or
 * Netty).
 * All network events are wrapped into unified IdSession and RequestContext, decoupling upper business logic from MinaorNetty API.
 * Applicable for both server and client side.
 */
public interface SocketIoDispatcher {

    /**
     * Triggered when a new socket connection is established.
     * <p>
     * Runs on native IO thread of underlying framework (Netty/Mina IO thread).
     * Keep processing logic lightweight, avoid blocking operations.
     * </p>
     *
     * @param session unified abstract socket session
     */
    void onSessionCreated(IdSession session);

    /**
     * Dispatch received business message to internal MessageHandler filter chain.
     * <p>
     * This method runs on the network framework IO thread (Netty or Mina IO thread).
     * Do NOT execute heavy business logic here, wrap and dispatch tasks to separate business thread pool inside this method.
     * Blocking operations will severely degrade network throughput.
     * All underlying byte decoding and native message object are encapsulated inside RequestContext
     * </p>
     *
     * @param session unified abstract socket session, shield Mina IoSession or Netty Channel
     * @param context unified wrapped request message context
     */
    void dispatch(IdSession session, RequestContext context);

    /**
     * Triggered when socket connection is closed normally or abnormally.
     * <p>
     * Runs on native IO thread of underlying framework
     * Heavy resource cleanup logic should be offloaded to business threads.
     * </p>
     *
     * @param session unified abstract socket session
     */
    void onSessionClosed(IdSession session);

    /**
     * Triggered when read, write, decoding exception occurs on socket session.
     * <p>
     * Runs on native IO thread of underlying framework
     * Avoid synchronous heavy persistence or complex computation.
     * </p>
     *
     * @param session unified abstract socket session
     * @param cause   raw throwable from underlying Mina or Netty IO layer
     */
    void exceptionCaught(IdSession session, Throwable cause);
}
