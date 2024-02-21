package jforgame.socket.share;

/**
 * 消息分发器
 * @author kinson
 */
public interface SocketIoDispatcher {

	/**
	 *
	 * @param session
	 */
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


	void exceptionCaught(IdSession session, Throwable cause);
}
