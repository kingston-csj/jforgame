package jforgame.server.cross.core.server;

import jforgame.server.cross.core.client.CCSession;
import jforgame.socket.message.Message;

public interface CMessageDispatcher {

	/**
	 * 服务端节点消息分发
	 * @param session
	 * @param message
	 */
	void serverDispatch(SCSession session, Message message);
	
	/**
	 * 服务端节点消息分发
	 * @param session
	 * @param message
	 */
	void clientDispatch(CCSession session, Message message);
	
}