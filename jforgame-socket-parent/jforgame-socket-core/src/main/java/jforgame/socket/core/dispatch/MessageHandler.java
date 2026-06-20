package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Message handler interface for processing specific messages
 */
public interface MessageHandler {

    /**
     * Message handling method
     *
     * @param session socket session
     * @param context message handling context
     * @return true to continue to the next message handler node (if any); false to interrupt message execution
     * @throws Exception when handling message
     */
    boolean messageReceived(IdSession session, RequestContext context) throws Exception;

}
