package jforgame.socket.share;

/**
 * 消息分发器
 * @author kinson
 */
public interface SocketIoDispatcher {

	/**
	 *
	 * @param session socket session
	 */
	void onSessionCreated(IdSession session);

	 /**
     * message entrance, in which io thread dispatch messages
     * @param session socket session
     * @param frame request message {@link jforgame.socket.share.message.RequestDataFrame}
     */
	void dispatch(IdSession session, Object frame);
	
	/**
	 * fire session close event
	 * @param session  socket session
	 */
	void onSessionClosed(IdSession session);


	void exceptionCaught(IdSession session, Throwable cause);
}
