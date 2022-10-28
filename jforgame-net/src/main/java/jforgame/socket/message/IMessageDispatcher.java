package jforgame.socket.message;

import jforgame.socket.IdSession;

/**
 * 消息分发器
 * @author kinson
 */
public interface IMessageDispatcher {
	
	void onSessionCreated(IdSession session);

	 /**
     * message entrance, in which io thread dispatch messages
     * @param session
     * @param message
     */
	void dispatch(IdSession session, Object message);
	
	/**
	 * fire session close event
	 * @param session
	 */
	void onSessionClosed(IdSession session);
}
